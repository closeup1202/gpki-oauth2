package kr.go.saas.gpkiauth.security.filter;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.saas.gpkiauth.history.model.ApiCallLogModel;
import kr.go.saas.gpkiauth.history.service.ApiCallLogService;
import kr.go.saas.gpkiauth.security.client.service.ClientIdService;
import kr.go.saas.gpkiauth.security.endpoint.OAuth2Endpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 6)
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoggingFilter extends OncePerRequestFilter {

    private final ApiCallLogService logService;
    private final ClientIdService clientIdService;
    private final Tracer tracer;

    @NewSpan
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        TraceContext context = Objects.requireNonNull(tracer.currentSpan()).context();
        String clientId = clientIdService.extract(request);
        request.setAttribute("traceId", context.spanId());
        request.setAttribute("clientId", clientId);
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
            if (OAuth2Endpoint.match(requestURI)) {
                long duration = System.currentTimeMillis() - start;
                ApiCallLogModel model = ApiCallLogModel.onSuccess(request, response, clientId, duration);
                logService.save(model);
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            ApiCallLogModel model = ApiCallLogModel.onFail(request, e, clientId, duration);
            logService.saveWithError(model);
            throw e;
        }
    }
}
