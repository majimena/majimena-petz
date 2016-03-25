package org.majimena.petz.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * チケットの添付ファイルの検索条件.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TicketAttachmentCriteria implements Serializable {

    private static final long serialVersionUID = 2636801448393141877L;

    private String clinicId;

    private String ticketId;
}
