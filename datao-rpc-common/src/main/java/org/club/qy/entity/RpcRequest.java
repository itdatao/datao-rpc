package org.club.qy.entity;

import lombok.*;

/**
 * @Author hht
 * @Date 2021/5/18 17:03
 */
@Data
@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {
    //请求id
    private String requestId;
    //请求接口名称
    private String serviceName;
    //请求方法名称
    private String methodName;
    //请求方法参数
    private Object[] parameters;
    //方法参数类型
    private Class<?>[] parameterTypes;
    //接口实现的版本
    private String version;
    //接口所在的组 当一个接口有多个实现时，用group属性来判断调用哪个实现
    private String group;

    public String getRpcServiceName() {
        return this.getServiceName()+this.getGroup()+this.getVersion();
    }
}
