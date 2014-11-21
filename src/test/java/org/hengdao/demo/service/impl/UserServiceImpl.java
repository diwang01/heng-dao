package org.hengdao.demo.service.impl;

import org.hengdao.demo.Model.UserBean;
import org.hengdao.demo.dao.UserDaoIF;
import org.hengdao.demo.service.UserServiceIF;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangdi on 14-11-21.
 */
public class UserServiceImpl implements UserServiceIF{

	@Autowired
	private UserDaoIF userDao;
	
	@Override
	public void insertUser() {
		 UserBean user1 = new UserBean();
         user1.setId(1);
         user1.setName("1");
         //
         userDao.insertUser(user1);
         
         //
         UserBean user2 = new UserBean();
         user2.setId(2);
         user2.setName("2");
         //
         userDao.insertUser(user2);

         //
         UserBean user3 = new UserBean();
         user3.setId(3);
         user3.setName("3");
         //
         userDao.insertUser(user3);

         //
         UserBean user4 = new UserBean();
         user4.setId(400);
         user4.setName("401");
         //
         userDao.insertUser(user4);
         
         UserBean user5 = new UserBean();
         user5.setId(100);
         user5.setName("100");
         //
         userDao.insertUser(user5);
		
	}

	
}
