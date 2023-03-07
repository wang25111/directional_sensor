import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cj
 * @create 2022-10-17 10:37
 */
public class Charger extends Entity implements Util{
    //最大充电距离
    public static double D = 16;
    //充电参数
    public static double alpha = 80;
    public static double beta = 8;
    //时间片大小
    public static double tao = 5;

    //使用的时间片数量
    public int h = 0;

    public Charger(double x, double y){
        this.x = x;
        this.y = y;
    }

    //获取充电范围内的传感器集合
    Set<Sensor> sensorSet = new HashSet<>();
    public void getSensors(List<Sensor> sensors){
        for(Sensor s : sensors){
            if(Util.distance(s, this) <= D){
                sensorSet.add(s);
            }
        }
    }


}
