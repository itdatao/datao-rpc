package org.club.qy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.club.qy.enums.RpcConfig;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author hht
 * @Date 2021/5/19 11:21
 */
@Slf4j
public class CuratorUtils {
    //zookeeper的根路径
    public static final String ZK_BASE_ROOT_PATH = "/my-rpc";
    //重试之间等待的初始时间
    private static final int RETRIES_WAIT_TIME = 1000;
    //最大重试次数
    private static final int MAX_RETRIES = 3;
    //连接的服务器列表
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    //已经注册过的path
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    //默认zookeeper连接地址是本机2181
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    //zkClient
    public static CuratorFramework zkClient;


    private CuratorUtils() {
    }


    //创建一个永久性节点
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("this persistentNode already exists {}", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                REGISTERED_PATH_SET.add(path);
                log.info(path);
            }
        } catch (Exception e) {
            log.error("persistent node create failure for path: [{}]", path);
        }
    }

    //获取节点的子节点
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String serviceName) {
        //先从SERVICE_ADDRESS_MAP中查一下如果存在直接返回
        if (SERVICE_ADDRESS_MAP.containsKey(serviceName)) {
            return SERVICE_ADDRESS_MAP.get(serviceName);
        }
        //否者调用zkClient获取指定节点中的子节点数据
        List<String> result = new ArrayList<>();
        String path = getPath(serviceName); // /my-rpc/127.0.0.1:2181
        try {
            result = zkClient.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(serviceName, result);
            registerWatcher(zkClient, serviceName);
        } catch (Exception e) {
            log.error("get the node children failure [{}]", path);
        }
        return result;
    }

    //删除节点下的所有数据
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress socketAddress) {
        REGISTERED_PATH_SET.stream().forEach(service -> {
            if (service.endsWith(socketAddress.toString())) {
                try {
                    zkClient.delete().forPath(service);
                } catch (Exception e) {
                    log.error("delete [{}] failure", service);
                }
            }
        });
    }

    //获取zkClient
    public static CuratorFramework getZkClient() {
        Properties rpcConfig = PropertiesUtils.getProperties(RpcConfig.RPC_CONFIG_PATH.getPropertyValue());
        String zookeeperAddress = "";
        if (rpcConfig != null && rpcConfig.getProperty(RpcConfig.ZK_ADDRESS.getPropertyValue()) != null) {
            zookeeperAddress = rpcConfig.getProperty(RpcConfig.ZK_ADDRESS.getPropertyValue());
        } else {
            zookeeperAddress = DEFAULT_ZOOKEEPER_ADDRESS;
        }
        //如果已经移动，直接返回zkClient
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        //如果还没创建需要创建并启动
        //首先定义重试策略，如重试3次，在重试期间睡眠1s
        RetryPolicy retry = new ExponentialBackoffRetry(RETRIES_WAIT_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retry)
                .build();
        zkClient.start();
        ;
        //如果超30s还没连上zookeeper，直接抛异常
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("connect zookeeper time out!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }


    //当一个指定节点更新时注册一个监听器
    private static void registerWatcher(CuratorFramework zkClient, String serviceName) throws Exception {
        String path = getPath(serviceName);
        PathChildrenCache childrenCache = new PathChildrenCache(zkClient, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(serviceName, serviceAddresses);
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        childrenCache.start();
    }

    //获取ZkClient
    private static String getPath(String serviceName) {
        return ZK_BASE_ROOT_PATH + "/" + serviceName;
    }
}
