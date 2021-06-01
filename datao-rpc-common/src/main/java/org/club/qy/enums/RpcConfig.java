package org.club.qy.enums;

/**
 * @Author hht
 * @Date 2021/5/18 9:08
 */
public enum RpcConfig {
    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zk.address");
    private final String propertyValue;

    RpcConfig(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}
