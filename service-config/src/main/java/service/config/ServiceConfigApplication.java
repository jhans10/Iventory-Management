package service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ServiceConfigApplication  implements CommandLineRunner {


	//@Value("${access-token.private-key}")
	//private String accessTokenPrivateKeyPath;

	@Value("${access-token.private-key:default/private/key/path}")
	private String accessTokenPrivateKeyPath;


	public static void main(String[] args) {
		SpringApplication.run(ServiceConfigApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		System.out.println(accessTokenPrivateKeyPath);
	}
}
