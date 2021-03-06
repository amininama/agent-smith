package sha.mpoos.agentsmith;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class ApplicationStarter {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationStarter.class, args);
	}
}
