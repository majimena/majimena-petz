package org.majimena.petical.batch.scraping.websites;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import rx.Observable;

/**
 * ページアクション.
 */
public interface PageAction<T> {

    Observable<HtmlPage> doAction(T trigger);

}
