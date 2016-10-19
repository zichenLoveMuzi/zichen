package cloud.eureka.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.boot.SpringApplication;

/**
* @brief 服务注册中心
* @details 
* @see 
* @author chunzhao.li
* @version v1.0.0.0
* @date 2016年10月12日下午2:21:37
*/
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {
	public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
