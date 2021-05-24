package com.aclab.campus_scud.service.impl;

import com.aclab.campus_scud.mapper.AddressMapper;
import com.aclab.campus_scud.mapper.UserMapper;
import com.aclab.campus_scud.pojo.Address;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.AddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address>
implements AddressService {

	@Autowired
	private AddressMapper addressMapper ;
	@Autowired
	private UserMapper userMapper ;
	@Autowired
	private RedisTemplate redisTemplate ;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;


	@Override
	public String getDefaultAddress(String openId, String skey) {

		//先从Redis 中获取
		final String defaultAddress = stringRedisTemplate.opsForValue().get("defaultAddress:_|" + skey);

		//redis 中没有， 再去 数据库获取
		if (defaultAddress == null || defaultAddress.equals("")){

			final User selectOne = userMapper.selectOne(new QueryWrapper<User>().lambda()
					.select(User::getDefaultAddress).eq(User::getSkey, skey));

			if (selectOne.getDefaultAddress() == null){
				//数据库中没有默认地址

				//去地址中拼装一个默认地址
				Address addressBuilder = addressMapper.selectOne(new QueryWrapper<Address>().lambda()
						.eq(Address::getUserId, selectOne.getId()));

				//拼装地址
				final String assemblyAddress = addressBuilder.getAssemblyAddress();

				//设置用户默认地址
				selectOne.setDefaultAddress(assemblyAddress);
				//更新数据库
				userMapper.update(selectOne, new UpdateWrapper<User>().lambda()
							.eq(User::getId, selectOne.getId()));

				//返回拼装地址
				return assemblyAddress;
			}

			//数据库中有默认地址
			return selectOne.getDefaultAddress();
		}
		//redis 中的是默认地址

		return defaultAddress;

	}

	@Override
	public String getDetailedAddress(String openId) {
		return null;
	}

	@Override
	public String getSummaryAddress(String openId) {
		return null;
	}
}




