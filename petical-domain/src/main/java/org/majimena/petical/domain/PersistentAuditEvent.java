package org.majimena.petical.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.majimena.petical.datatype.converters.LocalDateTimePersistenceConverter;
import org.majimena.petical.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petical.datatype.serializers.ISO8601LocalDateTimeSerializer;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Persist AuditEvent managed by the Spring Boot actuator
 *
 * @see org.springframework.boot.actuate.audit.AuditEvent
 */
@Entity
@Table(name = "audit_event")
public class PersistentAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String principal;

    @JsonSerialize(using = ISO8601LocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateTimeDeserializer.class)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    @Column(name = "event_date")
    private LocalDateTime auditEventDate;

    @Column(name = "event_type")
    private String auditEventType;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "audit_event_data", joinColumns = @JoinColumn(name = "event_id"))
    private Map<String, String> data = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public LocalDateTime getAuditEventDate() {
        return auditEventDate;
    }

    public void setAuditEventDate(LocalDateTime auditEventDate) {
        this.auditEventDate = auditEventDate;
    }

    public String getAuditEventType() {
        return auditEventType;
    }

    public void setAuditEventType(String auditEventType) {
        this.auditEventType = auditEventType;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
