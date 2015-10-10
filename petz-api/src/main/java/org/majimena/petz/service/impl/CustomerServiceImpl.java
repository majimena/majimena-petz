package org.majimena.petz.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.customer.CustomerAuthorizationToken;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.user.SignupRegistry;
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
    public Customer authorize(CustomerAuthorizationToken token) throws ApplicationException {
        // 関連情報を取得する
        Clinic clinic = clinicRepository.findOne(token.getClinicId());
        User user = userRepository.findOne(token.getUserId());

        // 既に連絡先が登録されており、それを流用する場合は電話番号をキーに認証する
        String phone = token.getPhoneNo();
        if (StringUtils.isNotEmpty(user.getPhoneNo()) &&
            !(StringUtils.equals(user.getPhoneNo(), phone) || StringUtils.equals(user.getMobilePhoneNo(), phone))) {
            throw new ApplicationException(ErrorCode.PTZ_000201);
        }

        // 登録情報と合っていればクリニック顧客として追加（但し、この時点ではアクティベートしていない）
        Customer customer = customerRepository.findByClinicIdAndUserId(token.getClinicId(), token.getUserId())
            .map(u -> {
                u.setClinic(clinic);
                u.setUser(user);
                return customerRepository.save(u);
            })
            .orElseGet(() -> customerRepository.save(Customer.builder()
                .clinic(clinic).user(user).activated(false).blocked(false).build()));
        return customer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer saveCustomer(String clinicId, Customer customer) throws ApplicationException {
        User user;
        // ユーザーが既にいるなら更新し、そうでなければ新規でアカウントを作成する
        if (StringUtils.isNotEmpty(customer.getUser().getId())) {
            user = userService.patchUser(customer.getUser());
        } else {
            // FIXME 海外対応した場合は考える
            customer.getUser().setUsername(customer.getLastName() + " " + customer.getFirstName());
            user = userService.saveUser(customer.getUser());
        }

        // 顧客とするクリニックを取得する
        Clinic clinic = clinicRepository.getOne(clinicId);

        // 顧客情報を更新してアクティベートする
        customer.setUser(user);
        customer.setClinic(clinic);
        customer.setActivated(Boolean.TRUE);
        Customer created = customerRepository.save(customer);

        // TODO 顧客に登録完了のメールを送信
        return created;
    }
}
