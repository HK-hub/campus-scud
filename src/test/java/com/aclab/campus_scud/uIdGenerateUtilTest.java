package com.aclab.campus_scud;

import com.aclab.campus_scud.service.impl.UidGenerateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 31618
 * @description: Id 生成器测试类
 * @date 2021-05-24 20:43
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class uIdGenerateUtilTest {

	@Autowired
	private UidGenerateService uIdGenerateService;


	@Test
	public void testUidGenerateService(){

		log.info("ID生成器测试开始");
		log.info(uIdGenerateService.getStrUid());

	}



}
