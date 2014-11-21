package org.hengdao.shard;

/**
 * Created by wangdi on 14-11-20.
 *
 * 分表参数
 */
public class ShardParam {

    public static final ShardParam NO_SHARD = new ShardParam();

    private String name;
    private Object shardValue;
    private Object params;

    public ShardParam() {
    }

    public ShardParam(String name, Object shardValue, Object params) {
        super();
        this.name = name;
        this.shardValue = shardValue;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getShardValue() {
        return shardValue;
    }

    public void setShardValue(Object shardValue) {
        this.shardValue = shardValue;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

	@Override
	public String toString() {
		return "ShardParam [name=" + name + ", shardValue=" + shardValue
				+ ", params=" + params + "]";
	}

   
}
