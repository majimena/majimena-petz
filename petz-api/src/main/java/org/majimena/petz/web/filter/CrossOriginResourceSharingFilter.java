package org.majimena.petz.web.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORSフィルタ.
 */
public class CrossOriginResourceSharingFilter extends OncePerRequestFilter {

    private String allowOrigin = "*";
    private String allowMethods = "GET,POST,PUT,DELETE";
    private Boolean allowCredentials = Boolean.TRUE;
    private String allowHeaders = "Content-Type, X-Requested-With, Origin, Accept, Authorization";
    private String exposeHeaders = "Link";
    private Integer maxAge = 3600;

    public void setAllowOrigin(String allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    public void setAllowMethods(String allowMethods) {
        this.allowMethods = allowMethods;
    }

    public void setAllowCredentials(Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public void setAllowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    public void setExposeHeaders(String exposeHeaders) {
        this.exposeHeaders = exposeHeaders;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // write CORS headers
        setHeader(response, "Access-Control-Allow-Origin", allowOrigin);
        setHeader(response, "Access-Control-Allow-Methods", allowMethods);
        setHeader(response, "Access-Control-Allow-Credentials", allowCredentials.toString());
        setHeader(response, "Access-Control-Allow-Headers", allowHeaders);
        setHeader(response, "Access-Control-Expose-Headers", exposeHeaders);
        setHeader(response, "Access-Control-Max-Age", String.valueOf(maxAge));

        // cache control
        setHeader(response, "Cache-Control", "no-cache, no-store, must-revalidate");

        // skip processing OPTIONS methods
        if (!StringUtils.equals(StringUtils.upperCase(request.getMethod()), "OPTIONS")) {
            filterChain.doFilter(request, response);
        }
    }

    protected void setHeader(HttpServletResponse response, String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            response.setHeader(name, value);
        }
    }
}
