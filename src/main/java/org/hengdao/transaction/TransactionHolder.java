package org.hengdao.transaction;

import org.springframework.transaction.TransactionStatus;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 事务持有者
 * Created by wangdi on 14-11-20.
 *
 * @author barney.wang
 */
public class TransactionHolder {

    private static ThreadLocal<Map<DataSource, LinkedList<TransactionWrapper>>> tranTreeHolder = new ThreadLocal<Map<DataSource, LinkedList<TransactionWrapper>>>();
    private static ThreadLocal<DataSource> dsHolder = new ThreadLocal<DataSource>();
    private static ThreadLocal<TransactionWrapper> txInfoHolder = new ThreadLocal<TransactionWrapper>();
    private static ThreadLocal<Map<TransactionStatus, DataSource>> statusDSHolder = new ThreadLocal<Map<TransactionStatus, DataSource>>();

    public static void addStatusDS(TransactionStatus status, DataSource ds) {
        Map<TransactionStatus, DataSource> map = statusDSHolder.get();
        if (map == null) {
            map = new HashMap<TransactionStatus, DataSource>();
            statusDSHolder.set(map);
        }
        //
        map.put(status, ds);
    }

    public static DataSource removeStatusDS(TransactionStatus status) {
        Map<TransactionStatus, DataSource> map = statusDSHolder.get();
        if (map != null) {
            DataSource ds = map.remove(status);
            if (map.isEmpty()) {
                statusDSHolder.remove();
            }
            return ds;
        }
        return null;
    }

    public static void setDataSource(DataSource ds) {
        dsHolder.set(ds);
    }

    public static DataSource getDataSource() {
        return dsHolder.get();
    }

    public static void addTxInfo2Tree(DataSource ds, TransactionWrapper txInfo) {
        Map<DataSource, LinkedList<TransactionWrapper>> map = tranTreeHolder.get();
        if (map == null) {
            map = new LinkedHashMap<DataSource, LinkedList<TransactionWrapper>>();
            tranTreeHolder.set(map);
        }
        //
        LinkedList<TransactionWrapper> subTree = map.get(ds);
        if (subTree == null) {
            subTree = new LinkedList<TransactionWrapper>();
            map.put(ds, subTree);
        }
        //
        subTree.add(txInfo);
    }

   public  static Map<DataSource, LinkedList<TransactionWrapper>> getTxTree() {
        return tranTreeHolder.get();
    }

    public static void setTransactionInfo(TransactionWrapper txInfo) {
        txInfoHolder.set(txInfo);
    }

    public static TransactionWrapper getTransactionInfo() {
        return txInfoHolder.get();
    }

    static void clearAll() {
        tranTreeHolder.remove();
        dsHolder.remove();
        txInfoHolder.remove();
        statusDSHolder.remove();
    }
}
