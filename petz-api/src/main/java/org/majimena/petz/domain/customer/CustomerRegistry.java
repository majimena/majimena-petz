package org.majimena.petz.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;

import java.io.Serializable;

/**
 * 顧客登録簿.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistry implements Serializable {

    private String clinicId;

    private User user;

    private Customer customer;

}
