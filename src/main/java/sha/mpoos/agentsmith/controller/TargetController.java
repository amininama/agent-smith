package sha.mpoos.agentsmith.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sha.mpoos.agentsmith.dao.TargetCollectionDao;
import sha.mpoos.agentsmith.entity.Target;
import sha.mpoos.agentsmith.entity.TargetCollection;

/**
 * Created by amin on 7/17/17.
 */
@RestController
public class TargetController {
    @Autowired
    private TargetCollectionDao targetCollectionDao;

    @RequestMapping("/collection/add")
    public ResponseEntity<TargetCollection> addCollection(
            @RequestParam(name = "name", required = true) String collectionName) {
        TargetCollection targetCollection = new TargetCollection();
        try {
            targetCollection.setName(collectionName);
        } catch (NullPointerException e) {
            //collectionName is null
            return ResponseEntity.badRequest().body(null);
        }
        try {
            targetCollection = targetCollectionDao.save(targetCollection);
            return ResponseEntity.ok(targetCollection);
        } catch (Throwable t) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @RequestMapping("/target/add")
    public ResponseEntity<TargetCollection> addTargetToCollection(
            @RequestParam(name = "collection_id", required = true) long collectionId,
            @RequestParam(name = "address", required = true) String address) {
        if (!Target.isValid(address)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        TargetCollection targetCollection = targetCollectionDao.findOne(collectionId);
        if (targetCollection == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        Target target = new Target();
        target.setAddress(address);
        targetCollection.addTarget(target);
        targetCollection = targetCollectionDao.save(targetCollection);
        return ResponseEntity.ok(targetCollection);
    }

    @RequestMapping("/collection/get")
    public ResponseEntity<TargetCollection> getById(
            @RequestParam(name = "id", required = true) long id) {
        TargetCollection collection = targetCollectionDao.findOne(id);
        if (collection == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.ok(collection);
    }

    @RequestMapping("/collection/edit")
    public ResponseEntity<TargetCollection> editTargetInCollection(
            @RequestParam(name = "collection_id", required = true) long collectionId,
            @RequestParam(name = "target_id", required = true) long targetId,
            @RequestParam(name = "address", required = true) String address){
        if(!Target.isValid(address)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        TargetCollection collection = targetCollectionDao.findOne(collectionId);
        if(collection == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        Target target = new Target();
        target.setAddress(address);
        target.setId(targetId);
        collection.addTarget(target);
        collection = targetCollectionDao.save(collection);
        return ResponseEntity.ok(collection);
    }
}
