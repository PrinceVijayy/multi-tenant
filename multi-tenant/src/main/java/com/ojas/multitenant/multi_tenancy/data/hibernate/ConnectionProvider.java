package com.ojas.multitenant.multi_tenancy.data.hibernate;

import com.ojas.multitenant.constant.TenantEnum;
import com.ojas.multitenant.multi_tenancy.tenant.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.MultiTenancySettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionProvider implements MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {

    private final transient DataSource dataSource;
    private final TenantService tenantService;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return getConnection(TenantEnum.DEFAULT.name());
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.info("Get connection for tenant {}", tenantIdentifier);
        var schema = tenantService
                .getTenantById(tenantIdentifier)
                .getSchema();

        Connection connection = dataSource.getConnection();
        connection.setSchema(schema);
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        log.info("Release connection for tenant {}", tenantIdentifier);
        connection.setSchema(null);
        releaseAnyConnection(connection);
    }


    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if ( MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType) ) {
            return (T) this;
        } else {
            throw new UnknownUnwrapTypeException( unwrapType );
        }
    }
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }
}
