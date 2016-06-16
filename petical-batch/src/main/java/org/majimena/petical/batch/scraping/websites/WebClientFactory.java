package org.majimena.petical.batch.scraping.websites;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * ウェブクライアントのファクトリ.
 */
public class WebClientFactory {
    public static WebClient create() {
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setJavaScriptEnabled(true);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        client.waitForBackgroundJavaScript(10000);
        client.getOptions().setRedirectEnabled(true);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getCookieManager().setCookiesEnabled(true);
        return client;
    }
}
