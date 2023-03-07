import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 产生对应的实体，传入的参数代表实体的数量
 */
public class EntityFactory {
    //网络的大小（影响坐标）
    static int size = 300;
    static Random rand = new Random();

    //随机生成m个传感器
    static List<Sensor> generateSensor(int m){
        List<Sensor> list = new ArrayList<>();
        for(int i = 0; i < m; i++){
            double x = rand.nextDouble() * size;
            double y = rand.nextDouble() * size;
            Sensor s = new Sensor(x, y);
            list.add(s);
        }
        return list;
    }

    //随机生成l个兴趣点
    static List<PoI> generatePoI(int l){
        List<PoI> list = new ArrayList<>();
        for(int i = 0; i < l; i++){
            PoI s = new PoI(rand.nextDouble() * size, rand.nextDouble() * size);
            list.add(s);
        }
        return list;
    }

    //随机生成RouteNumber条充电路径，属于车库g
    static List<Route> generateRoute(int RouteNumber, Garage g){
        List<Route> routes = new ArrayList<>();
        for(int i = 0; i < RouteNumber; i++){
            routes.add(new Route(g));
        }
        return routes;
    }

    //随机生成
    static List<Garage> generateGarage(int garageNumber, int chargerNumber, int routeNumber){
        List<Garage> garageList = new ArrayList<>();
        for(int i = 0; i < garageNumber; i++){
            double x = rand.nextDouble() * size * 0.8 + size / 10.0;
            double y = rand.nextDouble() * size * 0.8 + size / 10.0;
            int cn = rand.nextInt(chargerNumber) + 1;
            Garage g = new Garage(x, y, rand.nextInt(routeNumber - cn) + cn, cn);
            garageList.add(g);
        }
        return garageList;
    }
}
