package org.majimena.petz.domain;

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "user_contact")
public class UserContact extends AbstractAuditingEntity implements Serializable {

    @Id
    private String id;

    @NotEmpty
    @Size(max = 2)
    @Column(name = "country", length = 2, nullable = false)
    private String country;

    @NotEmpty
    @Size(max = 10)
    @Column(name = "zip_code", length = 10, nullable = false)
    private String zipCode;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "state", length = 50, nullable = false)
    private String state;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "city", length = 50, nullable = false)
    private String city;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "street", length = 50, nullable = false)
    private String street;

    @NotEmpty
    @Size(max = 15)
    @Column(name = "phone_no", length = 15, nullable = false)
    private String phoneNo;

    @Size(max = 15)
    @Column(name = "mobile_phone_no", length = 15, nullable = true)
    private String mobilePhoneNo;

}
