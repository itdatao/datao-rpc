package org.club.qy.spring;


import lombok.extern.slf4j.Slf4j;
import org.club.qy.annotation.RpcScan;
import org.club.qy.annotation.RpcService;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @Author hht
 * @Date 2021/5/19 18:13
 */
@Slf4j
public class CustomScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final String SPRING_BEAN_BASE_PACKAGE = "org.club.qy.spring";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcBasePackages = new String[0];
        if (annotationAttributes!=null){
           rpcBasePackages = annotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (rpcBasePackages.length==0){
            rpcBasePackages = new String[]{((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }
        //自定义包扫描 rpcService
        CustomPackageScanner rpcServiceScanner = new CustomPackageScanner(registry, RpcService.class);
        //自定义包扫描 component
        CustomPackageScanner componentScanner = new CustomPackageScanner(registry, Component.class);
        if (resourceLoader!=null){
            rpcServiceScanner.setResourceLoader(resourceLoader);
            componentScanner.setResourceLoader(resourceLoader);
        }
        int springScannerCount = componentScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("componentScanner 扫描实例的数量：{}",springScannerCount);
        int rpcServiceScannerCount = rpcServiceScanner.scan(rpcBasePackages);
        log.info("rpcServiceScanner 扫描的实例数量：{}",rpcServiceScannerCount);

    }

}
