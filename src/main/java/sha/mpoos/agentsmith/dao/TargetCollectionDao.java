package sha.mpoos.agentsmith.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sha.mpoos.agentsmith.entity.TargetCollection;

/**
 * Created by amin on 7/17/17.
 */

@Repository
public interface TargetCollectionDao extends CrudRepository<TargetCollection, Long> {
}
