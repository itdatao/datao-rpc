package org.club.qy.register.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.club.qy.register.ServiceRegister;
import org.club.qy.utils.CuratorUtils;
import java.net.InetSocketAddress;

/**
 * @Author hht
 * @Date 2021/5/19 9:58
 */
@Slf4j
public class ZkServiceRegister implements ServiceRegister {

    @Override
    public void registerServices(String serviceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_BASE_ROOT_PATH + "/" + serviceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
