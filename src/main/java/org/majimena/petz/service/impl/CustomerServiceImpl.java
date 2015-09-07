package org.majimena.petz.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.UserContact;
import org.majimena.petz.domain.customer.CustomerAuthorizationToken;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.majimena.petz.domain.customer.CustomerRegistry;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.UserContactRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

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
    private UserContactRepository userContactRepository;

    @Override
    public Page<Customer> getUsersByClinicUserCriteria(CustomerCriteria criteria, Pageable pageable) {
        return customerRepository.findAll(CustomerRepository.Spec.of(criteria), pageable);
    }

    @Override
    public void authorize(CustomerAuthorizationToken token) throws ApplicationException {
        Clinic clinic = clinicRepository.findOne(token.getClinicId());
        if (clinic == null) {
            throw new ApplicationException(ErrorCode.PTZ_001999);
        }
        User user = userRepository.findOne(token.getUserId());
        if (user == null) {
            throw new ApplicationException(ErrorCode.PTZ_000999);
        }

        // 既に連絡先が登録されており、それを流用する場合は電話番号をキーに認証する
        UserContact contact = userContactRepository.findOne(token.getUserId());
        if (contact != null && !StringUtils.equals(contact.getPhoneNo(), token.getPhoneNo())) {
            throw new ApplicationException(ErrorCode.PTZ_000201);
        }

        // 登録情報と合っていればクリニックのユーザーに追加
        customerRepository.findByClinicIdAndUserId(token.getClinicId(), token.getUserId())
                .map(u -> {
                    u.setClinic(clinic);
                    u.setUser(user);
                    return customerRepository.save(u);
                })
                .orElseGet(() -> customerRepository.save(new Customer(null, clinic, user, Boolean.FALSE)));
    }

    @Override
    public Customer saveCustomer(final CustomerRegistry registry) throws ApplicationException {
        return null;
    }
}
