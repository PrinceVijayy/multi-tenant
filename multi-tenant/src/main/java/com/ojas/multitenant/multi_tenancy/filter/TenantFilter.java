package com.ojas.multitenant.multi_tenancy.filter;

import com.ojas.multitenant.exception.TenantResolutionException;
import com.ojas.multitenant.multi_tenancy.tenant.Tenant;
import com.ojas.multitenant.multi_tenancy.tenant.TenantService;
import com.ojas.multitenant.multi_tenancy.utils.TenantContext;
import io.micrometer.common.KeyValue;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ServerHttpObservationFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final TenantService tenantService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tenantId = null;
        String requestHeader = request.getHeader("X-TENANT-ID");

        if (requestHeader != null) {
            tenantId = request.getHeader("X-TENANT-ID");
        } else {
            tenantId = request.getServerName().split("\\.")[0];
        }
        if (StringUtils.hasText(tenantId) && isTenantValid(tenantId)) {
            TenantContext.setTenantId(tenantId);
            configureLogs(tenantId);
            configureTraces(tenantId, request);
        } else {
            throw new TenantResolutionException("A valid tenant must be specified for requests to %s".formatted(request.getRequestURI()));
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            clear();
        }
    }

    private boolean isTenantValid(String tenantId) {
        Tenant tenant = tenantService.getTenantById(tenantId);
        return tenant.isEnabled();
    }

    private void configureLogs(String tenantId) {
        MDC.put("tenantId", tenantId);
    }

    private void configureTraces(String tenantId, HttpServletRequest request) {
        ServerHttpObservationFilter.findObservationContext(request).ifPresent(context ->
                context.addHighCardinalityKeyValue(KeyValue.of("tenant.id", tenantId)));
    }

    private void clear() {
        MDC.remove("tenantId");
        TenantContext.clear();
    }

}
