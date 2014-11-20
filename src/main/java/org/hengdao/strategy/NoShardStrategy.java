package org.hengdao.strategy;

import javax.sql.DataSource;

/**
 * 不分表策略
 * Created by wangdi on 14-11-20.
 * @author  barney.wang
 */
public class NoShardStrategy extends ShardStrategy{


    public static final NoShardStrategy INSTANCE = new NoShardStrategy();

    @Override
    public String getTargetSql() {
        return getSql();
    }

    @Override
    public DataSource getTargetDataSource() {
        return getMainDataSource();
    }

}
