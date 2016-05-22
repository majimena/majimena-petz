package io.petical.batch.scraping.websites;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Consumer;

import static org.junit.Assert.fail;

/**
 * {@link Nval}
 */
@RunWith(Enclosed.class)
public class NvalTest {

    public static class SearchTest {
        private Nval sut = new Nval();

        @Test
        public void testSearch() throws Exception {
            try {
                sut.init()
                        .flatMap(page -> sut.search(page))
                        .flatMap(page -> sut.extract(page))
                        .flatMap(page -> sut.format(page))
                        .subscribe(element -> System.out.println(element), throwable -> fail("エラー発生"));
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }
        }
    }
}