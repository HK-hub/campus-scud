package com.aclab.campus_scud.controller;

import com.aclab.campus_scud.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 31618
 * @description
 * @date 2021-05-12 17:20
 */

@Api(value = "用户管理")
@Controller
public class UserController {

	@Autowired
	UserServiceImpl userService ;

	@GetMapping("/test")
	public void print(){

		System.out.println("这是一个测试请求");


	}



}
