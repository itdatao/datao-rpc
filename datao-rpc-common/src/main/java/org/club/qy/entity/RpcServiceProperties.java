package org.club.qy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author hht
 * @Date 2021/5/18 9:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class RpcServiceProperties {
    //PpcServer的属性

    //接口的版本类型
    private String version;
    //当一个接口有多个实现时，用group做区分
    private String group;
    //接口名称
    private String serviceName;

    public String toServiceProperties(){
        return this.getServiceName()+this.getGroup()+this.getVersion();
    }

}
