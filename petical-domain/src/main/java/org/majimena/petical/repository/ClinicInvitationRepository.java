package org.majimena.petical.repository;

import org.majimena.petical.domain.ClinicInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * クリニック招待リポジトリ.
 */
public interface ClinicInvitationRepository extends JpaRepository<ClinicInvitation, String> {

    /**
     * ユーザーIDをもとに、該当するクリニックの招待状を取得する.
     *
     * @param userId ユーザーID
     * @return 該当するクリニックの招待状の一覧
     */
    List<ClinicInvitation> findByInvitedUserId(String userId);

    /**
     * メールアドレスをもとに、該当するクリニックの招待状を取得する.
     * @param email メールアドレス（ログインIDと同じもので検索すること）
     * @return 該当するクリニックの招待状の一覧
     */
    List<ClinicInvitation> findByEmail(String email);
}
