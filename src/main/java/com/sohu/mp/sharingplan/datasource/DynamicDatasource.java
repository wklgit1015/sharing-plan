package com.sohu.mp.sharingplan.datasource;

import com.sohu.mp.sharingplan.enums.DataSourceTypeEnum;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDatasource extends AbstractRoutingDataSource {

    private static class DynamicDataSourceHolder {

        //使用ThreadLocal来保存当前线程需要使用的 dataSource 的 key。保证线程安全
        private static final ThreadLocal<String> holder = new ThreadLocal<>();

        private static void setDataSourceKey(String key) {
            holder.set(key);
        }

        private static String getDataSourceKey() {
            String key = holder.get();
            if (null == key) {
                key = DataSourceTypeEnum.READ.getName();   //默认写库
            }
            return key;
        }

    }

    public static void setWrite() {
        DynamicDataSourceHolder.setDataSourceKey(DataSourceTypeEnum.WRITE.getName());
    }

    public static void setRead() {
        DynamicDataSourceHolder.setDataSourceKey(DataSourceTypeEnum.READ.getName());
    }


    @Override
    protected Object determineCurrentLookupKey() {
        // 暂时一个读库。多个读库时可以在这里做负载均衡
        return DynamicDataSourceHolder.getDataSourceKey();
    }

    public String getCurrentDatasourceKey()
    {
        return DynamicDataSourceHolder.getDataSourceKey();
    }
}
