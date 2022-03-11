package cn.sennri.inception.util;

import java.util.concurrent.ThreadLocalRandom;

public class RollUtils {
    final static ThreadLocalRandom random = ThreadLocalRandom.current();
    public static int roll(){
        return random.nextInt(6) + 1;
    }


}
