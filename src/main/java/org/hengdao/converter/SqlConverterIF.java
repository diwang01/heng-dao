package org.hengdao.converter;

import org.apache.ibatis.executor.statement.StatementHandler;

/**
 * sql代码转换
 * Created by wangdi on 14-11-20.
 * @author  barney.wang
 *
 */
public interface SqlConverterIF {
    /**
     * sql 代码转换
     * @param sql
     * @param statementHandler
     */
     abstract  String convert(String sql,StatementHandler statementHandler);
}
