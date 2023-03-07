import java.util.*;

import org.junit.Test;

public class Mytest {

    @Test
    public void test() {
        Garage g = new Garage(0, 0, 5, 12);
        Garage g1 = new Garage(50, 50, 5, 12);
        Sensor s = new Sensor(0, 5);
        System.out.println(s.Er);
        s.calEnergy(g1.RouteList);
        s.calEnergy(g.RouteList);
        System.out.println(s.Er);
    }

    @Test
    public void test1() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(list.size());
            System.out.println(list.get(index));
            list.remove(list.get(index));
        }
    }

    @Test
    public void test2() {

        double res = 0;
        double res1 = 0;
        double res2 = 0;
        for (int i = 0; i < 100; i++) {
            Algorithm.init(300, 800, 6);
            res += Algorithm.FOPA_IAA();
            res1 += Algorithm.FOPA_CMP();
            res2 += Algorithm.FOPA_IGSA();

        }


        System.out.println(res / 100);
        System.out.println(res1 / 100);
        System.out.println(res2 / 100);
    }

    @Test
    public void test3() {
        Charger.alpha = 13;
        Charger.beta = 3;

        double v = Charger.alpha / Math.pow(6 + Charger.beta, 2);
        System.out.println(v);
    }

    @Test
    public void test4() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            double v = 0 + Math.pow(-1, random.nextInt(2)) * (10 + random.nextInt(Route.Range - 10));
            System.out.println(v);

        }
    }

    @Test
    public void test5() {
        int[] array = {1,
                12,
                2,
                13,
                14,
                15,
                16,
                3,
                5,
                18,
                19,
                20,
                6,
                7,
                21,
                8,
                4,
                17,
                9,
                10,
                11,
                22,
                23,
                24,
                25,
                26,
                27,
                28,
                29,
                30,
                31,
                32,33,
                34,
                60,
                61,
                62,
                63,
                64,
                36,
                37,
                38,
                39,
                40,
                41,
                42,
                43,
                44,45,
                46,
                47,
                49,
                50,
                59,
                48,
                51,
                52,
                47,
                53,
                54,
                55,
                56,
                57,
                58};

        int[] count = new int[64];
        for(int e : array){
            count[e - 1]++;
        }

        for(int i = 0; i < 64; i++){
            if(count[i] > 1){
                System.out.println(i);
            }
        }
    }
}
