package sha.mpoos.agentsmith.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by amin on 8/14/17.
 */

@Configuration
public class ClientConfig {
    private @Value("${thread.concurrent}") int maxTotalPoolConnections;

    public int getMaxTotalPoolConnections() {
        return maxTotalPoolConnections;
    }

    public void setMaxTotalPoolConnections(int maxTotalPoolConnections) {
        this.maxTotalPoolConnections = maxTotalPoolConnections;
    }
}
