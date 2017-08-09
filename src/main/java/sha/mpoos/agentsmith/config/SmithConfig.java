package sha.mpoos.agentsmith.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class SmithConfig {
    private @Value("${thread.count}") int threadCount;
    private @Value("${sleep.time.millis}") long sleepTimeMillis;
    private @Value("${thread.concurrent}") int concurrentUserCount;
    private @Value("${client.timeout.secs}") int clientTimeoutSecs;

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public long getSleepTimeMillis() {
        return sleepTimeMillis;
    }

    public void setSleepTimeMillis(long sleepTimeMillis) {
        this.sleepTimeMillis = sleepTimeMillis;
    }

    public int getConcurrentUserCount() {
        return concurrentUserCount;
    }

    public void setConcurrentUserCount(int concurrentUserCount) {
        this.concurrentUserCount = concurrentUserCount;
    }

    public int getClientTimeoutSecs() {
        return clientTimeoutSecs;
    }

    public void setClientTimeoutSecs(int clientTimeoutSecs) {
        this.clientTimeoutSecs = clientTimeoutSecs;
    }
}
