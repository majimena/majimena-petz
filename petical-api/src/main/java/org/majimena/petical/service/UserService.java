package org.majimena.petical.service;

import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.user.PasswordRegistry;
import org.majimena.petical.domain.user.SignupRegistry;
import org.majimena.petical.domain.user.UserCriteria;
import org.majimena.petical.domain.user.UserOutline;

import java.util.List;
import java.util.Optional;

/**
 * Created by todoken on 2015/07/05.
 */
public interface UserService {

    @Deprecated
    User createUserInformation(String login, String password, String firstName, String lastName, String email, LangKey langKey);

    @Deprecated
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
     * ログインIDをもとにユーザーを取得する.
     *
     * @param loginId ログインID
     * @return 該当するユーザー情報
     */
    Optional<User> getUserByLogin(String loginId);

    /**
     * ユーザーを新規登録（サインアップ）する.
     *
     * @param registry 新規ユーザー
     */
    User saveUser(SignupRegistry registry);

    /**
     * ユーザーを新規登録する.
     *
     * @param user ユーザー情報
     * @return 登録したユーザー情報
     */
    User saveUser(User user);

    /**
     * ユーザーをアクティベートする.
     *
     * @param key アクティベーションキー
     * @return アクティベートしたユーザー
     */
    Optional<User> activateRegistration(String key);

    /**
     * ユーザーを更新する.
     *
     * @param user 更するユーザー情報
     * @return 更新後のユーザー情報
     */
    User updateUser(User user);

    /**
     * パスワードを変更する.
     *
     * @param registry パスワード登録情報
     */
    void changePassword(PasswordRegistry registry);

    /**
     * パスワードをリセットするための要求を出します.
     *
     * @param login パスワードをリセットするユーザのログインID
     * @return パスワードをリセットするユーザ（いない場合もある）
     */
    Optional<User> requestPasswordReset(String login);

    /**
     * パスワードをリセットする.
     *
     * @param password パスワード
     * @param key      パスワードリセットのキー
     * @return パスワードをリセットしたユーザ（いない場合もある）
     */
    Optional<User> resetPassword(String password, String key);
}
