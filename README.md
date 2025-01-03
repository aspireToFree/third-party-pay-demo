# third-party-pay-demo

### 项目介绍
- "third-party-pay-demo" 是一个Java后端接入第三方支付的演示项目，集成了微信支付、支付宝支付以及云闪付支付等功能；
- 支持微信官方渠道、支付宝官方渠道以及扫码富间联通道（支持多家第三方支付公司）；
- 可路由选择通道，业务流程使用策略模式，便于后续维护与功能扩展；
- 提供业务流程实现、状态处理逻辑以及异常处理代码，为开发者提供参考。

### 使用说明
#### 1. IDE安装lombok插件
- **IntelliJ IDEA**：内置Lombok插件，无需额外安装
- **Eclipse**：需手动安装Lombok插件，具体安装步骤可参考网络教程


#### 2. 配置支付参数

参数在```src/main/resources/application.yml```文件中

- **微信参数**
  - 位于```wechat```节点下
  - 需开发者在微信官方平台申请获取


- **支付宝参数**
  - 在```alipay```节点下
  - 当前配置为支付宝沙箱环境参数，仅供测试使用
  - 付款时需安装支付宝沙箱APP


- **扫码富参数**
  - 在```smf```节点下
  - 当前配置为扫码富测试环境的MOCK参数，仅供测试使用

#### 3. 运行测试

测试代码位于```src/test/java/com/kz/tppd/test/PayTest.java```

该测试类有详细的注解说明，只需按需选择执行即可。

### 参数获取

- **微信OpenID获取**
  - [微信公众号获取OpenID](https://blog.csdn.net/qq_31993357/article/details/144829561)
  - [微信小程序获取OpenID](https://blog.csdn.net/qq_31993357/article/details/144830257)


- **支付宝user_id或open_id获取**
  - [支付宝生活号获取用户信息](https://opendocs.alipay.com/mini/02xtl8)
  - [支付宝小程序获取用户信息](https://opendocs.alipay.com/mini/05dxgc?pathHash=1a3ecb13)


### 直联对接

- **微信直联**
  - [微信公众号支付](https://blog.csdn.net/qq_31993357/article/details/144830049)
  - [微信小程序支付](https://blog.csdn.net/qq_31993357/article/details/144830372)
  
- **支付宝直联**
  - [支付宝生活号支付](https://blog.csdn.net/qq_31993357/article/details/144831615)
  - [支付宝小程序支付](https://blog.csdn.net/qq_31993357/article/details/144832229)

### 扫码富平台介绍

扫码富是笔者所在公司倾力打造的聚合支付系统，已获聚合支付备案，支持 **微信支付、支付宝支付、云闪付支付**；并与多家知名第三方支付公司建立了深度合作，
如：拉卡拉、随行付、汇付天下、易宝支付、易生支付、乐刷等。

相较于直联模式，扫码富的间联模式具备以下优势：
- 更低商户费率：为您节省成本，提升利润空间；
- 对私结算支持：满足您多样化的结算需求，灵活便捷；
- 强大分账功能：助力您高效管理资金流，优化财务运作；
- 专业对接团队：提供专属对接群，确保问题响应迅速，沟通顺畅无阻。

**若您有意接入扫码富的间联服务，请添加我的微信进行深入交流。添加时，请备注“接入扫码富”，以便我们更快为您提供服务。**

| 微信二维码                                                                              |  
|:--:|  
|<img src="https://gitee.com/to_free/bucket/raw/master/img/20241205/wechat_friend_code.png" width=180/> |


### 直联与间联说明

#### 1、直联模式
直联模式是指商户直接对接微信与支付宝的官方平台与接口，实现支付功能的接入。
这种模式下，商户与支付平台之间建立直接的支付通道，无需通过其他第三方机构进行中转。
直联模式的优势在于其支付流程简洁明了，但通常商户需要承担较高的接入成本和技术维护要求，且商户费率可能相对较高。

#### 2、间联模式
间联模式则是通过其他具备人民银行颁发的第三方支付公司牌照的支付机构（如财付通、支付宝的间接合作方，
以及拉卡拉、随行付、汇付天下等独立的第三方支付公司）间接接入微信与支付宝的接口。
在这种模式下，商户与微信、支付宝平台之间不直接接口对接，而是通过第三方支付公司进行中转。
间联模式的优势在于其商户费率通常更为优惠，且支持对私结算和分账功能，同时，由于第三方支付公司具备专业的技术团队和丰富的支付经验，
能够为商户提供更加便捷、高效的支付接入和后续服务。此外，间联模式还提供了专业的对接团队和沟通渠道，
确保商户在使用过程中遇到的问题能够得到及时响应和解决。

<span style="color:red">无论是直联模式还是间联模式，资金都受到人民银行的严格监管，确保支付过程的安全性和合规性。</span>

### 捐赠 

如果你正在使用这个项目或者喜欢这个项目的，可以通过以下方式支持我：

- Star、Fork、Watch 一键三连 🚀
- 通过微信、支付宝一次性捐款 ❤


| 微信   | 支付宝 |  
| :--: | :--: |  
| <img src="https://gitee.com/to_free/bucket/raw/master/img/20241205/wechat_reward_code.png" width=310/> | <img src="https://gitee.com/to_free/bucket/raw/master/img/20241205/alipay_reward_code.png" width=310/> |
