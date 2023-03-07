import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author cj
 * @create 2022-11-04 19:35
 * 产生对应的实体，传入的参数代表实体的数量
 */
public class EntityFactory {
    //网络的大小（影响坐标）
    static int size = 250;
    static Random rand = new Random();

    //随机生产n个充电器
    static List<Charger> generateCharger(int n){
        List<Charger> list = new ArrayList<>();
        for(int i = 0; i < n; i++){
            double x = rand.nextDouble() * (size - 2 * Charger.D) + Charger.D;
            double y = rand.nextDouble() * (size - 2 * Charger.D) + Charger.D;
            Charger c = new Charger(x, y);
            list.add(c);
        }
        return list;
    }
    //获取传感器器列表
    static List<Sensor> generateSensor(int m){
        List<Sensor> list = new ArrayList<>();
        for(int i = 0; i < m; i++){
            double x = rand.nextDouble() * (size - Charger.D) + Charger.D / 2;
            double y = rand.nextDouble() * (size - Charger.D) + Charger.D / 2;
            Sensor s = new Sensor(x, y);
            list.add(s);
        }
        return list;
    }

    static List<Sensor> generateSensor(int m, List<Charger> chargerList){
        List<Sensor> list = new ArrayList<>();
        for(int i = 0; i < m; i++){
            Sensor s = null;
            boolean flag = false;

            while(!flag){
                double x = rand.nextDouble() * (size - Charger.D) + Charger.D / 2;
                double y = rand.nextDouble() * (size - Charger.D) + Charger.D / 2;
                s = new Sensor(x, y);
                for(Charger c : chargerList){
                    if(Util.distance(c, s) <= Algorithm.B){
                        flag = true;
                        break;
                    }
                }
            }

            list.add(s);
        }
        return list;
    }

    //获取兴趣点列表
    static List<PoI> generatePoI(int l){
        List<PoI> list = new ArrayList<>();
        for(int i = 0; i < l; i++){
            PoI s = new PoI(rand.nextDouble() * size, rand.nextDouble() * size);
            list.add(s);
        }
        return list;
    }
}
