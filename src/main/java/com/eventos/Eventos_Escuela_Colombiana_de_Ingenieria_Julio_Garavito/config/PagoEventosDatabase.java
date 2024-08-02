package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "db1ManagerFactory",
        transactionManagerRef = "db1TransactionManager",
        basePackages = {
                "com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.repositories"
        }
)
public class PagoEventosDatabase {
    private final Environment environment;

    public PagoEventosDatabase(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "db1DataSource")
    public DataSource dataSource1() {
        return DataSourceBuilder.create()
                .url(environment.getProperty("db1.datasource.jdbc.url"))
                .driverClassName(environment.getProperty("db1.datasource.driver-class-name"))
                .username(environment.getProperty("db1.datasource.username"))
                .password(environment.getProperty("db1.datasource.password"))
                .build();
    }
    @Bean(name = "db1ManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean1() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource1());
        factoryBean.setPackagesToScan("com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.domain.entities");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.default_schema", "even");
        factoryBean.setJpaPropertyMap(properties);
        return factoryBean;
    }

    @Bean(name = "db1TransactionManager")
    public PlatformTransactionManager platformTransactionManager1(
            @Qualifier(value = "db1ManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
