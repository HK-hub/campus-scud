package com.aclab.campus_scud.controller;

import com.aclab.campus_scud.excption.ResultInfo;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.impl.UserServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 31618
 * @description
 * @date 2021-05-12 17:20
 */
@Slf4j
@Api(value = "用户管理")
@Controller
public class UserController {

	@Autowired
	private UserServiceImpl userService ;
	@Autowired
	private RedisTemplate redisTemplate;

	@GetMapping("/wx/updatePhone")
	@ResponseBody
	public ResultInfo updateUserPhone(@RequestParam("skey")String skey,
									  //@RequestParam("openId")String openId,
	                                  @RequestParam("phone")String phone){
		//通过多重验证的方式来进行用户电话号码的更新: 只要一个条件为真就能更新

		System.out.println("skey: ===== "+skey);
		System.out.println("phone: ======= "+ phone);


		final int skeyCount = userService.count(new QueryWrapper<User>().lambda().eq(User::getSkey, skey ));
		User one = null ;

		//存在用户这样的用户
		if (skeyCount > 0){
			one = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getSkey, skey));
			System.out.println("查询到的用户的skey: "+ one.getSkey());
			System.out.println("查询到的用户的phone: "+ one.getPhone());
			//未绑定电话号码
			if (one.getPhone() == null){
				//绑定电话
				userService.update(new UpdateWrapper<User>().lambda().eq(User::getSkey, skey).set(User::getPhone, phone));
				//将电话存取进入User 用户信息中, 然后返回微信小程序端
				one.setPhone(phone);
			}


		}else{
			//不存在用户: 创建用户, 这种情况不存在, 因为已经获取到了OpenId
			log.info("存在了不存在的用户 skey=" + skey + " phone="+phone);
		}
		//返回修改后的用户数据
		if (one != null){
			redisTemplate.opsForValue().getAndSet("user:_|"+one.getSkey(), one);
			return ResultInfo.success(redisTemplate.opsForValue().get("user:_|"+one.getSkey()));
		}else{
			return ResultInfo.error("用户未存在");
		}
	}



}
