package org.majimena.petz.common.utils;

import org.junit.Test;
import org.majimena.petz.common.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by todoken on 2015/06/23.
 */
public class RandomUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(RandomUtils.class);

    @Test
    public void ランダムに生成したキーが他と重複しないこと_アクティベーションキーを発行した場合() {
        String key = RandomUtils.generateActivationKey();
        logger.debug(key);
        for (int i = 0; i < 100; i++) {
            String second = RandomUtils.generateActivationKey();
            assertThat(key, is(not(second)));
        }
    }

    @Test
    public void ランダムに生成したキーが他と重複しないこと_セキュアなアクティベーションキーを発行した場合() {
        String key = RandomUtils.generateSecureActivationKey();
        logger.debug(key);
        for (int i = 0; i < 100; i++) {
            String second = RandomUtils.generateSecureActivationKey();
            assertThat(key, is(not(second)));
        }
    }

}
