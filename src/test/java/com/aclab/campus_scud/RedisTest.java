package com.aclab.campus_scud;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 31618
 * @description: redis 的测试类
 * @date 2021-05-23 13:52
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RedisTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;


	@Test
	public void testRedis(){
		//键值对存储
		stringRedisTemplate.opsForValue().set("name", "HK");
		stringRedisTemplate.opsForValue().set("age", "18");
		final String name = stringRedisTemplate.opsForValue().get("name");
		System.out.println("姓名: " + name);
		final String age = stringRedisTemplate.opsForValue().get("age");
		System.out.println("年龄: " + age);

		System.out.println("--------------------------------------");

		//List 存储
		redisTemplate.opsForList().leftPushAll("names", "李四","王五","赵六");
		List names = this.redisTemplate.opsForList().range("names", 0, 3);
		log.info(names.toString());

		//hash 存储
		Map<String, String> map = new HashMap<>(4);
		map.put("name","秦始皇");
		map.put("age","未知");
		map.put("sex","男");
		this.redisTemplate.opsForHash().putAll("HASH_PERSON",map);
		String s =(String) this.redisTemplate.opsForHash().get("HASH_PERSON", "name");
		log.info(s);


	}






}
