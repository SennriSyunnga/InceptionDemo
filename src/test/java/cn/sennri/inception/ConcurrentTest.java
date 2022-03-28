package cn.sennri.inception;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;

public class ConcurrentTest {
    /**
     * 支持动态去除元素
     */
    public void testMap(){
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1,1);
        map.put(2,2);
        map.put(3,3);
        try{
            for (Map.Entry<Integer,Integer> e:map.entrySet()){
                map.remove(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        map = new ConcurrentHashMap<>();
        map.put(1,1);
        map.put(2,2);
        map.put(3,3);
        for (Map.Entry<Integer,Integer> e:map.entrySet()){
            map.remove(1);
        }
    }
}
