package org.majimena.petz.domain.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * グラフ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Graph implements Serializable {
    private static final long serialVersionUID = 8553594676748196467L;
    private List<String> labels;
    private List<List<Object>> data;
}
