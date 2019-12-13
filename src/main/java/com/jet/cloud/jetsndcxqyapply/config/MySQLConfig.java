package com.jet.cloud.jetsndcxqyapply.config;

import com.jet.cloud.jetsndcxqyapply.common.AES;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.jet.cloud.jetsndcxqyapply.mapper")
public class MySQLConfig {
    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;

    /**
     * 配置数据数据源 master
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.password")
    @Primary
    public DataSource masterDatasource() {
        String decrypt = AES.decrypt(password);
        return DataSourceBuilder.create().url(url).username(username).driverClassName(driverClass).password(decrypt).build();
    }
}
