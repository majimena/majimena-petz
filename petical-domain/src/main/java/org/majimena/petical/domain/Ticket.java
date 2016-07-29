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
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petical.datatype.defs.Description;
import org.majimena.petical.datatype.defs.Memo;
import org.majimena.petical.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petical.datatype.deserializers.TicketStateDeserializer;
import org.majimena.petical.datatype.serializers.EnumDataTypeSerializer;
import org.majimena.petical.datatype.serializers.ISO8601LocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * カルテドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "ticket")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class Ticket extends AbstractAuditingEntity implements Serializable {
    /**
     * ID.
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    /**
     * 動物病院ID.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    /**
     * カルテID.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chart_id", nullable = false)
    private Chart chart;

    /**
     * ステート.
     */
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TicketStateDeserializer.class)
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "state", nullable = false)
    private TicketState state;

    /**
     * 診察開始日時.
     */
    @NotNull
    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    /**
     * 診察終了日時.
     */
    @NotNull
    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    /**
     * 消費税.
     */
    @Column(name = "tax", precision = 9, scale = 0, nullable = true)
    private BigDecimal tax;

    /**
     * 請求金額.
     */
    @Column(name = "total", precision = 9, scale = 0, nullable = true)
    private BigDecimal total;

    /**
     * 不足金額.
     */
    @Column(name = "balance", precision = 9, scale = 0, nullable = true)
    private BigDecimal balance;

    /**
     * 値引金額.
     */
    @Column(name = "discount", precision = 9, scale = 0, nullable = true)
    private BigDecimal discount;

    /**
     * 主訴・稟告.
     */
    @Size(max = Description.MAX_LENGTH)
    @Column(name = "autognosis", length = Description.MAX_LENGTH, nullable = true)
    private String autognosis;

    /**
     * 診断結果.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id", nullable = true)
    private Diagnosis diagnosis;

    /**
     * 所見.
     */
    @Size(max = Memo.MAX_LENGTH)
    @Column(name = "memo", length = Memo.MAX_LENGTH, nullable = true)
    private String memo;
}
