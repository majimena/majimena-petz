package org.majimena.petical.domain;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * タグ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tag implements Serializable {

    @NotEmpty
    @Size(max = 50)
    @Id
    @Column(name = "name", length = 50, nullable = false)
    private String name;
}
