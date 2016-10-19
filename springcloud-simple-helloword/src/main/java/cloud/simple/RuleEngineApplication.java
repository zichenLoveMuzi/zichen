package cloud.simple;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.async.DeferredResult;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import cloud.simple.db.MongoDB;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
* @brief 
* @details 
* @see 
* @author chunzhao.li
* @version v1.0.0.0
* @date 2016年9月28日下午1:54:46
* @note spring boot 官方暂时不支持mongo3.X，并且官方demo要求mongo要映射到javabean，这样不灵活，所以自己写了个链接，便于拿到mongodatabase,从而操作mongo数据库；
* 		 springboot默认会自动启动mongo链接，暂时不知道怎么关闭，所以现在暂时先启动两个链接吧，默认自启的那个空着，等官方支持3.X了再更新。
* 
*/
@SpringBootApplication
@EnableSwagger2
public class WebApplication {

	@Autowired
	private Environment env;
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebApplication.class, args);
    }
    
    @Bean
	public MongoDatabase dataSource() {
		String servers = env.getProperty("spring.data.mongodb.custom.service");
		String databaseName = env.getProperty("spring.data.mongodb.database");
		
		List<ServerAddress> seeds = new ArrayList<ServerAddress>();
		String[] servers1 = servers.split(",");
		for(String server : servers1){
			String[] server1 = server.split(":");
			seeds.add(new ServerAddress(server1[0],Integer.parseInt(server1[1])));
		}
		
		Builder builder = MongoClientOptions.builder();
		builder.socketKeepAlive(true);
		builder.readPreference(ReadPreference.secondaryPreferred());
		MongoClientOptions options = builder.build();
		
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(seeds,options);
		return mongoClient.getDatabase(databaseName);
	}
    
    @Bean
    public MongoDB mongoDB(){
    	return new MongoDB();
    }
    
    /**可以定义多个组*/
    @Bean
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("default")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(PathSelectors.any())//过滤的接口
                .build()
                .apiInfo(defaultApiInfo());
    }

    private ApiInfo defaultApiInfo() {
        ApiInfo apiInfo = new ApiInfo("LCM规则引擎API",//大标题
                "用于规则的定义和运转",//小标题
                "1.0",//版本
                "NO terms of service",
                "chunzhao.li",//作者
                "The Accenture License, Version 1.0",//链接显示文字
                "http://www.accenture.cn"//网站链接
        );
        return apiInfo;
    }
}
