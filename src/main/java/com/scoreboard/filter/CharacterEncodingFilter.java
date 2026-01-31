package com.scoreboard.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@WebFilter( filterName = "CharacterEncodingFilter",  urlPatterns = "/*")
public class CharacterEncodingFilter extends HttpFilter {
    private static final String ENCODING = StandardCharsets.UTF_8.name();

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("=== CharacterEncodingFilter initialized with encoding: {} ===", ENCODING);
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(ENCODING);
            log.debug("Set request encoding to {}", ENCODING);
        }

        response.setCharacterEncoding(ENCODING);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("=== CharacterEncodingFilter destroyed ===");
    }
}
