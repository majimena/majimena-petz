package org.majimena.petical.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.majimena.petical.datatype.defs.Name;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 種類.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@Entity
@Table(name = "kind")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Kind implements Serializable {

    @Id
    @Size(max = Name.MAX_LENGTH)
    @Column(name = "name", length = Name.MAX_LENGTH, nullable = false)
    private String name;
}
