package com.aclab.campus_scud.service.impl;

import cn.amorou.uid.UidGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 31618
 * @description: 订单编号生成器
 * @date 2021-05-24 20:39
 */
@Service
public class UidGenerateService {

	@Resource
	private UidGenerator uidGenerator;

	public long getUid() {
		return uidGenerator.getUID();
	}

	public String getStrUid(){
		return "CS"+this.getUid();
	}

}
