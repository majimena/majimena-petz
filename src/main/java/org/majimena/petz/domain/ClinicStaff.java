package org.majimena.petz.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.majimena.persistence.converter.LocalDatePersistenceConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * クリニックスタッフドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "clinic_staff")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ClinicStaff extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @NotNull
    @Size(max = 60)
    @Column(name = "role", length = 60, nullable = false)
    private String role;

    @NotNull
    @Column(name = "activated", nullable = false)
    private Boolean activated = Boolean.FALSE;

    @JsonIgnore
    @Size(max = 20)
    @Column(name = "activation_key", length = 20, nullable = true)
    private String activationKey;

    @Column(name = "activated_date", length = 60, nullable = true)
    @Convert(converter = LocalDatePersistenceConverter.class)
    private LocalDate activatedDate;

}
