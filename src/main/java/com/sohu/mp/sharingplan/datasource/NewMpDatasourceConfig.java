package com.sohu.mp.sharingplan.datasource;

import com.sohu.mp.sharingplan.enums.DataSourceTypeEnum;
import com.sohu.mp.sharingplan.util.DataSourceUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@MapperScan(basePackages = "com.sohu.mp.sharingplan.dao.mp.impl", sqlSessionFactoryRef = NewMpDatasourceConfig.SQL_SESSION_FACTORY_NAME)
public class NewMpDatasourceConfig {

    static final String SQL_SESSION_FACTORY_NAME = "newMpSessionFactory";

    @Value("${spring.datasource.min-poolsize}")
    private int minimumPoolSize;

    @Value("${spring.datasource.max-poolsize}")
    private int maximumPoolSize;

    @Value("${spring.datasource.connect-timeout-ms}")
    private long connectTimeout;

    @Value("${spring.datasource.validate-timeout-ms}")
    private long validateTimeout;

    @Value("${spring.datasource.accounts.write.url}")
    private String writeUrl;

    @Value("${spring.datasource.accounts.write.username}")
    private String writeUsername;

    @Value("${spring.datasource.accounts.write.password}")
    private String writePassword;

    @Value("${spring.datasource.accounts.read.url}")
    private String readUrl;

    @Value("${spring.datasource.accounts.read.username}")
    private String readUsername;

    @Value("${spring.datasource.accounts.read.password}")
    private String readPassword;

    @Bean(name = "newMpReadHikariConfig")
    public HikariConfig newMpReadHikariConfig() {
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl(readUrl);
        conf.setUsername(readUsername);
        conf.setPassword(readPassword);
        conf.setConnectionTestQuery("SELECT 1");
        conf.setPoolName("new-mp-read-datasource");
        conf.setConnectionTimeout(connectTimeout);
        conf.setValidationTimeout(validateTimeout);
        conf.setMaximumPoolSize(maximumPoolSize);
        return conf;
    }

    @Bean(name = "newMpWriteDataSource")
    public DataSource newMpWriteDataSource() {
        return DataSourceUtil.getAtomikosXADataSource("new-mp-write-datasource", writeUrl, writeUsername,
                writePassword, minimumPoolSize, maximumPoolSize);
    }

    @Bean(name = "newMpReadDataSource")
    @Primary
    public DataSource newMpReadDataSource() {
        return new HikariDataSource(newMpReadHikariConfig());
    }

    @Bean(name = "newMpDataSource")
    public AbstractRoutingDataSource mpDataSource() {
        DynamicDatasource dynamicDatasource = new DynamicDatasource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceTypeEnum.WRITE.getName(), newMpWriteDataSource());
        targetDataSources.put(DataSourceTypeEnum.READ.getName(), newMpReadDataSource());
        dynamicDatasource.setTargetDataSources(targetDataSources);

        dynamicDatasource.afterPropertiesSet();
        return dynamicDatasource;
    }

    @Bean(name = NewMpDatasourceConfig.SQL_SESSION_FACTORY_NAME)
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(mpDataSource());
        sqlSessionFactoryBean.setTransactionFactory(new MpTransactionsFactory());
        sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactoryBean.getObject();
    }
}

