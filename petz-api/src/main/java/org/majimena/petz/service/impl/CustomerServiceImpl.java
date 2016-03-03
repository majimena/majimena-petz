package org.majimena.petz.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.common.factory.BeanFactory;
import org.majimena.petz.common.utils.BeanFactoryUtils;
import org.majimena.petz.common.utils.ExceptionUtils;
import org.majimena.petz.common.utils.RandomUtils;
import org.majimena.petz.datatype.LangKey;
import org.majimena.petz.datatype.TimeZone;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.customer.CustomerAuthenticationToken;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.repository.spec.CustomerCriteriaSpec;
import org.majimena.petz.service.CustomerService;
import org.majimena.petz.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

/**
 * 顧客サービスの実装.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Inject
    private ClinicRepository clinicRepository;

    @Inject
    private CustomerRepository customerRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Customer> getCustomersByCustomerCriteria(CustomerCriteria criteria, Pageable pageable) {
        return customerRepository.findAll(new CustomerCriteriaSpec(criteria), pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByCustomerId(String customerId) {
        Customer one = customerRepository.findOne(customerId);
        return Optional.ofNullable(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer saveCustomer(CustomerAuthenticationToken token) throws ApplicationException {
        // 指定クリニックと指定ユーザーを紐付けて顧客登録する
        Clinic clinic = clinicRepository.findOne(token.getClinicId());
        return userRepository.findOneByLogin(token.getLogin())
                .map(user -> {
                    Customer customer = Customer.builder()
                            .clinic(clinic)
                            .user(user)
                            .activated(Boolean.FALSE)
                            .blocked(Boolean.FALSE)
                            .build();
                    return customerRepository.save(customer);
                })
                .orElseThrow(() -> new ApplicationException(ErrorCode.PTZ_000201));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer saveCustomer(String clinicId, Customer customer) throws ApplicationException {
        User saved;
        // ユーザーが既にいるなら更新し、そうでなければ新規でアカウントを作成する
        if (StringUtils.isNotEmpty(customer.getUser().getId())) {
            saved = userService.updateUser(customer.getUser());
        } else {
            // FIXME 海外対応した場合は別途考えること
            User user = customer.getUser();
            user.setUsername(user.getLastName() + " " + user.getFirstName());
            user.setPassword(RandomUtils.generatePassword());
            user.setLangKey(LangKey.JAPANESE);
            user.setTimeZone(TimeZone.ASIA_TOKYO);
            saved = userService.saveUser(customer.getUser());
        }

        // 顧客とするクリニックを取得する
        Clinic clinic = clinicRepository.getOne(clinicId);

        // 顧客情報を更新してアクティベートする
        customer.setUser(saved);
        customer.setClinic(clinic);
        customer.setActivated(Boolean.TRUE);
        Customer created = customerRepository.save(customer);

        // TODO 顧客に登録完了のメールを送信
        return created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer updateCustomer(Customer customer) throws ApplicationException {
        // 更新対象データを取得する
        Customer one = customerRepository.findOne(customer.getId());
        User user = one.getUser();
        ExceptionUtils.throwIfNull(one);

        // ユーザー情報を更新する
        BeanFactoryUtils.copyNonNullProperties(customer.getUser(), user);
        userService.updateUser(user);

        // データをコピーして保存する
        BeanFactoryUtils.copyNonNullProperties(customer, one);
        return customerRepository.save(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCustomer(Customer customer) {
    }
}
