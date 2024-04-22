package com.ojas.multitenant.multi_tenancy.tenant;

public interface TenantService {
    Tenant getTenantById(String tenantId);
}
