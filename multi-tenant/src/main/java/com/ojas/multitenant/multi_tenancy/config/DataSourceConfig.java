package com.ojas.multitenant.multi_tenancy.config;

import com.ojas.multitenant.multi_tenancy.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final Environment env;

    @Bean
    public DataSource dataSource() {

        String driverClassName = env.getProperty("spring.datasource.driver-classname");
        String url = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        if (url!=null && url.contains("{schema}")) {
            Tenant tenant = new Tenant(); // Fetch tenant information based on ID
            url = url.replace("{schema}", tenant.getSchema());
        }

        DataSourceBuilder<?> dataSource = DataSourceBuilder.create();
        dataSource.driverClassName(driverClassName);
        dataSource.url(url);
        dataSource.username(username);
        dataSource.password(password);

        return dataSource.build();
    }
}
