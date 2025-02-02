package com.jamiewang.usercenter.config;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.jamiewang.usercenter.dao", entityManagerFactoryRef = "primaryEntityManager", transactionManagerRef = "primaryTransactionManager")
@EnableTransactionManagement
public class HibernateConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private LocalContainerEntityManagerFactoryBean entityManager;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

//    @Autowired
//    private SessionFactory sessionFactory;

    private DataSource dataSource;

    @Bean
    public DataSource dataSource(){
        this.dataSource = DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
        return this.dataSource;
    }

    @Bean
    public LocalSessionFactoryBean localSessionFactoryBean(){
        Properties properties=new Properties();
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        LocalSessionFactoryBean lsb=new LocalSessionFactoryBean();
        lsb.setDataSource(dataSource());
        /**
         * 设置实体类的位置
         */
        lsb.setPackagesToScan("com.jamiewang.usercenter.entity");
        lsb.setHibernateProperties(properties);
        return lsb;
    }

    @Bean
    @Primary
    LocalContainerEntityManagerFactoryBean primaryEntityManager(EntityManagerFactoryBuilder builder) {
        log.info("Scanning entity...");
        entityManager = builder.dataSource(this.dataSource).packages("com.jamiewang.usercenter.entity")
                .persistenceUnit("primaryPersistenceUnit").build();
        return entityManager;
    }

    @Bean
    @Primary
    PlatformTransactionManager primaryTransactionManager(EntityManagerFactoryBuilder builder) {
        log.info("Configuration database transaction manager...");
        return new JpaTransactionManager(entityManager.getObject());
    }

}
