package sha.mpoos.agentsmith.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sha.mpoos.agentsmith.proxy.manager.ProxyManager;

/**
 * Created by amin on 7/17/17.
 */
@RestController
public class ProxyController {
    @Autowired
    private ProxyManager proxyManager;

    @RequestMapping("/proxy/refresh")
    public ResponseEntity<Boolean> refreshProxyList() {
        this.proxyManager.refreshProxyList();
        return ResponseEntity.ok(true);
    }
}
