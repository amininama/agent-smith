package sha.mpoos.agentsmith.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sha.mpoos.agentsmith.entity.AgentSession;

/**
 * Created by amin on 7/17/17.
 */
@Repository
public interface AgentSessionDao extends CrudRepository<AgentSession, Long> {
}
