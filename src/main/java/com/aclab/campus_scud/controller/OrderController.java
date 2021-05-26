package com.aclab.campus_scud.controller;

import com.aclab.campus_scud.enums.UserStatusEnum;
import com.aclab.campus_scud.excption.CommonEnum;
import com.aclab.campus_scud.excption.ResultInfo;
import com.aclab.campus_scud.pojo.Order;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.impl.AddressServiceImpl;
import com.aclab.campus_scud.service.impl.OrderServiceImpl;
import com.aclab.campus_scud.service.impl.UidGenerateService;
import com.aclab.campus_scud.service.impl.UserServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 31618
 * @description
 * @date 2021-05-23 15:06
 */
@Api(value = "用户订单处理")
@RestController
public class OrderController {

	@Autowired
	private UidGenerateService uidGenerateService ;
	@Autowired
	private OrderServiceImpl orderService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private AddressServiceImpl addressService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;



	/**
	 * @Title: 新增一个订单
	 * @description:
	 * @author: 31618
	 * @date: 2021/5/24
	 * @param : 前端订单需要的信息，用户信息skey
	 * @return: 新增的订单的订单ID
	 */
	@ApiOperation(value = "新增订单", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "订单基本数据", value = "标题,内容,地点,类型等", type = "JSONObject:order", required = true, paramType = "query"),
			@ApiImplicitParam(name = "用户Skey", value = "user.skey", type = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "session_key",value = "业务校验",type = "String", paramType = "query")
	})
	@ApiResponses({
			@ApiResponse(code = 200,message = "新增订单成功",responseContainer = "新增的订单"),
			@ApiResponse(code = 202,message = "用户未登录", responseContainer = "错误信息"),
			@ApiResponse(code = 401,message = "数字签名不匹配", responseContainer = "错误信息"),
			@ApiResponse(code = 201, message = "非法参数"),
			@ApiResponse(code = 404, message = "请求资源不存在"),
			@ApiResponse(code = 400, message = "请求数据错误"),
			@ApiResponse(code = 500, message = "服务器内部错误")
	})
	@PostMapping("/order/newOrder")
	public ResultInfo addNewOrder(@RequestParam(name = "order") JSONObject orderData,
	                              @RequestParam(name = "skey")String skey,
                                  @RequestParam(name = "session_key")String sessionKey
	){
		//校验登录状态
		final String redisSkey = stringRedisTemplate.opsForValue().get("skey:_|" + skey);
		final User redisUser = (User)redisTemplate.opsForValue().get("user:_|" + skey);
		Order newOrder = null;
		//用户未登录
		if (redisSkey == null && redisUser == null){
			ResultInfo.error(CommonEnum.USER_NOT_LOGIN);
		}else {
			//用户已登录->进行校验数字业务签名
			final String redisSessionKey = stringRedisTemplate.opsForValue().get("session_key:_|" + sessionKey);
			if(redisUser.getSessionKey().equals(redisSessionKey)){
				//业务数字签名校验通过->进行业务操作
				newOrder = orderService.addNewOrder(skey, sessionKey, orderData);
			}else{
				//数字签名校验不通过->
				ResultInfo.error(CommonEnum.SIGNATURE_NOT_MATCH);
			}
		}
		//服务器内部错误
		if (newOrder == null){
			ResultInfo.error(CommonEnum.INTERNAL_SERVER_ERROR);
		}

		//将订单放入Redis 中
		redisTemplate.opsForList().leftPush("orders", newOrder);
		//返回请求成功：返回新增加的订单
		return ResultInfo.success(newOrder);
	}



	/**
	 * @Title: 修改订单
	 * @description: 修改订单的信息：
	 * @author: 31618
	 * @date: 2021/5/25
	 * @param :订单编号， 用户skey , 用户session_key
	 * @return:
	 */
	@ApiOperation(value = "修改一个订单", httpMethod = "GET",response = ResultInfo.class ,responseContainer = "修改后的订单Order 对象")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "optOrder", value = "需要被修改的订单对象", dataType = "Order", type = "update", required = true),
			@ApiImplicitParam(name = "skey", value = "登录状态码", dataType = "String", required = true),
			@ApiImplicitParam(name = "session_key", value = "业务校验码", dataType = "String", required = true)
	})
	@ApiResponses({
			@ApiResponse(code = 200,message = "修改订单成功",responseContainer = "修改后的订单"),
			@ApiResponse(code = 202,message = "用户未登录", responseContainer = "错误信息"),
			@ApiResponse(code = 401,message = "数字签名不匹配", responseContainer = "错误信息"),
			@ApiResponse(code = 201, message = "非法参数"),
			@ApiResponse(code = 404, message = "请求资源不存在"),
			@ApiResponse(code = 400, message = "请求数据错误"),
			@ApiResponse(code = 500, message = "服务器内部错误")
	})
	@GetMapping("/order/updateOrder")
	public ResultInfo updateOneOrder(@RequestBody(required = true) Order optOrder,
	                                 @RequestParam(name = "skey")String skey,
	                                 @RequestParam(name = "session_key")String session_key){
		//先校验登录状态
		final String redisSkey = stringRedisTemplate.opsForValue().get("skey:_|" + skey);
		//用户未登录： 返回错误信息
		if (redisSkey == null){
			return ResultInfo.error(CommonEnum.USER_NOT_LOGIN);
		}

		//在校验数字签名：
		final String redisSessionKey = stringRedisTemplate.opsForValue().get("session_key:_|" + session_key);
		if (redisSessionKey == null) {
			//用户数字签名不正确
			return ResultInfo.error(CommonEnum.SIGNATURE_NOT_MATCH);
		}

		//进行业务：更新订单
		final Order updateOrder = orderService.updateOrder(optOrder);

		//判断更新操作
		if (updateOrder == null){
			//更新订单失败: 服务器内部错误
			return ResultInfo.error(CommonEnum.INTERNAL_SERVER_ERROR);
		}

		//更新成功：
		return ResultInfo.success(updateOrder);
	}

	/**
	 * @Title: 删除一个订单，撤销订单
	 * @description: 是用户主动删除，撤销订单，不是完成订单后的消除
	 * @author: 31618
	 * @date: 2021/5/25
	 * @param : seky , session_key , order_number
	 * @return:
	 */
	@ApiOperation(value = "用户撤销订单")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Order UID",value = "需要被删除的订单编号",dataType = "String",required = true),
			@ApiImplicitParam(name = "skey" ,value = "用户登录状态码",dataType = "String",required = true),
			@ApiImplicitParam(name = "session_key",value = "业务校验码",dataType = "String", required = true)
	})
	@ApiResponses({
			@ApiResponse(code=200,message = "撤销成功"),
			@ApiResponse(code=202,message = "用户未登录"),
			@ApiResponse(code=401,message = "数字签名不匹配"),
			@ApiResponse(code=500,message = "服务器内部错误"),
	})
	@GetMapping("/order/cancelOrder")
	public ResultInfo cancelOrder(@RequestParam("oId")String oId,
	                              @RequestParam("skey")String skey,
	                              @RequestParam("session_key")String session_key){
		//先判断登录状态
		if (userService.getUserLoginStatus(skey).equals(UserStatusEnum.USER_LOGGED_IN)){
			//用户已经登录：校验签名
			if (userService.getUserSignatureStatus(session_key).equals(UserStatusEnum.USER_IDENTITY_LEGAL)) {
				//数字签名校验通过

				//进行业务:先取除Redis 中的缓存
				final Order one = orderService.getOne(new QueryWrapper<Order>().lambda().eq(Order::getOrderNumber, oId));
				redisTemplate.opsForList().remove("orders", 0, one);

				//在将数据库中的订单状态修改
				orderService.cancelOrder(oId);
				//修改流水中的状态

			}else{
				//数字签名校验不通过
				return ResultInfo.error(CommonEnum.SIGNATURE_NOT_MATCH);
			}
		}else{
			//未登录
			return ResultInfo.error(CommonEnum.USER_NOT_LOGIN);
		}

		return ResultInfo.success("撤销订单成功");
		
	}

	/**
	 * @Title: 跑腿完成订单，用户完成订单
	 * @description: 
	 * @author: 31618
	 * @date: 2021/5/25
	 * @param null: 
	 * @return: 
	 */




	
	

	/**
	 * @Title: 查询所有跑腿订单
	 * @description: 将所有的订单查询出来放在 Redis 中, 每次更新Redis 中的
	 * @author: 31618
	 * @date: 2021/5/24
	 * @param :
	 * @return: 
	 */
	@ApiOperation(value = "获取所有经过排序了的订单", httpMethod = "GET")
	@ApiResponses({
			@ApiResponse(code = 200,message = "返回订单列表成功",responseContainer = "JSONObject , 订单的List 集合的JSON数据"),
			@ApiResponse(code = 500,message = "服务器内部错误",responseContainer = "String")
	})
	@GetMapping("/order/allOrder")
	public ResultInfo getAllOrder(){

		//先从Redis 中获取

		//没有再从数据库中获取
		final List<Order> allOrderBySort = orderService.getAllOrderBySort();

		return ResultInfo.success(allOrderBySort) ;

	}







}
