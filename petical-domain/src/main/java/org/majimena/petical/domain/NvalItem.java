package org.majimena.petical.domain;

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
import org.majimena.petical.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petical.datatype.defs.Description;
import org.majimena.petical.datatype.defs.Text;
import org.majimena.petical.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petical.datatype.serializers.ISO8601LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 動物用医薬品アイテム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "wk_nval_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NvalItem extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "nval_id", length = Description.MAX_LENGTH, nullable = true)
    private String nvalId;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "name", length = Description.MAX_LENGTH, nullable = true)
    private String name;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "category_name", length = Description.MAX_LENGTH, nullable = true)
    private String categoryName;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "side_effect")
    private Boolean sideEffect;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "medicinal_effect_category", length = Description.MAX_LENGTH, nullable = true)
    private String medicinalEffectCategory;
    @Size(max = Text.MAX_LENGTH)
    @Column(name = "packing_unit", nullable = true)
    private String packingUnit;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "target", length = Description.MAX_LENGTH, nullable = true)
    private String target;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "banning_period", length = Description.MAX_LENGTH, nullable = true)
    private String banningPeriod;
    @Size(max = Text.MAX_LENGTH)
    @Column(name = "effect", nullable = true)
    private String effect;
    @Size(max = Text.MAX_LENGTH)
    @Column(name = "dosage", nullable = true)
    private String dosage;
    @Size(max = Text.MAX_LENGTH)
    @Column(name = "attention", nullable = true)
    private String attention;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "storage_condition", length = Description.MAX_LENGTH, nullable = true)
    private String storageCondition;
    @Size(max = Text.MAX_LENGTH)
    @Column(name = "note", nullable = true)
    private String note;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "modified_date", nullable = true)
    private LocalDateTime modifiedDate;
    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "approved_date", nullable = true)
    private LocalDateTime approvedDate;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "approved_type", length = Description.MAX_LENGTH, nullable = true)
    private String approvedType;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "approved_date1", nullable = true)
    private LocalDateTime approvedDate1;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "approved_date2", nullable = true)
    private LocalDateTime approvedDate2;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "approved_date3", nullable = true)
    private LocalDateTime approvedDate3;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "notified_date", nullable = true)
    private LocalDateTime notifiedDate;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "re_examine_result_notice_date", nullable = true)
    private LocalDateTime reExamineResultNoticeDate;

    @Size(max = Description.MAX_LENGTH)
    @Column(name = "maker_or_dealer_name", length = Description.MAX_LENGTH, nullable = true)
    private String makerOrDealerName;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "selected_maker_or_dealer_name", length = Description.MAX_LENGTH, nullable = true)
    private String selectedMakerOrDealerName;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "preparation_type", length = Description.MAX_LENGTH, nullable = true)
    private String preparationType;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "form_type", length = Description.MAX_LENGTH, nullable = true)
    private String formType;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "regulation_type", length = Description.MAX_LENGTH, nullable = true)
    private String regulationType;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "available_period", length = Description.MAX_LENGTH, nullable = true)
    private String availablePeriod;
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "ruminant_by_products", length = Description.MAX_LENGTH, nullable = true)
    private String ruminantByProducts;
}
