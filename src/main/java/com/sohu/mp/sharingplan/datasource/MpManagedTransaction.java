package com.sohu.mp.sharingplan.datasource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义事务管理主要解决springManagedTransaction事务内不能切换数据源的问题
 */
public class MpManagedTransaction extends SpringManagedTransaction {
    private static final Log LOGGER = LogFactory.getLog(MpManagedTransaction.class);

    private DataSource dataSource;
    private ConcurrentHashMap<String, Connection> map = new ConcurrentHashMap<>();

    MpManagedTransaction(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        DynamicDatasource dynamicDatasource = (DynamicDatasource) dataSource;
        String key = dynamicDatasource.getCurrentDatasourceKey();
        if (map.containsKey(key)) {
            return map.get(key);
        }
        Connection con = dataSource.getConnection();
        map.put(key, con);
        return con;
    }

    /**
     * commit和rollback暂时应该都可以不用重写 因为我们都是autocommit的
     *
     * @throws SQLException
     */
    @Override
    public void commit() throws SQLException {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            return;
        }
        for (Connection conn : map.values()) {
            if (conn != null && !conn.getAutoCommit()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Committing JDBC Connection [" + conn + "]");
                }
                conn.commit();
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            return;
        }
        for (Connection conn : map.values()) {
            if (conn != null && !conn.getAutoCommit()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Rollback JDBC Connection [" + conn + "]");
                }
                conn.commit();
            }
        }
    }

    @Override
    public void close() throws SQLException {
        map.values().forEach(p -> DataSourceUtils.releaseConnection(p, dataSource));
    }
}