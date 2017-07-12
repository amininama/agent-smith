package sha.mpoos.agentsmith.reader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Configuration
public class AgentReader {
    private static final Logger log = Logger.getLogger("AgentReader");
    private List<String> agents;
    private @Value("${agents.source}") String address;

    public AgentReader() {
        agents = new ArrayList<>();
    }

    @PostConstruct
    public void init() throws IOException{
        String content = new String(Files.readAllBytes(Paths.get(address)));
        String[] items = content.trim().split("\\n");
        Collections.addAll(agents, items);
        log.info("Loaded " + agents.size() + " agents.");
    }

    public List<String> getAgents() {
        return agents;
    }

    public void setAgents(List<String> items) {
        this.agents = items;
    }

    public String randomAgent(){
        Random random = new Random();
        return agents.get(random.nextInt(agents.size()));
    }
}
