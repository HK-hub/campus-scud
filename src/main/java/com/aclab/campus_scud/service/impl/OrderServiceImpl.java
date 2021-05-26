package com.aclab.campus_scud.service.impl;

import com.aclab.campus_scud.enums.OrderStatusEnum;
import com.aclab.campus_scud.mapper.OrderMapper;
import com.aclab.campus_scud.pojo.FinancialFlow;
import com.aclab.campus_scud.pojo.Order;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.OrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
implements OrderService{

	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
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
		//将订单放入Redis 中
		redisTemplate.opsForList().leftPush("orders", newOrder);

		return newOrder;
	}

	@Override
	public List<Order> getAllOrderBySort() {

		final List redisOrders = redisTemplate.opsForList().range("orders", 0, redisTemplate.opsForList().size("orders"));
		if (redisOrders == null){
			//redis 数据库中没有
			final List<Order> orderList = orderMapper.selectList(new QueryWrapper<Order>().lambda().eq(Order::getOrderStatus, 1)
					.or().eq(Order::getOrderStatus, 0)
					.orderByDesc(Order::getCreateTime, Order::getModifiedTime, Order::getPrice, Order::getExpectedDatetime)
			);
			if (orderList!=null){
				//加入Redis 中
				redisTemplate.opsForList().rightPushAll("orders", orderList);
			}
			//返回经过排序:创建时间, 修改时间, 价格, 期望时间, 后的订单列表
			return orderList;
		}

		return redisOrders;
	}

	@Transactional
	@Override
	public Order updateOrder(Order optOrder) {

		//设置更新内容
		optOrder.setModifiedTime(new Date());

		final int state = orderMapper.update(optOrder, new UpdateWrapper<Order>().lambda()
				.eq(Order::getOrderNumber, optOrder.getOrderNumber()));

		//返回的是匹配的记录数， 而不是影响的记录数
		//开启了事务，不用太担心无法更新， 更新后无需再次获取数据库的Order 订单
		if (state > 0){
			return optOrder;
		}
		return null;
	}

	/**
	 * @Title: 撤销一个订单
	 * @description: 撤销订单，用户主动撤销
	 * @author: 31618
	 * @date: 2021/5/25
	 * @param : Stirng oId
	 * @return:
	 */
	@Override
	public int cancelOrder(String oId) {

		return orderMapper.update(null, new UpdateWrapper<Order>().lambda()
				.eq(Order::getOrderNumber, oId)
				.set(Order::getOrderStatus, OrderStatusEnum.ORDER_REVOKED.getCode()));
	}


}




