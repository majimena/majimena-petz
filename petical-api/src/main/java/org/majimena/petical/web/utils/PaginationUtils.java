package org.majimena.petical.web.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class for handling pagination.
 * <p>
 * <p>
 * Pagination uses the same principles as the <a href="https://developer.github.com/v3/#pagination">Github API</api>,
 * and follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link header)</a>.
 * </p>
 */
public class PaginationUtils {

    public static final int DEFAULT_OFFSET = 1;

    public static final int MIN_OFFSET = 1;

    public static final int DEFAULT_LIMIT = 20;

    public static final int MAX_LIMIT = 100;

    private PaginationUtils() {
    }

    public static Pageable generatePageRequest(Integer offset, Integer limit) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        return new PageRequest(offset - 1, limit);
    }

    public static HttpHeaders generatePaginationHttpHeaders(Page page, String baseUrl, Integer offset, Integer limit) {
        return generatePaginationHttpHeaders(page, baseUrl, offset, limit, null);
    }

    public static HttpHeaders generatePaginationHttpHeaders(Page page, String baseUrl, Integer offset, Integer limit, Object criteria, String... excludes) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + page.getTotalElements());

        try {
            String params = getParamString(criteria, excludes);
            String link = "";
            if (offset < page.getTotalPages()) {
                URI uri = new URI(baseUrl + "?page=" + (offset + 1) + "&per_page=" + limit + params);
                link = "<" + uri.toString() + ">; rel=\"next\",";
            }
            if (offset > 1) {
                URI uri = new URI(baseUrl + "?page=" + (offset - 1) + "&per_page=" + limit + params);
                link += "<" + uri.toString() + ">; rel=\"prev\",";
            }
            URI last = new URI(baseUrl + "?page=" + page.getTotalPages() + "&per_page=" + limit + params);
            URI first = new URI(baseUrl + "?page=" + 1 + "&per_page=" + limit + params);
            link += "<" + last.toString() + ">; rel=\"last\"," + "<" + first.toString() + ">; rel=\"first\"";
            headers.add(HttpHeaders.LINK, link);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return headers;
    }

    private static String getParamString(Object o, String... excludes) {
        if (o == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(o);
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            for (String exclude : excludes) {
                if (StringUtils.equals(name, exclude) || StringUtils.equals(name, "class")) {
                    break;
                }
                try {
                    String value = ObjectUtils.toString(PropertyUtils.getProperty(o, name));
                    builder.append("&" + name + "=" + value);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return builder.toString();
    }
}
