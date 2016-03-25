package org.majimena.petz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petz.datatype.defs.Description;
import org.majimena.petz.datatype.defs.MailAddress;
import org.majimena.petz.datatype.defs.Name;
import org.majimena.petz.datatype.defs.PhoneNo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * クリニックドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "clinic")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Clinic extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "name", length = Name.MAX_LENGTH, nullable = false)
    private String name;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "first_name", length = Name.MAX_LENGTH, nullable = false)
    private String firstName;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "last_name", length = Name.MAX_LENGTH, nullable = false)
    private String lastName;

    @NotEmpty
    @Size(max = 2)
    @Column(name = "country", length = 2, nullable = false)
    private String country;

    @NotEmpty
    @Pattern(regexp = PhoneNo.REGEXP)
    @Size(max = 10)
    @Column(name = "zip_code", length = 10, nullable = false)
    private String zipCode;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "state", length = Name.MAX_LENGTH, nullable = false)
    private String state;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "city", length = Name.MAX_LENGTH, nullable = false)
    private String city;

    @Size(max = Name.MAX_LENGTH)
    @Column(name = "street", length = Name.MAX_LENGTH, nullable = true)
    private String street;

    @NotEmpty
    @Pattern(regexp = PhoneNo.REGEXP)
    @Size(max = PhoneNo.MAX_LENGTH)
    @Column(name = "phone_no", length = PhoneNo.MAX_LENGTH, nullable = false)
    private String phoneNo;

    @Email
    @Size(max = MailAddress.MAX_LENGTH)
    @Column(name = "email", length = MailAddress.MAX_LENGTH, nullable = true)
    private String email;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "description", length = Description.MAX_LENGTH, nullable = true)
    private String description;

    @Column(name = "removed", nullable = false)
    private Boolean removed;
}
