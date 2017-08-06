package sha.mpoos.agentsmith.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sha.mpoos.agentsmith.entity.Proxy;

import java.util.List;

/**
 * Created by amin on 6/30/17.
 */

@Repository
public interface ProxyDao extends CrudRepository<Proxy, Long> {
    Proxy findByHost(String hostString);
    @Query("SELECT p FROM Proxy p ORDER BY p.lastFetchDate , p.lastSuccessfulUse DESC, p.successCount DESC, p.failedCount")
    List<Proxy> findBest(Pageable page);
}
