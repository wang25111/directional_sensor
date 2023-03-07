import java.util.*;


public class Sensor extends Entity implements Util {
    //传感器的容量
    public static double Ecap = 50;
    //感知参数
    public static double a = 16;
    public static double b = 8;
    //感知距离
    public static double R = 5;
    //感知角大小
    public static double A = Math.PI / 2;
    //工作单位时长的能耗
    public static double e = 1;

    //传感器的剩余能量，取值范围（0, Ecap]
    public double Er = (new Random()).nextInt((int) Ecap) * 0.2 + 1;
    //感知范围内的兴趣点
    Set<PoI> poISet = new HashSet<>();

    //感知方案: T[0] 角度，T[1] 时长
    public List<double[]> T = new ArrayList<>();

    public Sensor(double x, double y, double Er) {
        this.x = x;
        this.y = y;
        this.Er = Er;
    }

    public Sensor(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //获取感知范围内的兴趣点集合
    public void getPoIs(List<PoI> PoIs) {
        for (PoI p : PoIs) {
            if (Util.distance(p, this) <= R && p.Ur > 0) {
                poISet.add(p);
            }
        }
    }

    //计算充电功率
    public double power(Charger c) {
        double dis = Util.distance(c, this);
        if (dis > Charger.D) {
            return 0;
        }
        return Charger.alpha / Math.pow((dis + Charger.beta), 2);
    }

    //计算充电后的能量
    public double calEnergy(List<Charger> chargerList) {
        for (Charger c : chargerList) {
            Er += power(c) * Charger.tao * c.h;
            if (Er >= Ecap) {
                Er = Ecap;
                break;
            }
        }
        return Er;
    }

    //最佳感知角、覆盖情况
    static class Node {
        double r;
        Set<PoI> set = new HashSet<>();
        //角度区间
        double[] interval = new double[2];

        @Override
        public String toString() {
            return "传感器集合：" + set.toString()
                    + ";角度区间：[" + interval[0] + "," + interval[1] + ");最佳角：" + r;
        }
    }

    public List<Node> nodeList = new ArrayList<>();



}
