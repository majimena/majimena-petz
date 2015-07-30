package org.majimena.petz.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Sets;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.framework.core.datatypes.converters.EnumDataTypeSerializer;
import org.majimena.framework.core.datatypes.converters.ISO8601LocalDateDeserializer;
import org.majimena.framework.core.datatypes.converters.ISO8601LocalDateSerializer;
import org.majimena.framework.persistence.converters.LocalDatePersistenceConverter;
import org.majimena.petz.datatypes.SexType;
import org.majimena.petz.datatypes.SexTypeConverter;
import org.majimena.petz.datatypes.SexTypeDeserializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * ペット.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "pet")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Pet extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    @JsonSerialize(using = ISO8601LocalDateSerializer.class)
//    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "birth_date", nullable = true)
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate birthDate;

//    @JsonSerialize(using = EnumDataTypeSerializer.class)
//    @JsonDeserialize(using = SexTypeDeserializer.class)
    @Column(name = "sex", length = 10, nullable = true)
    @Convert(converter = SexTypeConverter.class)
    private SexType sex;

    @Size(max = 2000)
    @Column(name = "profile", length = 2000, nullable = true)
    private String profile;

    @NotNull
    @ManyToMany
    @JoinTable(
        name = "pet_type",
        joinColumns = {@JoinColumn(name = "pet_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "type_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Type> types = Sets.newHashSet();

    @ManyToMany
    @JoinTable(
        name = "pet_tag",
        joinColumns = {@JoinColumn(name = "pet_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "tag_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Tag> tags = Sets.newHashSet();

}
