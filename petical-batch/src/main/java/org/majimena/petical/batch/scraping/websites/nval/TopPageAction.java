package org.majimena.petical.batch.scraping.websites.nval;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.majimena.petical.batch.scraping.websites.PageAction;
import rx.Observable;

import java.io.IOException;

/**
 * トップページのアクション.
 */
public class TopPageAction implements PageAction<WebClient> {

    public static final String TOP_PAGE = "http://www.nval.go.jp/asp/asp_dbDR_idx.asp";

    @Override
    public Observable<HtmlPage> doAction(WebClient client) {
        return Observable.create(subscriber -> {
            try {
                HtmlPage page = client.getPage(TOP_PAGE);
                subscriber.onNext(page);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }
}
