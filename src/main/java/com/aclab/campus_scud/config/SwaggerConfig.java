package com.aclab.campus_scud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 31618
 * @description: 配置Swagger2
 * @date 2021-05-23 13:32
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket createdApi(){
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				// 这个包要是你的启动类的所在包，这样才能检测到所有的API
				.apis(RequestHandlerSelectors.basePackage("com.aclab.campus_scud"))
				// 设置对外开放的API，这里是所有接口
				.paths(PathSelectors.any())
				.build();
	}



	private ApiInfo apiInfo(){
		return new ApiInfoBuilder()
				.title("校园飞毛腿后台API文档构建")
				.description("校园飞毛腿后台API文档构建, 控制接口, 服务接口, 测试接口")
				.termsOfServiceUrl("https://blog.csdn.net/HK_HH?spm=1001.2101.3001.5343")
				.contact("HK温柔的心")
				.version("0.1")
				.build();
	}

}
