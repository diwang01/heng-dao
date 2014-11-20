package org.hengdao.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.hengdao.converter.SqlConverter;
import org.hengdao.utils.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * 分表拦截器
 * Created by wangdi on 14-11-20.
 * @author  barney.wang
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class ShardIntercept implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardIntercept.class);

    private SqlConverter sqlConverter;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        String sql = statementHandler.getBoundSql().getSql();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Original Sql [" + sql + "]");
        }

        String targetSql = sqlConverter.convert(sql, statementHandler);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Converted Sql [" + targetSql + "]");
        }

        if (!sql.equals(targetSql)) {
            Reflections.setFieldValue(statementHandler.getBoundSql(), "sql", targetSql);
        }

        return invocation.proceed();    }

    @Override
    public Object plugin(Object target) {
        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public void setSqlConverter(SqlConverter sqlConverter) {
        this.sqlConverter = sqlConverter;
    }
}
