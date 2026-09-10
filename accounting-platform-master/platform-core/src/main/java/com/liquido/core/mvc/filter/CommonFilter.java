package com.liquido.core.mvc.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static java.util.regex.Pattern.compile;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Common Filter
 */
public abstract class CommonFilter extends OncePerRequestFilter implements Ordered {

    private int order;
    /**
     * Filter links by default
     */
    private final List<Pattern> excludeUrlPatterList = new ArrayList<>();
    private boolean init = false;

    @Override
    public int getOrder() {
        return order;
    }


    @Override
    protected void initFilterBean() throws ServletException {
        if (init) {
            return;
        }

        super.initFilterBean();
        // static resources
        excludeUrlPatterList.add(compile("/(.*)?/(.*)?\\.js"));
        excludeUrlPatterList.add(compile("/(.*)?/(.*)?\\.css"));
        excludeUrlPatterList.add(compile("/(.*)?/(.*)?\\.html"));
        excludeUrlPatterList.add(compile("/(.*)?/(.*)?\\.png"));
        excludeUrlPatterList.add(compile("/(.*)?/(.*)?\\.jpg"));
        excludeUrlPatterList.add(compile("/(.*)?/(.*)?\\.gif"));
        excludeUrlPatterList.add(compile("/actuator/health"));
        excludeUrlPatterList.add(compile("/health/check"));
        excludeUrlPatterList.add(compile("/health-check"));
        excludeUrlPatterList.add(compile("/health_check"));
        excludeUrlPatterList.add(compile("/"));

        init = true;
    }

    /**
     * Determine whether to execute the current filter
     *
     * @param request
     * @return
     */
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        if (excludeUrlPatterList == null) {
            return false;
        }

        for (final Pattern pattern : excludeUrlPatterList) {
            final String reqPath = request.getServletPath();
            final Matcher matcher = pattern.matcher(reqPath);
            if (matcher.matches()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Set not to print the requested url
     *
     * @param excludeUrls
     */
    public void setExcludeUrls(final List<String> excludeUrls) {
        if (excludeUrls == null) {
            return;
        }

        for (final String url : excludeUrls) {
            excludeUrlPatterList.add(compile(url));
        }
    }
}
