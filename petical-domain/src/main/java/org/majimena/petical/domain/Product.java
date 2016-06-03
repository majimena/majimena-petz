package org.majimena.petical.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;
import org.majimena.petical.datatype.CourseType;
import org.majimena.petical.datatype.TaxType;
import org.majimena.petical.datatype.defs.Description;
import org.majimena.petical.datatype.defs.Name;
import org.majimena.petical.datatype.deserializers.CourseTypeDeserializer;
import org.majimena.petical.datatype.deserializers.TaxTypeDeserializer;
import org.majimena.petical.datatype.serializers.EnumDataTypeSerializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品ドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Product extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @NotEmpty
    @Enumerated(EnumType.STRING)
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = CourseTypeDeserializer.class)
    @Column(name = "course", length = 20, nullable = false)
    private CourseType course;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "category", length = Name.MAX_LENGTH, nullable = false)
    private String category;

    @NotEmpty
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "name", length = Name.MAX_LENGTH, nullable = false)
    private String name;

    @Size(max = Name.MAX_LENGTH)
    @Column(name = "unit", length = Name.MAX_LENGTH, nullable = false)
    private String unit;

    @NotNull
    @Digits(integer = 9, fraction = 0)
    @Column(name = "price", precision = 9, scale = 0, nullable = false)
    private BigDecimal price;

    // デフォルトEXCLUSIVE
    @Enumerated(EnumType.STRING)
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TaxTypeDeserializer.class)
    @Column(name = "tax_type", length = 20, nullable = false)
    private TaxType taxType;

    // デフォルト0.08
    @Digits(integer = 1, fraction = 2)
    @Column(name = "tax_rate", precision = 3, scale = 2, nullable = false)
    private BigDecimal taxRate;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "description", length = Description.MAX_LENGTH, nullable = true)
    private String description;

    @Column(name = "removed", nullable = false)
    private Boolean removed;
}
