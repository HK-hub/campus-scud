package com.aclab.campus_scud.service;

import com.aclab.campus_scud.pojo.Order;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface OrderService extends IService<Order> {

	public Order addNewOrder(String skey, String session_key, JSONObject orderData);
	public List<Order> getAllOrderBySort();
	public Order updateOrder(Order optOrder);
	public int cancelOrder(String oId);

}
