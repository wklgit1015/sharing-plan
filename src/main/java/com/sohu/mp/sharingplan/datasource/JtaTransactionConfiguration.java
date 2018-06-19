package com.sohu.mp.sharingplan.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Created by zouchangjing on 2017/6/26.
 */
@Configuration
public class JtaTransactionConfiguration {
    @Autowired
    private AtomikosJtaConfiguration jtaConfiguration;

    @Bean(name = "mpTransaction")
    public PlatformTransactionManager platformTransactionManager() throws Throwable {
        return new JtaTransactionManager(jtaConfiguration.userTransaction(), jtaConfiguration.transactionManager());
    }
}
