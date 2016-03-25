package org.majimena.petz.domain;

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
import org.majimena.petz.datatype.TicketActivityType;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petz.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petz.datatype.deserializers.TicketActivityTypeDeserializer;
import org.majimena.petz.datatype.deserializers.TicketStateDeserializer;
import org.majimena.petz.datatype.serializers.EnumDataTypeSerializer;
import org.majimena.petz.datatype.serializers.ISO8601LocalDateTimeSerializer;

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
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * チケットのアクティビティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "ticket_activity")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class TicketActivity extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @NotNull
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TicketActivityTypeDeserializer.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TicketActivityType type;

    @NotNull
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TicketStateDeserializer.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "state_from", nullable = false)
    private TicketState stateFrom;

    @NotNull
    @JsonSerialize(using = EnumDataTypeSerializer.class)
    @JsonDeserialize(using = TicketStateDeserializer.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "state_to", nullable = false)
    private TicketState stateTo;

    @NotNull
    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "change_date_time", nullable = false)
    private LocalDateTime changeDateTime;
}
