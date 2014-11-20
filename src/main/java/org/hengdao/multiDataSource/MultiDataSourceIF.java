package org.hengdao.multiDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 多数据源支持接口
 *
 * @author  barney.wang
 */
public interface MultiDataSourceIF {

    public DataSource getMainDataSource();

    public Map<String, DataSource> getShardDataSources();
}