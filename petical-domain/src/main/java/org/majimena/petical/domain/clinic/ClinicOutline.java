package org.majimena.petical.domain.clinic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * クリニック概要.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicOutline {

    private BigDecimal reserve;
    private BigDecimal chart;
    private BigDecimal examinated;
    private BigDecimal sales;
}
