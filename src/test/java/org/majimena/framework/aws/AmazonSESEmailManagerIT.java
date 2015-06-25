package org.majimena.framework.aws;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

/**
 * 実行時には、以下の環境変数が登録されているか、~/.aws/credentialsが存在する環境で実行すること.
 * <ul>
 *     <li>AWS_ACCESS_KEY_ID</li>
 *     <li>AWS_SECRET_KEY</li>
 * </ul>
 * @see com.amazonaws.auth.DefaultAWSCredentialsProviderChain
 */
public class AmazonSESEmailManagerIT {

    private final Logger logger = LoggerFactory.getLogger(AmazonSESEmailManagerIT.class);

    private AmazonSESEmailManager sut = new AmazonSESEmailManager();

    @Before
    public void setup() {
        AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(new DefaultAWSCredentialsProviderChain());
        Region REGION = Region.getRegion(Regions.US_WEST_2);
        client.setRegion(REGION);
        sut.setAmazonSimpleEmailService(client);
    }

    @Test
    public void test() {
        try {
            sut.send("ken.todoroki@majimena.org", "ken.todoroki@majimena.org", "TEST SUBJECT", "テストメールです。\n本文は日本語が文字化けしないことを確認する。");
        } catch (Exception e) {
            logger.error("cannot send email!", e);
            fail();
        }
    }

}
