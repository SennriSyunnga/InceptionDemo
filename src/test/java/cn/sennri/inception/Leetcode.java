package cn.sennri.inception;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Leetcode {

    public static void main(String[] args) throws InterruptedException {
        int a = 2;
        int b = a >> 1;
        int c = 1;
//        List<AtomicBoolean> list = new CopyOnWriteArrayList<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadPoolExecutor.DiscardPolicy());
//        long test = 1L;
//        for (int i = 0; i < 5; i++) {
//            executor.execute(() -> {
//                AtomicBoolean bool = new AtomicBoolean(false);
//                        list.add(bool);
//                    }
//            );
//        }
//        for (int i = 0; i < 5; i++) {
//            int index = i;
//            CompletableFuture.runAsync(() -> {
//                try {
//                    TimeUnit.SECONDS.sleep(10);
//                    System.out.println(list.size());
//                    list.get(index).set(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                System.out.println(index +"ready");
//            }, executor);
//        }
//        boolean allClear = false;
//        while (!allClear){
//            allClear = true;
//            for (AtomicBoolean bool: list){
//                if (!bool.get()){
//                    allClear = false;
//                    break;
//                }
//            }
//            if (!allClear){
//                TimeUnit.MILLISECONDS.sleep(500);
//            }
//        }
//        System.out.println("all clear");
//        return;
        Map<Integer, Integer> map = new ConcurrentHashMap<>();
        map.put(1,1);
        map.put(2,2);
        map.put(3,3);
        for (Map.Entry<Integer,Integer> e:map.entrySet()){
            map.remove(1);
        }
        return;
    }


}
