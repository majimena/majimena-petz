package org.majimena.petz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
@Entity
@Table(name = "color")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Color implements Serializable {

    @Id
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;
}
