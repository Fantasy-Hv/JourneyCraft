package org.dsgroup.journeycraft.common.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 数据源和MyBatis配置类
 * <p>
 * 显式配置SqlSessionFactory和事务管理器
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
     * 配置MyBatis-Plus的SqlSessionFactory
     * 使用MybatisSqlSessionFactoryBean而不是原生的SqlSessionFactoryBean
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // 设置MyBatis配置（可选）
        // org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // configuration.setMapUnderscoreToCamelCase(true);
        // sessionFactory.setConfiguration(configuration);
        
        // 设置类型别名包扫描
        sessionFactory.setTypeAliasesPackage("org.dsgroup.journeycraft.**.entity");
        
        // 设置Mapper XML文件位置（如果有的话）
        // PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/**/*.xml"));
        
        return sessionFactory.getObject();
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}