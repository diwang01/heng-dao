package org.hengdao.support;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hengdao.shard.ShardParam;
import org.hengdao.strategy.NoShardStrategy;
import org.hengdao.strategy.ShardStrategy;
import org.hengdao.strategy.StrategyHolder;
import org.hengdao.transaction.TransactionHolder;
import org.hengdao.transaction.TransactionWrapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAttribute;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Created by wangdi on 14-11-20.
 * @author  barney.wang
 */
public class SqlSessionSupport implements InitializingBean {
    private SqlSessionFactoryBean sqlSessionFactoryBean;

    private Map<DataSource, SqlSessionTemplate> dataSourceMap;

    private SqlSession sqlSession;

    {
        sqlSession = (SqlSession) Proxy.newProxyInstance(SqlSessionSupport.class.getClassLoader(),
                new Class[]{SqlSession.class}, new SessionHandler());
    }

    @Autowired(required = false)
    public final void setSqlSessionFactory(SqlSessionFactoryBean sqlSessionFactoryBean) {
        this.sqlSessionFactoryBean = sqlSessionFactoryBean;
    }

    public final SqlSession getSqlSession() {
        return sqlSession;
    }

    @Override
    public final void afterPropertiesSet() throws Exception {
        sqlSessionFactoryBean.afterPropertiesSet();
        //
        dataSourceMap = new LinkedHashMap<DataSource, SqlSessionTemplate>();
        dataSourceMap.put(sqlSessionFactoryBean.getMainDataSource(),
                new SqlSessionTemplate(sqlSessionFactoryBean.getMainSqlSessionFactory()));

        Map<String, DataSource> shardDataSources = sqlSessionFactoryBean.getShardDataSources();
        if (shardDataSources != null) {
            for (Map.Entry<String, DataSource> entry : shardDataSources.entrySet()) {
                SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getShardSqlSessionFactory().get(
                        entry.getKey());
                dataSourceMap.put(entry.getValue(), new SqlSessionTemplate(sqlSessionFactory));
            }
        }
    }

    private class SessionHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                // 默认DataSource为MainDataSource
                DataSource targetDS = sqlSessionFactoryBean.getMainDataSource();

                //
                if (args == null || args.length == 0) {
                    // 准备事务
                    prepareTx(targetDS);
                    //
                    return method.invoke(dataSourceMap.get(sqlSessionFactoryBean.getMainDataSource()), args);
                }
                if (!(args[0] instanceof String)) {
                    // 准备事务
                    prepareTx(targetDS);
                    //
                    return method.invoke(dataSourceMap.get(sqlSessionFactoryBean.getMainDataSource()), args);
                }
                ShardParam shardParam = (args.length > 1 && args[1] instanceof ShardParam) ? (ShardParam) args[1]
                        : null;
                if (shardParam == null) {
                    // 准备事务
                    prepareTx(targetDS);
                    //
                    return method.invoke(dataSourceMap.get(sqlSessionFactoryBean.getMainDataSource()), args);
                } else {
                    args[1] = shardParam.getParams();
                }

                //
                String statement;
                String shardStrategyName;
                ShardStrategy shardStrategy;

                statement = (String) args[0];
                shardStrategyName = shardParam.getName();

                shardStrategy = sqlSessionFactoryBean.getShardStrategyMap().get(shardStrategyName);
                if (shardStrategy == null) {
                    shardStrategy = NoShardStrategy.INSTANCE;
                }

                Configuration configuration = sqlSessionFactoryBean.getMainSqlSessionFactory().getConfiguration();
                MappedStatement mappedStatement = configuration.getMappedStatement(statement);
                BoundSql boundSql = mappedStatement.getBoundSql(wrapCollection(shardParam.getParams()));

                shardStrategy.setMainDataSource(sqlSessionFactoryBean.getMainDataSource());
                shardStrategy.setShardDataSources(sqlSessionFactoryBean.getShardDataSources());
                shardStrategy.setShardParam(shardParam);
                shardStrategy.setSql(boundSql.getSql());
                //
                StrategyHolder.setShardStrategy(shardStrategy);
                // 重新指定目标DataSource
                targetDS = shardStrategy.getTargetDataSource();
                SqlSessionTemplate sqlSessionTemplate = null;
                if (targetDS == null || (sqlSessionTemplate = dataSourceMap.get(targetDS)) == null) {
                    targetDS = sqlSessionFactoryBean.getMainDataSource();
                    sqlSessionTemplate = dataSourceMap.get(targetDS);
                }

                // 准备事务
                prepareTx(targetDS);

                return method.invoke(sqlSessionTemplate, args);
            } finally {
                StrategyHolder.removeShardStrategy();
            }
        }

        /**
         * 、 准备事务
         *
         * @param targetDS
         */
        private void prepareTx(DataSource targetDS) {
            //
            TransactionHolder.setDataSource(targetDS);

            // for transaction
            TransactionWrapper txInfo = TransactionHolder.getTransactionInfo();
            if (txInfo != null) {
                TransactionAttribute attr = txInfo.getTransactionAttribute();
                if (attr != null) {
                    createTxIfAbsent(targetDS, txInfo);
                }
            }
        }

        /**
         * 如果不存在则创建事务
         *
         * @param targetDS
         * @param txInfo
         */
        private void createTxIfAbsent(DataSource targetDS, TransactionWrapper txInfo) {
            Map<DataSource, LinkedList<TransactionWrapper>> txTree = TransactionHolder.getTxTree();
            if (txTree == null || !txTree.containsKey(targetDS)) {
                createTx(targetDS, txInfo);
            }
        }

        private void createTx(DataSource targetDS, TransactionWrapper txInfo) {
            TransactionStatus txStatus = txInfo.getTransactionManager()
                    .getTransaction(txInfo.getTransactionAttribute());
            // txStatus = new TransactionStatusWrap((DefaultTransactionStatus)
            // txStatus);
            TransactionHolder.addStatusDS(txStatus, targetDS);
            //
            TransactionWrapper txInfoCopy = txInfo.newCopy();
            txInfoCopy.newTransactionStatus(txStatus);
            //
            TransactionHolder.addTxInfo2Tree(targetDS, txInfoCopy);
        }

        private Object wrapCollection(final Object object) {
            if (object instanceof List) {
                return new HashMap<String, Object>() {
                    private static final long serialVersionUID = -2533602760878803345L;
                    {
                        put("list", object);
                    }
                };
            } else if (object != null && object.getClass().isArray()) {
                return new HashMap<String, Object>() {
                    private static final long serialVersionUID = 8371167260656531195L;
                    {
                        put("array", object);
                    }
                };
            }
            return object;
        }
    }
}
