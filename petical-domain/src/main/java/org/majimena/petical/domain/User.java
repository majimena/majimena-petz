package org.majimena.petical.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petical.datatype.defs.MailAddress;
import org.majimena.petical.datatype.defs.Name;
import org.majimena.petical.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petical.datatype.deserializers.LangKeyDeserializer;
import org.majimena.petical.datatype.deserializers.TimeZoneDeserializer;
import org.majimena.petical.datatype.serializers.EnumDataTypeSerializer;
import org.majimena.petical.datatype.serializers.ISO8601LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * ユーザーエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "authorities")
@Entity
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true, nullable = false)
    private String login;

    @JsonIgnore
    @Size(min = 5, max = 100)
    @Column(length = 100)
    private String password;

    @Size(max = Name.MAX_LENGTH)
    @Column(name = "username", length = 50)
    private String username;

    @Size(max = Name.MAX_LENGTH)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = Name.MAX_LENGTH)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Email
    @Size(max = MailAddress.MAX_LENGTH)
    @Column(length = MailAddress.MAX_LENGTH, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean activated = Boolean.FALSE;

    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = LangKeyDeserializer.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "lang_key", length = 60, nullable = true)
    private LangKey langKey;

    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TimeZoneDeserializer.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "time_zone", length = 60, nullable = true)
    private TimeZone timeZone;

    @JsonIgnore
    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "reset_date", nullable = true)
    private LocalDateTime resetDate = null;

    @Size(max = 2)
    @Column(name = "country", length = 2, nullable = true)
    private String country;

    @Size(max = 10)
    @Column(name = "zip_code", length = 10, nullable = true)
    private String zipCode;

    @Size(max = 50)
    @Column(name = "state", length = 50, nullable = true)
    private String state;

    @Size(max = 50)
    @Column(name = "city", length = 50, nullable = true)
    private String city;

    @Size(max = 50)
    @Column(name = "street", length = 50, nullable = true)
    private String street;

    @Size(max = 15)
    @Column(name = "phone_no", length = 15, nullable = true)
    private String phoneNo;

    @Size(max = 15)
    @Column(name = "mobile_phone_no", length = 15, nullable = true)
    private String mobilePhoneNo;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> authorities = new HashSet<>();

}
