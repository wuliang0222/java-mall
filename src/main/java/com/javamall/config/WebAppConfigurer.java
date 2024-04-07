package com.javamall.config;

import com.javamall.interceptor.SysInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 虚拟路径映射
 */

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {
    private final String URL = "file:F:/300-Program/310-Code/images";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .maxAge(3600);
    }


    @Bean
    public SysInterceptor sysInterceptor() {
        return new SysInterceptor();
    }

    //放行
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] patterns = new String[]{"/adminLogin", "/product/**", "/bigType/**", "/user/wxlogin"};
        registry.addInterceptor(sysInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(patterns);
    }

    //资源定位
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/swiper/**").addResourceLocations(URL + "/swiperImgs/");
        registry.addResourceHandler("/image/bigType/**").addResourceLocations(URL + "/bigTypeImgs/");
        registry.addResourceHandler("/image/product/**").addResourceLocations(URL + "/productImgs/");
        registry.addResourceHandler("/image/productSwiperImgs/**").addResourceLocations(URL + "/productSwiperImgs/");
        registry.addResourceHandler("/image/productIntroImgs/**").addResourceLocations(URL + "/productIntroImgs/");
        registry.addResourceHandler("/image/productParaImgs/**").addResourceLocations(URL + "/productParaImgs/");
        registry.addResourceHandler("/image/ootdImgs/**").addResourceLocations(URL + "/ootdImgs/");
    }


}
