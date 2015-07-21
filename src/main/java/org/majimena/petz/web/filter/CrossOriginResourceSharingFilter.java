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
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // FIXME 環境ごとに許可先を変えられるようにする
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, Origin, Accept, Authorization");
        response.setHeader("Access-Control-Expose-Headers", "Link");

        if (!StringUtils.equals(StringUtils.upperCase(request.getMethod()), "OPTIONS")) {
            filterChain.doFilter(request, response);
        }
    }
}
