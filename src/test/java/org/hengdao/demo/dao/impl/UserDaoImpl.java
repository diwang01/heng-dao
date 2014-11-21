package org.hengdao.demo.dao.impl;

import org.hengdao.demo.Model.UserBean;
import org.hengdao.demo.dao.UserDaoIF;
import org.hengdao.shard.ShardParam;
import org.mybatis.spring.support.SqlSessionDaoSupport;

/**
 * 
 * @author barney.wang
 */
public class UserDaoImpl extends SqlSessionDaoSupport implements UserDaoIF {

	@Override
	public boolean insertUser(UserBean user) {
		ShardParam shardParam = new ShardParam("Shard_User", user.getId(), user);
        
        return getSqlSession().insert("demo.insertUser", shardParam) > 0;
	}

}
