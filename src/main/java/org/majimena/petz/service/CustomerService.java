package org.majimena.petz.service;

import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.customer.CustomerAuthorizationToken;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.majimena.petz.domain.customer.CustomerRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 顧客サービス.
 */
public interface CustomerService {

    Page<Customer> getUsersByClinicUserCriteria(CustomerCriteria criteria, Pageable pageable);

    void authorize(CustomerAuthorizationToken token) throws ApplicationException;

    Customer saveCustomer(CustomerRegistry registry) throws ApplicationException;
}
