package com.aclab.campus_scud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author 31618
 * @description
 * @date 2021-05-22 21:04
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//super.configure(http);
		http.csrf().disable();
		//配置不需要登陆验证
		http.authorizeRequests().anyRequest().permitAll().and().logout().permitAll();
	}



}
