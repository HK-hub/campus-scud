package com.aclab.campus_scud.service;

import com.aclab.campus_scud.pojo.Order;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface OrderService extends IService<Order> {

	public Order addNewOrder(String skey, String session_key, JSONObject orderData);


}
