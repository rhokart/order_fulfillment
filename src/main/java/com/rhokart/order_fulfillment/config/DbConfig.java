package com.rhokart.order_fulfillment.config;


import com.rhokart.order_fulfillment.util.OrderDbMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@MapperScan(value = "com.rhokart.order_fulfillment.repository",
        annotationClass = OrderDbMapper.class,
        sqlSessionFactoryRef = "orderDBSessionFactory")
@Configuration
public class DbConfig {

    @Value("${spring.datasource.ordermgmt.driverClassName}")
    private String driver;

    @Value("${spring.datasource.ordermgmt.url}")
    private String url;

    @Value("${spring.datasource.ordermgmt.username}")
    private String username;

    @Value("${spring.datasource.ordermgmt.password}")
    private String password;

    @Bean(name = "orderDBSource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

    @Bean(name = "orderDBSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("orderDBSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        return factoryBean.getObject();
    }

    @Bean(name = "orderDBSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("orderDBSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "orderDBTransactionManager")
    public DataSourceTransactionManager eWalletDBTransactionManager(@Qualifier("orderDBSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
