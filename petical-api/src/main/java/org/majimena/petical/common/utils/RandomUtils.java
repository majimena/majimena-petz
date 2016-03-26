package org.majimena.petical.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtils {

    private static final int DEF_COUNT = 20;

    private RandomUtils() {
    }

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    /**
     * 通常使用するアクティベーションキーを発行する.
     *
     * @return アクティベーションキー
     */
    public static String generateActivationKey() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    /**
     * セキュアなアクティベーションキーを発行する.
     *
     * @return アクティベーションキー
     */
    public static String generateSecureActivationKey() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(256, random).toString(32);
    }

    /**
     * Generates a reset key.
     *
     * @return the generated reset key
     */
    public static String generateResetKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

}
