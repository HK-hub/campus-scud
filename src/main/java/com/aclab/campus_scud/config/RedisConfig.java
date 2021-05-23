package com.aclab.campus_scud.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author 31618
 * @description: Redis 配置类
 * @date 2021-05-22 16:27
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableCaching  //开启缓存注解
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)    //注解，开启redis集中session管理
public class RedisConfig {

	// 以下redisTemplate自由根据场景选择
	//默认的String-String: <String, String>
	@Bean
	//@ConditionalOnMissingBean(StringRedisTemplate.class)
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
	/*@Bean
	public RedisTemplate defaultRedisTemplate(RedisConnectionFactory factory){
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setConnectionFactory(factory);
		return stringRedisTemplate ;
	}*/




	/**
	 *
	 * <String, Object>
	 *
	 * */
	@Bean
	//@ConditionalOnMissingBean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(
			RedisConnectionFactory redisConnectionFactory) {

		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(jackson2JsonRedisSerializer);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashKeySerializer(jackson2JsonRedisSerializer);
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

	/*@Bean
	public RedisTemplate<String,Object> objectRedisTemplate(RedisConnectionFactory factory){
		RedisTemplate<String,Object>template=new RedisTemplate<>();
		//关联
		template.setConnectionFactory(factory);
		//设置key的序列化器
		template.setKeySerializer(new StringRedisSerializer());

		//GenericJackson2JsonRedisSerializer方便反序列化，redis中也方便查看json
		//设置value的序列化器
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.afterPropertiesSet();
		return template;
	}*/






	/**
	 *
	 * <Object, Object>
	 *
	 * */
	@Bean
	public RedisTemplate<Object,Object> objectObjectRedisTemplate(RedisConnectionFactory factory){
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		//使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
		Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		//mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); 过期了通过直接调用 activeDefaultTyping
		mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,ObjectMapper.DefaultTyping.NON_FINAL,JsonTypeInfo.As.WRAPPER_ARRAY);
		serializer.setObjectMapper(mapper);
		//使用StringRedisSerializer来序列化和反序列化redis的key值
		template.setKeySerializer(new StringRedisSerializer());
		//使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
		template.setValueSerializer(serializer);
		// 设置hash key 和value序列化模式
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(serializer);
		template.afterPropertiesSet();
		return template;

	}


	/**
	 * redis作为缓存
	 * @param
	 * @return
	 */
	@Bean
	public CacheManager cacheManager(RedisTemplate<String, Object> template) {
		RedisCacheConfiguration defaultCacheConfiguration =
				RedisCacheConfiguration
						.defaultCacheConfig()
						// 设置key为String
						.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getStringSerializer()))
						// 设置value 为自动转Json的Object
						.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getValueSerializer()))
						// 不缓存null
						.disableCachingNullValues()
						// 缓存数据保存1小时
						.entryTtl(Duration.ofHours(1));
		RedisCacheManager redisCacheManager =
				RedisCacheManager.RedisCacheManagerBuilder
						// Redis 连接工厂
						.fromConnectionFactory(template.getConnectionFactory())
						// 缓存配置
						.cacheDefaults(defaultCacheConfiguration)
						// 配置同步修改或删除 put/evict
						.transactionAware()
						.build();
		return redisCacheManager;
	}



}
