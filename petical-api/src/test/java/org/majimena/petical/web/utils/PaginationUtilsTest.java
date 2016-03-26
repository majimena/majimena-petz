package org.majimena.petical.web.utils;

import org.junit.Test;
import org.majimena.petical.common.utils.TestCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

import java.net.URISyntaxException;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see PaginationUtils
 */
public class PaginationUtilsTest {

    @Test
    public void ページリクエストを生成できること() {
        Pageable result = PaginationUtils.generatePageRequest(1, 100);

        assertThat(result.getPageNumber(), is(0));
        assertThat(result.getOffset(), is(0));
        assertThat(result.getPageSize(), is(100));
    }

    @Test
    public void オフセットがない場合でもページリクエストを生成できること() {
        Pageable result = PaginationUtils.generatePageRequest(null, 100);

        assertThat(result.getPageNumber(), is(0));
        assertThat(result.getOffset(), is(PaginationUtils.DEFAULT_OFFSET - 1));
        assertThat(result.getPageSize(), is(100));
    }

    @Test
    public void ページサイズがない場合でもページリクエストを生成できること() {
        Pageable result = PaginationUtils.generatePageRequest(1, null);

        assertThat(result.getPageNumber(), is(0));
        assertThat(result.getOffset(), is(0));
        assertThat(result.getPageSize(), is(PaginationUtils.DEFAULT_LIMIT));
    }

    @Test
    public void ページング用ヘッダを生成できること() throws URISyntaxException {
        TestCriteria criteria = new TestCriteria();
        criteria.setCondition1("value1");
        criteria.setCondition2("value2");
        Pageable pageable = PaginationUtils.generatePageRequest(1, 1);
        Page<String> page = new PageImpl<>(Arrays.asList("test"), pageable, 10);
        HttpHeaders result = PaginationUtils.generatePaginationHttpHeaders(page, "/api/test", 1, 1, criteria, "condition2");

        assertThat(result.get("X-Total-Count").get(0), is("10"));
        assertThat(result.get(HttpHeaders.LINK).get(0), is("</api/test?page=2&per_page=1&condition1=value1>; rel=\"next\",</api/test?page=10&per_page=1&condition1=value1>; rel=\"last\",</api/test?page=1&per_page=1&condition1=value1>; rel=\"first\""));
    }

    @Test
    public void 次ページ目以降もページング用ヘッダを生成できること() throws URISyntaxException {
        TestCriteria criteria = new TestCriteria();
        criteria.setCondition1("value1");
        criteria.setCondition2("value2");
        Pageable pageable = PaginationUtils.generatePageRequest(1, 1);
        Page<String> page = new PageImpl<>(Arrays.asList("test"), pageable, 10);
        HttpHeaders result = PaginationUtils.generatePaginationHttpHeaders(page, "/api/test", 2, 1, criteria, "condition2");

        assertThat(result.get("X-Total-Count").get(0), is("10"));
        assertThat(result.get(HttpHeaders.LINK).get(0), is("</api/test?page=3&per_page=1&condition1=value1>; rel=\"next\",</api/test?page=1&per_page=1&condition1=value1>; rel=\"prev\",</api/test?page=10&per_page=1&condition1=value1>; rel=\"last\",</api/test?page=1&per_page=1&condition1=value1>; rel=\"first\""));
    }

    @Test
    public void オフセットがない場合でもページング用ヘッダを生成できること() throws URISyntaxException {
        Pageable pageable = PaginationUtils.generatePageRequest(1, 1);
        Page<String> page = new PageImpl<>(Arrays.asList("test"), pageable, 10);
        HttpHeaders result = PaginationUtils.generatePaginationHttpHeaders(page, "/api/test", null, 1);

        assertThat(result.get("X-Total-Count").get(0), is("10"));
        assertThat(result.get(HttpHeaders.LINK).get(0), is("</api/test?page=2&per_page=1>; rel=\"next\",</api/test?page=10&per_page=1>; rel=\"last\",</api/test?page=1&per_page=1>; rel=\"first\""));
    }

    @Test
    public void ページサイズがない場合でもページング用ヘッダを生成できること() throws URISyntaxException {
        Pageable pageable = PaginationUtils.generatePageRequest(1, 1);
        Page<String> page = new PageImpl<>(Arrays.asList("test"), pageable, 10);
        HttpHeaders result = PaginationUtils.generatePaginationHttpHeaders(page, "/api/test", 1, null);

        assertThat(result.get("X-Total-Count").get(0), is("10"));
        assertThat(result.get(HttpHeaders.LINK).get(0), is("</api/test?page=2&per_page=20>; rel=\"next\",</api/test?page=10&per_page=20>; rel=\"last\",</api/test?page=1&per_page=20>; rel=\"first\""));
    }
}
