package org.majimena.petical.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petical.datatype.SexType;
import org.majimena.petical.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petical.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petical.datatype.serializers.ISO8601LocalDateTimeSerializer;
import org.majimena.petical.datatype.defs.Memo;
import org.majimena.petical.datatype.defs.MicrochipNo;
import org.majimena.petical.datatype.defs.Name;
import org.majimena.petical.datatype.defs.URL;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
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
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonSerialize(using = TypeSerializer.class)
    @JsonDeserialize(using = TypeDeserializer.class)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type", nullable = false)
    private Type type;

    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "color", nullable = false)
    private Color color;

    @JsonSerialize(using = BloodSerializer.class)
    @JsonDeserialize(using = BloodDeserializer.class)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blood", nullable = true)
    private Blood blood;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", length = 10, nullable = true)
    private SexType sex;

    @Size(max = MicrochipNo.MAX_LENGTH)
    @Column(name = "microchip_no", length = 50, nullable = true)
    private String microchipNo;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "microchip_date", nullable = true)
    private LocalDateTime microchipDate;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "birth_date", nullable = true)
    private LocalDateTime birthDate;

    @Column(nullable = true)
    private Boolean neutral = Boolean.FALSE;

    @Size(max = Memo.MAX_LENGTH)
    @Column(name = "profile", length = 2000, nullable = true)
    private String profile;

    @Size(max = Memo.MAX_LENGTH)
    @Column(name = "allergia", length = 2000, nullable = true)
    private String allergia;

    @Size(max = Memo.MAX_LENGTH)
    @Column(name = "drug", length = 2000, nullable = true)
    private String drug;

    @Size(max = Memo.MAX_LENGTH)
    @Column(name = "other", length = 2000, nullable = true)
    private String other;

    @Size(max = URL.MAX_LENGTH)
    @Column(name = "image", length = 200, nullable = true)
    private String image;

    @JsonSerialize(using = TagSetSerializer.class)
    @JsonDeserialize(using = TagSetDeserializer.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "pet_tag",
            joinColumns = {@JoinColumn(name = "pet_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Tag> tags = Sets.newHashSet();
}
