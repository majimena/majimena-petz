package io.petical.batch.scraping.utils;

import java.util.List;

/**
 * スクレイピング用のユーティリティ.
 */
public class ScrapingUtils {

    public static void throwIfNotFound(List<?> list) {
        if (list == null || list.size() == 0) {
            throw new IllegalStateException("" + list.size());
        }
    }

    public static void throwIfNotOne(List<?> list) {
        if (list == null || list.size() != 1) {
            throw new IllegalStateException("" + list.size());
        }
    }

    public static RuntimeException newCannotScrapeException() {
        return new RuntimeException("cannot scrape continued.");
    }
}
