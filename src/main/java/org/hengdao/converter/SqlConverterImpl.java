package org.hengdao.converter;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.hengdao.strategy.NoShardStrategy;
import org.hengdao.strategy.ShardStrategy;
import org.hengdao.strategy.StrategyHolder;

/**
 *  sql代码转换接口实现
 * Created by wangdi on 14-11-20.
 * @author  barney.wang
 */
public class SqlConverterImpl implements SqlConverterIF {


    /**
     * sql 代码转换
     * 如果是非分表策略则不处理
     * @param sql
     * @param statementHandler
     */
    @Override
    public String convert(String sql, StatementHandler statementHandler) {

        ShardStrategy strategy = StrategyHolder.getShardStrategy();

        if (strategy == null || strategy instanceof NoShardStrategy) {
            return sql;
        }
        return strategy.getTargetSql();
    }


}
