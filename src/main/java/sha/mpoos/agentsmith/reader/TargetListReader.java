package sha.mpoos.agentsmith.reader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Configuration
public class TargetListReader {
    private static final Logger log = Logger.getLogger("TargetListReader");
    private List<URI> targetsList;
    private @Value("${targets.source}") String address;

    public TargetListReader() {
        targetsList = new ArrayList<>();
    }

    @PostConstruct
    public void init() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(address)));
        String[] targets = content.trim().split("\\n");
        for (String target : targets) {
            try {
                URI t = new URI(target.trim());
                targetsList.add(t);
            } catch (URISyntaxException e) {
                log.warning("Invalid URL: \'" + target + "\'");
            }
        }
        log.info("Loaded " + targetsList.size() + " targets");
    }

    public List<URI> getTargets() {
        return targetsList;
    }

    public void setTargets(List<URI> targets) {
        this.targetsList = targets;
    }

    public List<URI> shuffleList() {
        Collections.shuffle(this.targetsList);
        return this.targetsList;
    }
}
