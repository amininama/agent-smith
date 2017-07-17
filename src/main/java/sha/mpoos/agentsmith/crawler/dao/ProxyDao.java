package sha.mpoos.agentsmith.crawler.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sha.mpoos.agentsmith.entity.Proxy;

/**
 * Created by amin on 6/30/17.
 */

@Repository
public interface ProxyDao extends CrudRepository<Proxy, Long> {
    Proxy findByHost(String hostString);
}
