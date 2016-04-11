package org.majimena.petical.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.common.utils.BeanFactoryUtils;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.common.utils.RandomUtils;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.customer.CustomerAuthenticationToken;
import org.majimena.petical.domain.customer.CustomerCriteria;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.repository.spec.CustomerSpecs;
import org.majimena.petical.service.CustomerService;
import org.majimena.petical.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    @Deprecated
    @Override
    @Transactional(readOnly = true)
    public Page<Customer> getCustomersByCustomerCriteria(CustomerCriteria criteria, Pageable pageable) {
        return customerRepository.findAll(new CustomerSpecs(criteria), pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Customer> getCustomersByClinicId(String clinicId) {
        return customerRepository.findAll(CustomerSpecs.of(clinicId), CustomerSpecs.asc());
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
                            .removed(Boolean.FALSE)
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
        User user = customer.getUser();
        // ユーザーが既にいるなら更新し、そうでなければ新規でアカウントを作成する
        if (StringUtils.isNotEmpty(user.getId())) {
            saved = userService.updateUser(user);
        } else {
            // ログインIDとメールアドレスの空対応
            if (StringUtils.isEmpty(user.getLogin())) {
                String email = user.getEmail();
                if (StringUtils.isEmpty(email)) {
                    email = System.nanoTime() + "@petical.io";
                }
                user.setLogin(email);
                user.setPassword(RandomUtils.generatePassword());
            } else if (StringUtils.isEmpty(user.getEmail())) {
                user.setEmail(user.getLogin());
            }

            // FIXME 海外対応した場合は別途考えること
            user.setUsername(user.getLastName() + " " + user.getFirstName());
            user.setPassword(RandomUtils.generatePassword());
            user.setLangKey(LangKey.JAPANESE);
            user.setTimeZone(TimeZone.ASIA_TOKYO);
            saved = userService.saveUser(user);
        }

        // 顧客とするクリニックを取得する
        Clinic clinic = clinicRepository.getOne(clinicId);

        // 顧客情報を更新してアクティベートする
        customer.setUser(saved);
        customer.setClinic(clinic);
        customer.setRemoved(Boolean.FALSE);
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
        // 紐付けだけ外せば良いので、飼い主情報を物理削除する
        customerRepository.delete(customer);
    }
}
