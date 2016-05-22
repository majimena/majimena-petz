package io.petical.batch.scraping.utils;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by todoken on 2016/05/14.
 */
public class NvalHtmlUnitUtils {

    public static String getRightSideContent(HtmlTableRow row, String name) {
        boolean found = false;
        for (HtmlTableCell cell : row.getCells()) {
            if (found) {
                return cell.getTextContent();
            }
            if (StringUtils.equals(name, cell.getTextContent())) {
                found = true;
            }
        }
        return null;
    }

    public static String getNextRowContent(Iterable<HtmlTableRow> rows, String name) {
        boolean found = false;
        int no = 0;
        for (HtmlTableRow row : rows) {
            int index = 0;
            if (found) {
                for (HtmlTableCell cell : row.getCells()) {
                    if (index == no) {
                        return cell.getTextContent();
                    }
                    index++;
                }
                throw new RuntimeException(row.asXml());
            }
            for (HtmlTableCell cell : row.getCells()) {
                if (StringUtils.equals(name, cell.getTextContent())) {
                    found = true;
                    no = index;
                }
                index++;
            }
        }
        return null;
    }

    public static boolean equalAnchorText(HtmlAnchor anchor, String content) {
        DomNode child = anchor.getFirstChild();
        if (child == null) {
            return false;
        }
        DomNode grandchild = child.getFirstChild();
        if (grandchild == null) {
            return false;
        }
        return StringUtils.equals(content, grandchild.getTextContent());
    }

}
