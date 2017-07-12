package sha.mpoos.agentsmith.random;

import com.google.common.net.InetAddresses;

import java.util.Random;

public class Randomizer {
    public static String randomIP(){
        Random random = new Random();
        return InetAddresses.fromInteger(random.nextInt()).getHostAddress();
    }
}
