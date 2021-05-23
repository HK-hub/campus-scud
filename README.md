# 工程简介

# 延伸阅读

# 微信小程序登录
1. 微信小程序登录流程
   微信小程序登录流程涉及到三个角色：小程序、开发者服务器、微信服务器

三者交互步骤如下：

#### 第一步：小程序通过wx.login()获取code。
#### 第二步：小程序通过wx.request()发送code到开发者服务器。
#### 第三步：开发者服务器接收小程序发送的code，并携带appid、appsecret（这两个需要到微信小程序后台查看）、code发送到微信服务器。
#### 第四步：微信服务器接收开发者服务器发送的appid、appsecret、code进行校验。校验通过后向开发者服务器发送session_key、openid。
#### 第五步：开发者服务器自己生成一个skey（自定义登录状态）与openid、session_key进行关联，并存到数据库中（mysql、redis等）。
#### 第六步：开发者服务器返回生成skey（自定义登录状态）到小程序。
#### 第七步：小程序存储skey（自定义登录状态）到本地。
#### 第八步：小程序通过wx.request()发起业务请求到开发者服务器，同时携带skey（自定义登录状态）。
#### 第九步：开发者服务器接收小程序发送的skey（自定义登录状态），查询skey在数据库中是否有对应的openid、session_key。
#### 第十步：开发者服务器返回业务数据到小程序。

![](https://img2020.cnblogs.com/blog/1419013/202008/1419013-20200828144212867-597822069.png)

## 代码
### pox.xml依赖
```xml
<!--hutool具包-->   <dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.4.0</version>
</dependency>
<!--简化代码的工具包-->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<!--  mybatis-plus-spring-boot-starter-->
<dependency>
    <groupId>com.baomidou</groupId>   
    <artifactId>mybatis-plus-boot-starter</artifactId>    
    <version>3.3.2</version>
</dependency>

```

### wx返回的用户信息
```java

/**
 * @Author dw
 * @ClassName WeChatUserInfo
 * @Description 微信用户信息
 * @Date 2020/8/28 14:14
 * @Version 1.0
 */
@Data
public class WeChatUserInfo {
    /**
     * 微信返回的code
     */
    private String code;
    /**
     * 非敏感的用户信息
     */
    private String rawData;
    /**
     * 签名信息
     */
    private String signature;
    /**
     * 加密的数据
     */
    private String encrypteData;
    /**
     * 加密密钥
     */
    private String iv;

}
```
### WeChatUtil 工具类
```java
/**
 * @Author dw
 * @ClassName WeChatUtil
 * @Description
 * @Date 2020/8/28 10:56
 * @Version 1.0
 */
public class WeChatUtil {

	public static JSONObject getSessionKeyOrOpenId(String code) {
		String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
		HashMap<String, Object> requestUrlParam = new HashMap<>();
		//小程序appId
		requestUrlParam.put("appid", "小程序appId");
		//小程序secret
		requestUrlParam.put("secret", "小程序secret");
		//小程序端返回的code
		requestUrlParam.put("js_code", code);
		//默认参数
		requestUrlParam.put("grant_type", "authorization_code");
		//发送post请求读取调用微信接口获取openid用户唯一标识
		String result = HttpUtil.get(requestUrl, requestUrlParam);
		JSONObject jsonObject = JSONUtil.parseObj(result);
		return jsonObject;
	}

	public static JSONObject getUserInfo(String encryptedData, String sessionKey, String iv) throws Base64DecodingException {
		// 被加密的数据
		byte[] dataByte = Base64.decode(encryptedData);
		// 加密秘钥
		byte[] keyByte = Base64.decode(sessionKey);
		// 偏移量
		byte[] ivByte = Base64.decode(iv);
		try {
			// 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
			int base = 16;
			if (keyByte.length % base != 0) {
				int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
				byte[] temp = new byte[groups * base];
				Arrays.fill(temp, (byte) 0);
				System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
				keyByte = temp;
			}
			// 初始化
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
			SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
			parameters.init(new IvParameterSpec(ivByte));
			// 初始化
			cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
			byte[] resultByte = cipher.doFinal(dataByte);
			if (null != resultByte && resultByte.length > 0) {
				String result = new String(resultByte, "UTF-8");
				return JSONUtil.parseObj(result);
			}
		} catch (Exception e) {
		}
		return null;
	}
}
```


### 登录Controller
```java
/**
 * @Author dw
 * @ClassName WeChatUserLoginController
 * @Description
 * @Date 2020/8/28 14:12
 * @Version 1.0
 */
@RestController
public class WeChatUserLoginController {

    @Resource
    private IUserService userService;

    /**
     * 微信用户登录详情
     */
    @PostMapping("wx/login")
    public ResultInfo user_login(@RequestBody WeChatUserInfo weChatUserInfo) throws Base64DecodingException {
        // 2.开发者服务器 登录凭证校验接口 appId + appSecret + 接收小程序发送的code
        JSONObject SessionKeyOpenId = WeChatUtil.getSessionKeyOrOpenId(weChatUserInfo.getCode());
        // 3.接收微信接口服务 获取返回的参数
        String openid = SessionKeyOpenId.get("openid", String.class);
        String sessionKey = SessionKeyOpenId.get("session_key", String.class);
        // 用户非敏感信息：rawData
        // 签名：signature
        JSONObject rawDataJson = JSONUtil.parseObj(weChatUserInfo.getRawData());
        // 4.校验签名 小程序发送的签名signature与服务器端生成的签名signature2 = sha1(rawData + sessionKey)
     //   String signature2 = DigestUtils.sha1Hex(weChatUserInfo.getRawData() + sessionKey);
       // if (!weChatUserInfo.getSignature().equals(signature2)) {
         //   return ResultInfo.error( "签名校验失败");
        //}
        //encrypteData比rowData多了appid和openid
        JSONObject userInfo = WeChatUtil.getUserInfo(weChatUserInfo.getEncrypteData(),
                sessionKey, weChatUserInfo.getIv());
        // 5.根据返回的User实体类，判断用户是否是新用户，是的话，将用户信息存到数据库；不是的话，更新最新登录时间
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(User::getLoginName, openid);
        int userCount = userService.count(userQueryWrapper);
        // uuid生成唯一key，用于维护微信小程序用户与服务端的会话（或者生成Token）
        String skey = UUID.randomUUID().toString();
        if (userCount <= 0) {
            // 用户信息入库
            String nickName = rawDataJson.get("nickName",String.class);
            String avatarUrl = rawDataJson.get("avatarUrl",String.class);
            String gender = rawDataJson.get("gender",String.class);
            String city = rawDataJson.get("city",String.class);
            String country = rawDataJson.get("country",String.class);
            String province = rawDataJson.get("province",String.class);
           // 新增用户到数据库
        } else {
            // 已存在，更新用户登录时间

        }
        //6. 把新的skey返回给小程序
        return ResultInfo.success();
    }


}
```


### 全局返回结果
```java

public class ResultInfo {
    /**
     * 响应代码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应结果
     */
    private Object result;

    public ResultInfo() {
    }

    public ResultInfo(BaseErrorInfoInterface errorInfo) {
        this.code = errorInfo.getResultCode();
        this.message = errorInfo.getResultMsg();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * 成功
     *
     * @return
     */
    public static ResultInfo success() {
        return success(null);
    }

    /**
     * 成功
     * @param data
     * @return
     */
    public static ResultInfo success(Object data) {
        ResultInfo rb = new ResultInfo();
        rb.setCode(CommonEnum.SUCCESS.getResultCode());
        rb.setMessage(CommonEnum.SUCCESS.getResultMsg());
        rb.setResult(data);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultInfo error(BaseErrorInfoInterface errorInfo) {
        ResultInfo rb = new ResultInfo();
        rb.setCode(errorInfo.getResultCode());
        rb.setMessage(errorInfo.getResultMsg());
        rb.setResult(null);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultInfo error(String code, String message) {
        ResultInfo rb = new ResultInfo();
        rb.setCode(code);
        rb.setMessage(message);
        rb.setResult(null);
        return rb;
    }

    /**
     * 失败
     */
    public static ResultInfo error(String message) {
        ResultInfo rb = new ResultInfo();
        rb.setCode("-1");
        rb.setMessage(message);
        rb.setResult(null);
        return rb;
    }}
```



## 微信小程序端
```html
<view class="container">
  <!-- 登录组件 https://developers.weixin.qq.com/miniprogram/dev/api/wx.getUserInfo.html -->  
  <button wx:if="{{!hasUserInfo}}" open-type="getUserInfo" bind:getuserinfo="onGetUserInfo">授权登录</button>
  <!-- 登录后使用open-data -->
  <view class="avatar-container avatar-position">
      <image src="{{userInfo.avatarUrl}}" wx:if="{{hasUserInfo}}" class="avatar" />
      <open-data wx:if="{{hasUserInfo}}" type="userNickName"></open-data>
  </view>
</view>
```

```js
// pages/me/me.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    hasUserInfo: false,
    userInfo: null
  },

  onLoad: function() {
    // 页面加载时使用用户授权逻辑，弹出确认的框  
    this.userAuthorized()
  },
  
  userAuthorized() {
    wx.getSetting({
      success: data => {
        if (data.authSetting['scope.userInfo']) {
          wx.getUserInfo({
            success: data => {
              this.setData({
                hasUserInfo: true,
                userInfo: data.userInfo
              })
            }
          })
        } else {
          this.setData({
            hasUserInfo: false
          })
        }
      }
    })
  },

  onGetUserInfo(e) {
    const userInfo = e.detail.userInfo
    if (userInfo) {
      // 1. 小程序通过wx.login()获取code
      wx.login({
        success: function(login_res) {
          //获取用户信息
          wx.getUserInfo({
            success: function(info_res) {
              // 2. 小程序通过wx.request()发送code到开发者服务器
              wx.request({
                url: 'http://localhost:8080/wx/login',
                method: 'POST',
                header: {
                 'content-type': 'application/json'
                },
                data: {
                  code: login_res.code, //临时登录凭证
                  rawData: info_res.rawData, //用户非敏感信息
                  signature: info_res.signature, //签名
                  encrypteData: info_res.encryptedData, //用户敏感信息
                  iv: info_res.iv //解密算法的向量
                },
                success: function(res) {
                  if (res.data.status == 200) {
                    // 7.小程序存储skey（自定义登录状态）到本地
                    wx.setStorageSync('userInfo', userInfo);
                    wx.setStorageSync('skey', res.data.data);
                  } else{
                    console.log('服务器异常');
                  }
                },
                fail: function(error) {
                  //调用服务端登录接口失败
                  console.log(error);
                }
              })
            }
          })
        }
      })
      this.setData({
        hasUserInfo: true,
        userInfo: userInfo
      })
    }
  }

})
```



# Swagger2 后台接口文档生成
## swagger通过注解表明该接口会生成文档，包括接口名、请求方法、参数、返回信息的等等。

* @Api：修饰整个类，描述Controller的作用
* @ApiOperation：描述一个类的一个方法，或者说一个接口
* @ApiParam：单个参数描述
* @ApiModel：用对象来接收参数（Javabean 类上的注解）
* @ApiProperty：用对象接收参数时，描述对象的一个字段
* @ApiResponse：HTTP响应其中1个描述
* @ApiResponses：HTTP响应整体描述
* @ApiIgnore：使用该注解忽略这个API
* @ApiError ：发生错误返回的信息
* @ApiImplicitParam：一个请求参数
* @ApiImplicitParams：多个请求参数




