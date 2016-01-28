package org.majimena.petz.service;

import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.customer.CustomerAuthenticationToken;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 顧客サービス.
 */
public interface CustomerService {

    Page<Customer> getCustomersByCustomerCriteria(CustomerCriteria criteria, Pageable pageable);

    Optional<Customer> getCustomerByCustomerId(String customerId);

    /**
     * 認証トークンの情報で登録情報と合致しているかチェックして認証する.
     *
     * @param token 顧客認証トークン
     * @return 顧客情報
     * @throws ApplicationException 認証に失敗した場合に発生する例外
     */
    Customer saveCustomer(CustomerAuthenticationToken token) throws ApplicationException;

    Customer saveCustomer(String clinicId, Customer customer) throws ApplicationException;

    /**
     * 顧客情報を更新する.
     *
     * @param customer 顧客情報
     * @return 更新した顧客情報
     * @throws ApplicationException
     */
    Customer updateCustomer(Customer customer) throws ApplicationException;

    /**
     * 顧客情報を削除する.<br/>
     * このケースでは、厳密には論理削除する.
     *
     * @param customer 顧客
     */
    void deleteCustomer(Customer customer);
}
