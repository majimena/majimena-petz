package org.majimena.petical.repository;

import org.majimena.petical.domain.ClinicInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * クリニック招待リポジトリ.
 */
public interface ClinicInvitationRepository extends JpaRepository<ClinicInvitation, String> {

    /**
     * ユーザーIDまたはメールアドレスをもとに、該当するクリニックの招待状を取得する.
     *
     * @param userId ユーザーID
     * @param email  メールアドレス
     * @return 該当するクリニックの招待状の一覧
     */
    List<ClinicInvitation> findByInvitedUserIdOrEmailOrderByCreatedDateAsc(String userId, String email);

    /**
     * メールアドレスをもとに古い招待状を削除する.
     * @param email メールアドレス
     */
    void deleteByEmail(String email);
}
