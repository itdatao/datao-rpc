# datao-rpc
基于Netty实现的简单RPC

## 实现SPI机制
## 负载均衡
- 实现一致性Hash算法
- 随机算法
- 轮询（待做）
- 加权（待做）
## 消息序列化
- Kryo序列化
- Hessian序列化
- protostuff序列化
- 客户端可以自定义选择消息序列化算法
## 解决粘包半包问题
- 定长消息 FixedLengthFrameDecoder
- 分隔符
  - LineBasedFrameDecoder 基于换行符作为分隔符
  - DelimiterBasedFramedDecoder 自定义分隔符   

## zookeeper作为注册中心
- 提供服务发现
- 服务监控
## 消息压缩
- Gzip
- zip
## 心跳检测
- Netty IdleStateHandler  30m内没收到客户端发送的心跳数据包就关闭连接

## 使用方式
**服务提供端（Server端）**
service的实现
```java
@Slf4j
@RpcService(group = "group1",version = "version1")
public class HelloServiceImpl implements HelloService {
    static {
        log.info("HelloServiceImpl 被创建....");
    }

    @Override
    public String sayHello(String message) {

        System.out.println("这是客户端接口传来的参数："+message);
        return message+"123";
    }
}

```


启动类
```java
//扫描的包
@RpcScan(basePackage = {"org.club.qy"})
public class ServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ServerMain.class);
        RpcServer rpcServer = (RpcServer) applicationContext.getBean("rpcServer");
        //创建被调用的接口
        HelloService helloService = new HelloServiceImpl();
        //注册服务并启动
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test2").version("version2").service(helloService).build();
        rpcServer.registerService(rpcServiceConfig);
        rpcServer.start();
    }
}
```
**服务调用方（Client端）**
Controller层
```java
@Component
public class HelloController {
    @RpcReference(version = "version1",group = "group1")
    private HelloService helloService;

    public void test(){
        String s = helloService.sayHello("我是客户端，阿巴阿巴阿巴");
        System.out.println("我是客户端，阿巴阿巴阿巴123".equals(s));  //true
    }
}
```
启动类
```java
@RpcScan(basePackage = {"org.club.qy"})
public class ClientMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
    
        helloController.test();
    }
}
```

Reference：
- [guide-rpc](https://github.com/Snailclimb/guide-rpc-framework)
- 《极客时间-RPC实战与核心原理》
- [Rpc笔记](https://www.yuque.com/huhuitao-sssvf/gg0865/ueermu)
