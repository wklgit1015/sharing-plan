package com.sohu.mp.sharingplan.util;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;

/**
 * Created by zouchangjing on 2017/7/7.
 */
public class DataSourceUtil {
    /**
     * 获取支持分布式事务的mysql数据源
     *
     * @param uniqueResourceName
     * @param databaseUrl
     * @param userName
     * @param password
     * @param minPoolSize
     * @param maxPoolSize
     * @return
     */
    public static DataSource getAtomikosXADataSource(String uniqueResourceName, String databaseUrl, String userName,
                                                     String password, int minPoolSize, int maxPoolSize) {
        if (StringUtils.isBlank(uniqueResourceName) || StringUtils.isBlank(databaseUrl) || StringUtils.isBlank(userName)
                || StringUtils.isBlank(password) || minPoolSize < 0 || maxPoolSize < 0) {
            return null;
        }
        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setUrl(databaseUrl);
        mysqlXADataSource.setUser(userName);
        mysqlXADataSource.setPassword(password);
        AtomikosDataSourceBean atomikosDataSource = new AtomikosDataSourceBean();
        atomikosDataSource.setUniqueResourceName(uniqueResourceName);
        atomikosDataSource.setXaDataSource(mysqlXADataSource);
        atomikosDataSource.setMinPoolSize(minPoolSize);
        atomikosDataSource.setMaxPoolSize(maxPoolSize);
        atomikosDataSource.setTestQuery("SELECT 1");
        return atomikosDataSource;
    }
}
