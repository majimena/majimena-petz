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
        response.setHeader("Access-Control-Allow-Origin", allowOrigin);
        response.setHeader("Access-Control-Allow-Methods", allowMethods);
        response.setHeader("Access-Control-Allow-Credentials", allowCredentials.toString());
        response.setHeader("Access-Control-Allow-Headers", allowHeaders);
        response.setHeader("Access-Control-Expose-Headers", exposeHeaders);
        response.setHeader("Access-Control-Max-Age", String.valueOf(maxAge));

        // skip processing OPTIONS methods
        if (!StringUtils.equals(StringUtils.upperCase(request.getMethod()), "OPTIONS")) {
            filterChain.doFilter(request, response);
        }
    }
}
