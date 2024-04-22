package com.ojas.multitenant.multi_tenancy.tenant;

import com.ojas.multitenant.exception.TenantNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService{

    private final TenantRepo tenantRepo;
    @Override
    public Tenant getTenantById(String tenantId) {
        return tenantRepo.findById(tenantId)
                .orElseThrow(()-> new TenantNotFoundException(
                        "Tenant not found with the given tenant id : "
                                +tenantId));
    }
}
