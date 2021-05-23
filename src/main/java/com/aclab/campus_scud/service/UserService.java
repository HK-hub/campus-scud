package com.aclab.campus_scud.service;

import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.pojo.WeChatUserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 */
@Mapper
public interface UserService extends IService<User> {

	public boolean updateWeChatUser(WeChatUserInfo weChatUserInfo);
	public boolean insertWeChatUser(WeChatUserInfo weChatUserInfo);


}
