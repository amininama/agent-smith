package sha.mpoos.agentsmith.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by amin on 8/14/17.
 */

@Configuration
public class ClientConfig {
    private @Value("${thread.concurrent}") int maxTotalPoolConnections;
    private @Value("${client.timeout.secs}") int validateAfterInactivity;
    private @Value("${client.timeout.secs}") int defaultSocketTimeout;

    public int getMaxTotalPoolConnections() {
        return maxTotalPoolConnections;
    }

    public void setMaxTotalPoolConnections(int maxTotalPoolConnections) {
        this.maxTotalPoolConnections = maxTotalPoolConnections;
    }

    public int getValidateAfterInactivity() {
        return validateAfterInactivity;
    }

    public void setValidateAfterInactivity(int validateAfterInactivity) {
        this.validateAfterInactivity = validateAfterInactivity;
    }

    public int getDefaultSocketTimeout() {
        return defaultSocketTimeout;
    }

    public void setDefaultSocketTimeout(int defaultSocketTimeout) {
        this.defaultSocketTimeout = defaultSocketTimeout;
    }
}
