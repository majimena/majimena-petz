package org.majimena.petical.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.common.utils.BeanFactoryUtils;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.common.utils.RandomUtils;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.domain.Authority;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.customer.CustomerAuthenticationToken;
import org.majimena.petical.domain.customer.CustomerCriteria;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.AuthorityRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.repository.spec.CustomerSpecs;
import org.majimena.petical.service.CustomerService;
import org.majimena.petical.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private AuthorityRepository authorityRepository;

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
        return Optional.ofNullable(one)
                .map(customer -> {
                    customer.getUser().getId();
                    return customer;
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer saveCustomer(CustomerAuthenticationToken token) throws ApplicationException {
        // 指定クリニックと指定ユーザーを紐付けて顧客登録する
        Clinic clinic = clinicRepository.findOne(token.getClinicId());
        return userRepository.findOneByActivatedIsTrueAndLogin(token.getLogin())
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

    @Override
    public Customer createCustomerAndUser(Customer customer) throws ApplicationException {
        // ロールを作成する
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        // メールアドレスがない場合でも無条件に基本ユーザーを登録する（その場合、勝手に作成するユーザなのでログインできないユーザーとなる）
        User user = customer.getUser();
        user.setUsername("Auto Generated");
        if (StringUtils.isEmpty(user.getEmail())) {
            user.setLogin(System.nanoTime() + "@petical.io"); // ログインIDは勝手に生成
        }
        if (StringUtils.isEmpty(user.getLogin())) {
            user.setLogin(user.getEmail()); // ログインIDの指定がないときだけメールアドレスをログインIDとして使う
        }
        user.setPassword("not-activated-user-password");
        user.setCountry("JP");
        user.setLangKey(LangKey.JAPANESE);
        user.setTimeZone(TimeZone.ASIA_TOKYO);
        user.setActivated(false);
        user.setActivationKey(RandomUtils.generateActivationKey());
        user.setAuthorities(authorities);
        User saved = userRepository.save(user);

        // 飼い主として登録するクリニックを取得する
        Clinic clinic = clinicRepository.getOne(customer.getClinic().getId());

        // 顧客情報を更新してアクティベートする
        customer.setUser(saved);
        customer.setClinic(clinic);
        customer.setRemoved(Boolean.FALSE);
        return customerRepository.save(customer);
        // TODO メールアドレスがある場合は飼い主にメール通知した方が良い
    }

    @Override
    public Customer mergeCustomer(User user, Customer customer) throws ApplicationException {
        // 飼い主として登録するクリニックを取得する
        Clinic clinic = clinicRepository.getOne(customer.getClinic().getId());

        // ユーザー情報が足りない場合があるので必要に応じて追記して更新する
        user.setFirstName(StringUtils.defaultString(user.getFirstName(), customer.getUser().getFirstName()));
        user.setLastName(StringUtils.defaultString(user.getLastName(), customer.getUser().getLastName()));
        user.setFirstNameKana(StringUtils.defaultString(user.getFirstNameKana(), customer.getUser().getFirstNameKana()));
        user.setLastNameKana(StringUtils.defaultString(user.getLastNameKana(), customer.getUser().getLastNameKana()));
        user.setZipCode(StringUtils.defaultString(user.getZipCode(), customer.getUser().getZipCode()));
        user.setState(StringUtils.defaultString(user.getState(), customer.getUser().getState()));
        user.setCity(StringUtils.defaultString(user.getCity(), customer.getUser().getCity()));
        user.setStreet(StringUtils.defaultString(user.getStreet(), customer.getUser().getStreet()));
        user.setPhoneNo(StringUtils.defaultString(user.getPhoneNo(), customer.getUser().getPhoneNo()));
        user.setMobilePhoneNo(StringUtils.defaultString(user.getMobilePhoneNo(), customer.getUser().getMobilePhoneNo()));
        userRepository.save(user);

        // 既存のユーザーに紐付けて飼い主を登録する
        customer.setUser(user);
        customer.setClinic(clinic);
        customer.setRemoved(Boolean.FALSE);
        return customerRepository.save(customer);
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
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
            saved = userService.activate(user);
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
