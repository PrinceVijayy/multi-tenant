package com.ojas.multitenant.multi_tenancy.utils;

import com.ojas.multitenant.exception.TenantNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class TenantContext {
    private TenantContext(){}

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenantId(String tenantId){
        log.debug("Setting current tenant to: {}", tenantId);
        currentTenant.set(tenantId);
    }

    public static String getTenantId(){
        return currentTenant.get();
    }

    public static void clear(){
        log.debug("Clearing current tenant");
        currentTenant.remove();
    }
}
