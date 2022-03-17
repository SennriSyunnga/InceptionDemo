package cn.sennri.inception.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class GameUtils {
    final static ThreadLocalRandom random = ThreadLocalRandom.current();
    public static int roll(){
        return random.nextInt(6) + 1;
    }

    /*
     以下两种随机数生成方法参考：https://stackoverflow.com/questions/8115722/generating-unique-random-numbers-in-java
     Result "cn.sennri.inception.util.GameUtilsTest.getRandomAscendNumArray":
     845.933 ±(99.9%) 8.801 ns/op [Average]
     (min, avg, max) = (842.909, 845.933, 848.255), stdev = 2.286
     CI (99.9%): [837.132, 854.734] (assumes normal distribution)
    */

    /**
     *
     * @param range
     * @param num
     * @return
     */
    @Deprecated
    public static int[] getRandomAscendNumArray(int range, int num){
        if (num > range){
             throw new IllegalArgumentException();
        }else{
            Integer[] indices = new Integer[range];
            Arrays.setAll(indices, i -> i);
            Collections.shuffle(Arrays.asList(indices));
            int[] ans = new int[num];
            for(int i = 0;i < num;i++){
                ans[i] = indices[i];
            }
            return ans;
        }
    }


    /*
        Result "cn.sennri.inception.util.GameUtilsTest.getRandomAscendNumArray2":
        270.828 ±(99.9%) 4.429 ns/op [Average]
        (min, avg, max) = (269.475, 270.828, 271.929), stdev = 1.150
        CI (99.9%): [266.399, 275.257] (assumes normal distribution)
     */
    /**
     * 生成若干个在一定范围内无重复的数字
     * @param bound 数字范围 (excluded)
     * @param num   产生数字量
     * @return
     */
    public static int[] getRandomAscendNumArray2(int bound, int num){
        if (num > bound){
            throw new IllegalArgumentException();
        }else{
            return random.ints(0, bound).distinct().limit(num).sorted().toArray();
        }
    }


    /**
     * 可能需要的效果的指定对象
     */
    public enum TargetTypeEnum{
        /**
         * 不取对象的效果
         */
        NONE,
        /**
         * 针对玩家的效果
         */
        PLAYER,
        /**
         * 将对象解读为墓地里的卡
         */
        GRAVEYARD_CARD,
        /**
         * 针对某个玩家的手牌
         */
        HAND_CARD
    }
}
