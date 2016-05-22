package io.petical.batch.scraping.utils;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by todoken on 2016/05/14.
 */
public class HtmlUnitUtils {

    private static Logger logger = LoggerFactory.getLogger(HtmlUnitUtils.class);

    public static HtmlPage click(HtmlElement element) {
        try {
            Page click = element.click();
            if (click.isHtmlPage()) {
                return (HtmlPage) click;
            }
            throw new RuntimeException("not html page object");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends HtmlElement> Optional<T> getOne(Iterable<DomElement> elements, Class<T> clazz) {
        for (DomElement element : elements) {
            if (clazz.isAssignableFrom(element.getClass())) {
                return Optional.of(clazz.cast(element));
            }
        }
        return Optional.empty();
    }

    public static <T extends HtmlElement> Stream<T> getHtmlElementsByTagName(HtmlPage page, String tag, Class<T> clazz) {
        return page.getElementsByTagName(tag).stream()
                .map(element -> {
                    if (clazz.isAssignableFrom(element.getClass())) {
                        return clazz.cast(element);
                    } else {
                        throw new RuntimeException();
                    }
                });
    }

    public static Stream<HtmlTable> getHtmlTables(HtmlPage page) {
        return getHtmlElementsByTagName(page, HtmlTable.TAG_NAME, HtmlTable.class);
    }
}
