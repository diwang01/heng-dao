package org.hengdao.strategy;

/**
 * Created by wangdi on 14-11-20.
 * @author  barney.wang
 */
public class StrategyHolder {

    private static ThreadLocal<ShardStrategy> holder = new ThreadLocal<ShardStrategy>();

    public static ShardStrategy getShardStrategy() {
        return holder.get();
    }

    public static void removeShardStrategy() {
        holder.remove();
    }

    public static void setShardStrategy(ShardStrategy shardStrategy) {
        holder.set(shardStrategy);
    }
}
