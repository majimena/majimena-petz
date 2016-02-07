package org.majimena.petz.service;

import org.majimena.petz.domain.ClinicInvitation;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * クリニック招待サービス.
 */
public interface ClinicInvitationService {

    /**
     * ユーザーIDをもとに、クリニックへの招待状を全て取得する.
     *
     * @param userId ユーザーID
     * @return クリニックへの招待状の一覧
     */
    List<ClinicInvitation> getClinicInvitationsByUserId(String userId);

    /**
     * 招待状を取得する.
     *
     * @param invitationId 招待状ID
     * @return 該当する招待状
     */
    Optional<ClinicInvitation> getClinicInvitationById(String invitationId);

    /**
     * 招待状を送付する.
     *
     * @param clinicId 招待状を送信するクリニックID
     * @param userId   招待状を送信したユーザーID
     * @param emails   招待状を送信する先のメールアドレス
     */
    void inviteStaff(String clinicId, String userId, Set<String> emails);

    void activate(String invitationId, String activationKey);

}
