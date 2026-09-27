package martinromero.actividad3.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TrazabilidadFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TrazabilidadFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.nanoTime();
        response.setHeader(REQUEST_ID_HEADER, requestId);
        MDC.put("requestId", requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logger.info("requestId={} method={} path={} status={} durationMs={}",
                    requestId, request.getMethod(), request.getRequestURI(), response.getStatus(), durationMs);
            MDC.remove("requestId");
        }
    }
}