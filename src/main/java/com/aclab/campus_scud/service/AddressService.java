package com.aclab.campus_scud.service;

import com.aclab.campus_scud.pojo.Address;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface AddressService extends IService<Address> {

	public String getDefaultAddress(String openId,String skey);
	public String getDetailedAddress(String openId);
	public String getSummaryAddress(String openId);



}
