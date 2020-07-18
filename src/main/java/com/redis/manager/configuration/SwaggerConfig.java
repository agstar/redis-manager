/*
package com.redis.manager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

*/
/**
 * Swagger  Configuration
 *
 * @author agstar
 * @date 2020/7/11 14:44
 *//*

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage("ccom.redis.manager.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    */
/**
     * 构建 api文档的详细信息函数,注意这里的注解引用的是哪个
     *//*

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("RedisManager Api")
                //创建人
                .contact(new Contact("agstar", "http://blog.bianxh.top/", ""))

                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                //版本号
                .version("1.0")
                //描述
                .description("API 描述")
                .build();
    }
}*/
