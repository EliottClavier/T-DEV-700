package com.api.bank.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import java.util.Properties;

//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = "com.api.bank.repository")
public class EntityManagerUtils {

//        @Autowired
//        private Environment env;
//
//        @Bean
//        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//
//            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//            vendorAdapter.setDatabase(Database.valueOf(env.getProperty("spring.jpa.database")));
//            vendorAdapter.setGenerateDdl(true);
//
//            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//            em.setDataSource(dataSource());
//            em.setPackagesToScan("com.thomasvitale.jpa.demo.model");
//            em.setJpaVendorAdapter(vendorAdapter);
//            em.setJpaProperties(additionalProperties());
//
//            return em;
//        }
//
//        @Bean
//        public DataSource dataSource() {
//            DriverManagerDataSource dataSource = new DriverManagerDataSource();
//            dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
//            dataSource.setUrl(env.getProperty("spring.datasource.url"));
//            dataSource.setUsername(env.getProperty("spring.datasource.username"));
//            dataSource.setPassword(env.getProperty("spring.datasource.password"));
//            return dataSource;
//        }
//
//        @Bean
//        public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
//            JpaTransactionManager transactionManager = new JpaTransactionManager();
//            transactionManager.setEntityManagerFactory(emf);
//
//            return transactionManager;
//        }
//
//        @Bean
//        public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
//            return new PersistenceExceptionTranslationPostProcessor();
//        }
//
//        private Properties additionalProperties() {
//            Properties properties = new Properties();
//            properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
//            properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
//            properties.setProperty("hibernate.current_session_context_class", env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
//            properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", env.getProperty("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation"));
//            properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
//            properties.setProperty("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
//            return properties;
//        }
//    }
}
