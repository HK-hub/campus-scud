package com.aclab.campus_scud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.UserService;
import com.aclab.campus_scud.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

	@Autowired
	UserMapper userMapper ;

	public void test(){

	}

}




