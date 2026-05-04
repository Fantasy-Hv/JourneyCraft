package org.dsgroup.journeycraft.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 数据源和MyBatis配置类
 * <p>
 * 注意: SqlSessionFactory 由 MybatisConfig.java 统一配置
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Configuration
@EnableTransactionManagement
@MapperScan("org.dsgroup.journeycraft.**.mapper")
public class DataSourceConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * 配置事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}