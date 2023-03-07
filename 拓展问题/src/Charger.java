import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
    mobile charger
 */
public class Charger extends Entity implements Util{
    //最大充电距离
    public static double D = 15;
    //充电参数
    public static double alpha = 80;
    public static double beta = 8;
    //移动单位距离的能耗
    public static double eta = 1;
    //充电器的电池容量
    public static double B = 3000;
    //开启功率
    public static double P = 10;
}
