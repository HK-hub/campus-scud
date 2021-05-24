package com.aclab.campus_scud.service.impl;

import com.aclab.campus_scud.mapper.OrderMapper;
import com.aclab.campus_scud.pojo.FinancialFlow;
import com.aclab.campus_scud.pojo.Order;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.OrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
implements OrderService{

	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private UidGenerateService uidGenerateService;
	@Autowired
	private FinancialFlowServiceImpl financialFlowService;


	@Override
	@Transactional  //开启事务回滚
	public Order addNewOrder(String skey, String session_key, JSONObject orderData) {

		//通过skey 获取用户: 先从redis 中获取用户
		User user = (User)redisTemplate.opsForValue().get("user:_|" + skey);

		//redis 中没有用户， 再去数据库中获取
		if (user == null){
			user = userService.getUserBySkey(skey);
		}

		//获取订单数据
		Order newOrder = new Order();
		newOrder.setTitle(orderData.getString("title"));
		newOrder.setOrderContent(orderData.getString("content"));
		newOrder.setCreator(user.getId());
		//设置订单编号
		newOrder.setOrderNumber(uidGenerateService.getStrUid());
		//1：发布 ， 0：接单， -1：结束
		newOrder.setOrderStatus(1);
		newOrder.setPrice(Double.parseDouble(orderData.getString("price")));
		//订单类型：
		newOrder.setType(orderData.getInteger("type"));
		//支付状态： 如果微信支付足够准确的话， 是已经支付成功了：
		// 1：支付成功， 0：支付失败，-1：退还
		newOrder.setPaymentStatus(1);
		//如果存在照片的话
		//newOrder.setOrderPhoto();

		//设置期望时间
		newOrder.setExpectedDatetime(orderData.getDate("expectDate"));
		//设置送货地点：要送到什么地方
		newOrder.setDeliveryLocation(orderData.getString("deliveryLocation"));
		//设置取货地点：在什么地方取
		newOrder.setPickupLocation(orderData.getString("pickUpLocation"));
		//设置创建时间，更新时间
		newOrder.setCreateTime(new Date());
		newOrder.setModifiedTime(newOrder.getCreateTime());


		//新建财务流水
		FinancialFlow financialFlow = new FinancialFlow();
		financialFlow.setTransactionAmount(newOrder.getPrice());
		//设置收款人：新建订单为平台， 平台：0，发单者：1，接单者：-1
		financialFlow.setPayeeId(0);
		//设置付款人
		financialFlow.setPayerId(user.getId());
		//设置财务流水状态：平台0，发单者(回退)：1，跑腿者：-1
		financialFlow.setFlowState(0);
		financialFlow.setTransactionDate(newOrder.getCreateTime());
		//设置财务状态：接受1， 回退0
		financialFlow.setTransactionState(1);
		financialFlow.setCreateTime(new Date());
		financialFlow.setModifiedTime(financialFlow.getCreateTime());

		//先插入财务流水： 开启了事务回滚
		final boolean finaState = financialFlowService.save(financialFlow);
		//插入财务流水成功： 进行订单插入
		if (finaState){
			//设置财务流水
			newOrder.setFinancialId(financialFlow.getId());
			orderMapper.insert(newOrder);

		}
		return newOrder;
	}
}




