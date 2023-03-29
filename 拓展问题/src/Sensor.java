import java.util.*;


public class Sensor extends Entity implements Util {
    //传感器的能量源容量
    public static double Ecap = 50;
    //感知参数
    public static double a = 90;
    public static double b = 8;
    //感知距离
    public static double R = 5;
    //感知角大小
    public static double A = Math.PI / 2;
    //工作单位时长的能耗
    public static double e = 1;

    //传感器的剩余能量，取值范围: (0, 20%]*Ecap
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
            if (Util.distance(p, this) <= R && p.Ur > 0.01) {
                poISet.add(p);
            }
        }
    }

    //从某条路径上获取的能量
    public double calEnergy(Route r){
        double res = 0;
        res += calEnergy(r, r.node1, r.node2);
        res += calEnergy(r, r.node2, r.node3);
        res += calEnergy(r, r.node3, r.node4);
        res += calEnergy(r, r.node4, r.node1);
        return res;
    }

    //传感器从一段直线路径上接收到的充电能量
    private double calEnergy(Route r, Entity n1, Entity n2){
        if(r.v == 0){
            return 0;
        }
        double res = 0;
        double d = Util.distance(this, n1, n2);
        double h = Util.pointToLine(this, n1, n2);
        double d1 = Math.min(Util.distance(this, n1), Charger.D);
        double d2 = Math.min(Util.distance(this, n2), Charger.D);
        //若 h == d: 传感器到充电路径的距离为h
        if(Math.abs(d - h) <= 0.0001){
            double t1 = Math.sqrt(d1 * d1 - h * h) / r.v;
            double slot1 = t1 / 30;
            double t = 0;
            while(t < t1){
                res += slot1 * Charger.alpha / Math.pow( Math.sqrt(h * h + r.v * r.v * t * t) + Charger.beta, 2);
                t += slot1;
            }
            double t2 = Math.sqrt(d2 * d2 - h * h) / r.v;
            double slot2 = t2 / 30;
            t = 0;
            while(t < t2){
                res += slot2 * Charger.alpha / Math.pow( Math.sqrt(h * h + r.v * r.v * t * t) + Charger.beta, 2);
                t += slot2;
            }
        }else{
            if(d1 > d2){
                double tmp = d1;
                d1 = d2;
                d2 = tmp;
            }

            double start = Math.sqrt(d1 * d1 - h * h);
            double end = Math.sqrt(d2 * d2 - h * h);
            double slot = (end - start) / 30;
            double t = start;
            while(t < end){
                res += Charger.alpha / Math.pow(Math.sqrt(h * h + r.v * r.v * t * t) + Charger.beta, 2);
                t += slot;
            }
        }
        return res;
    }

    //计算充电后的能量
    public double calEnergy(List<Route> routeList) {
        for(Route r : routeList){
            Er += calEnergy(r);
            if(Er >= Ecap){
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

    @Override
    public String toString() {
        return x + "," + y +"," + Er;
    }
}
