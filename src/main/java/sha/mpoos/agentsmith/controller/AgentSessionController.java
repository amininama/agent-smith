package sha.mpoos.agentsmith.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sha.mpoos.agentsmith.TheSmith;
import sha.mpoos.agentsmith.dao.AgentSessionDao;
import sha.mpoos.agentsmith.dao.TargetCollectionDao;
import sha.mpoos.agentsmith.entity.AgentSession;
import sha.mpoos.agentsmith.entity.TargetCollection;

/**
 * Created by amin on 7/17/17.
 */
@RestController
public class AgentSessionController {
    @Autowired
    private TargetCollectionDao targetCollectionDao;
    @Autowired
    private AgentSessionDao agentSessionDao;
    @Autowired
    private TheSmith theSmith;

    @RequestMapping("/agent/session/init")
    public ResponseEntity<AgentSession> init(
            @RequestParam(name = "target_collection_id", required = true) long targetCollectionId,
            @RequestParam(name = "concurrent_clients", required = true) int concurrentClients,
            @RequestParam(name = "total_clients", required = true) int totalClients,
            @RequestParam(name = "sleep_time", required = true) int sleepTime) {
        AgentSession agentSession = new AgentSession();
        try {
            agentSession.setConcurrentRequestCount(concurrentClients);
            agentSession.setTotalRequestCount(totalClients);
            agentSession.setSleepTimeMillis(sleepTime);
            TargetCollection targetCollection = targetCollectionDao.findOne(targetCollectionId);
            agentSession.setTargetCollection(targetCollection);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        agentSession = agentSessionDao.save(agentSession);
        return ResponseEntity.ok(agentSession);
    }

    @RequestMapping("/agent/session/launch")
    public ResponseEntity<Boolean> launch(
            @RequestParam(name = "id", required = true) long agentSessionId) throws InterruptedException {
        AgentSession agentSession = agentSessionDao.findOne(agentSessionId);
        if (agentSession == null) return ResponseEntity.notFound().build();
        theSmith.launchSession(agentSession);
        return ResponseEntity.ok(true);
    }

    @RequestMapping("/agent/session/get")
    public ResponseEntity<AgentSession> query(
            @RequestParam(name = "id", required = true) long id) {
        AgentSession agentSession = agentSessionDao.findOne(id);
        if (agentSession == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(agentSession);
    }
}
