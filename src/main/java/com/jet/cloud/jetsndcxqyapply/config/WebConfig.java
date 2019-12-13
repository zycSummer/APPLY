package com.jet.cloud.jetsndcxqyapply.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/admin/main").setViewName("admin/main");
        registry.addViewController("/admin").setViewName("admin/index");
        registry.addViewController("/admin/").setViewName("admin/index");
        registry.addViewController("/admin/index").setViewName("admin/index");
        registry.addViewController("/admin/login").setViewName("admin/login");
        registry.addViewController("/admin/business").setViewName("admin/business");
        registry.addViewController("/admin/townStreet").setViewName("admin/townStreet");
        registry.addViewController("/admin/committee").setViewName("admin/committee");
        registry.addViewController("/admin/officeArea").setViewName("admin/officeArea");
        registry.addViewController("/admin/review").setViewName("admin/review");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/public/**").addResourceLocations("file:./static/", "classpath:/static/");
    }

    @Bean
    public HttpPutFormContentFilter httpPutFormContentFilter(){
        return new HttpPutFormContentFilter();
    }

}
