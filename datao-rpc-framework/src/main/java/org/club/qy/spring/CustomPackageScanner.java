package org.club.qy.spring;

import org.club.qy.extension.SPI;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @Author hht
 * @Date 2021/5/19 18:13
 */
public class CustomPackageScanner extends ClassPathBeanDefinitionScanner {

    public CustomPackageScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annotation) {
        super(registry);

        addIncludeFilter(new AnnotationTypeFilter(annotation));
    }


    @Override
    public int scan(String... basePackage) {
        return super.scan(basePackage);
    }


}
