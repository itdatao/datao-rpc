package org.club.qy.loadbalance;


import org.club.qy.entity.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author hht
 * @Date 2021/5/18 10:20
 */

/**
 * 基于一致性hash的负载均衡
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected String select(List<String> serviceAddress, RpcRequest rpcRequest) {
        int identityHashCode = System.identityHashCode(serviceAddress);
        // build rpc service name by rpcRequest
        String rpcServiceName = rpcRequest.getServiceName();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);
        // check for updates
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceName, new ConsistentHashSelector(serviceAddress, 160, identityHashCode));
            selector = selectors.get(rpcServiceName);
        }
        //方法名+参数
        return selector.selector(rpcServiceName + Arrays.stream(rpcRequest.getParameters()));
    }


    private static final class ConsistentHashSelector {
        //存储Hash值与节点映射关系的TreeMap
        private final TreeMap<Long, String> virtualInvokers;
        //用来识别Invoker列表是否发生变更的Hash码
        private final int identityHashCode;

        /**
         * 以replicaNumber取默认值160为例，
         * 假设当前遍历到的Invoker地址为127.0.0.1:20880，
         * 它会依次获得“127.0.0.1:208800”、“127.0.0.1:208801”、……、“127.0.0.1:2088040”的md5摘要，
         * 在每次获得摘要之后，还会对该摘要进行四次数位级别的散列。大致可以猜到其目的应该是为了加强散列效果。
         * @param invokers 服务列表
         * @param replicaNumber  节点数目
         * @param identityHashCode hash码
         */
        public ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            for (String invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker + i);
                    for (int j = 0; j < 4; j++) {
                        long m = hash(digest, j);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        /**
         * 返回大于请求Hash的第一个服务节点
         * @param serviceName
         * @return
         */
        public String selector(String serviceName) {
            byte[] bytes = md5(serviceName);
            long hashCode = hash(bytes, 0);

            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            //如果没有大于该请求的hash节点，就使用环中第一个节点
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        /**
         * int&0xFF目的是保持二进制补码的一致性
         * @param digest
         * @param number
         * @return
         */
        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        //使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
        private static int getHash(String str) {
            final int p = 16777619;
            int hash = (int) 2166136261L;
            for (int i = 0; i < str.length(); i++)
                hash = (hash ^ str.charAt(i)) * p;
            hash += hash << 13;
            hash ^= hash >> 7;
            hash += hash << 3;
            hash ^= hash >> 17;
            hash += hash << 5;

            // 如果算出来的值为负数则取其绝对值
            if (hash < 0)
                hash = Math.abs(hash);
            return hash;
        }
    }
}
