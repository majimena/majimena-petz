package org.majimena.petz.service;

import org.majimena.petz.domain.User;
import org.majimena.petz.domain.UserContact;
import org.majimena.petz.domain.user.PasswordRegistry;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.domain.user.UserCriteria;
import org.majimena.petz.domain.user.UserOutline;

import java.util.List;
import java.util.Optional;

/**
 * Created by todoken on 2015/07/05.
 */
public interface UserService {

    Optional<User> activateRegistration(String key);

    Optional<User> completePasswordReset(String newPassword, String key);

    Optional<User> requestPasswordReset(String mail);

    @Deprecated
    User createUserInformation(String login, String password, String firstName, String lastName, String email, String langKey);

    void updateUserInformation(String firstName, String lastName, String email);

    /**
     * ユーザークライテリアをもとにユーザーを検索する.
     *
     * @param criteria ユーザークライテリア
     * @return 該当するユーザーの一覧（該当がない場合は空の一覧）
     */
    List<UserOutline> getUsersByUserCriteria(UserCriteria criteria);

    /**
     * ユーザーIDをもとにユーザーを取得する.
     *
     * @param userId ユーザーID
     * @return 該当するユーザー情報
     */
    Optional<User> getUserByUserId(String userId);

    /**
     * ユーザーIDをもとにユーザー連絡先を取得する.
     *
     * @param userId ユーザーID
     * @return 該当するユーザー連絡先情報
     */
    Optional<UserContact> getUserContactByUserId(String userId);

    /**
     * ユーザーを新規登録（サインアップ）する.
     *
     * @param registry 新規ユーザー
     */
    @Deprecated
    void saveUser(SignupRegistry registry);

    /**
     * ユーザーを新規登録する.
     *
     * @param user ユーザー情報
     * @return 登録したユーザー情報
     */
    User saveUser(User user);

    /**
     * ユーザー連絡先を保存する.
     *
     * @param contact ユーザー連絡先情報
     * @return 保存したユーザー連絡先
     */
    @Deprecated
    UserContact saveUserContact(UserContact contact);

    /**
     * パスワードを変更する.
     *
     * @param registry パスワード登録情報
     */
    void changePassword(PasswordRegistry registry);

    /**
     * ユーザーを更新する.
     *
     * @param user 更するユーザー情報
     * @return 更新後のユーザー情報
     */
    User patchUser(User user);

}
