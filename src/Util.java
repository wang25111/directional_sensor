

import java.util.*;


public interface Util {
    //计算距离
    static double distance(Entity e1, Entity e2) {
        return Math.sqrt(
                (e1.x - e2.x) * (e1.x - e2.x)
                        + (e1.y - e2.y) * (e1.y - e2.y)
        );
    }

    //计算感知质量
    static double Quality(Sensor s, PoI o, double r) {
        if (o.Ur <= 0.01) return 0;
        double dis = distance(s, o);
        if (dis > Sensor.R) {
            return 0;
        }

        //向量r
        double rx = -Math.sin(r);
        double ry = Math.cos(r);
        //向量so
        double sox = o.x - s.x;
        double soy = o.y - s.y;
        //计算r和so夹角的cos值
        double cos_r_sox;
        if (sox == 0 && soy == 0) {
            cos_r_sox = 1;
        } else {
            cos_r_sox = (rx * sox + ry * soy) / Math.sqrt(sox * sox + soy * soy);
        }

        if (cos_r_sox < Math.cos(Sensor.A / 2)) {
            return 0;
        }
        double res = o.wk * Sensor.a * cos_r_sox / Math.pow(dis + Sensor.b, 2);
        return res;
    }

    //s的总感知质量
    static double QualityAll(Sensor s, double r) {
        double res = 0;
        for (PoI o : s.poISet) {
            res += Quality(s, o, r);
        }
        return res;
    }

    //s对某些兴趣点集合的感知效用
    static double QualityCollection(Sensor s, Collection<PoI> collection, double r){
        double res = 0;
        for(PoI p : collection){
            res += Quality(s, p, r);
        }
        return res;
    }

    //s感知o获得的效用
    static double Utility(Sensor s, PoI o) {
        double res = 0;
        for (double[] t : s.T) {
            double tmp = Quality(s, o, t[0]) * t[1];
            if (tmp > o.Ur) {
                tmp = o.Ur;
            }
            res += tmp;
            o.Ur -= tmp;
        }
        return res;
    }

    //整个网络感知o获取的效用
    static double Utility(List<Sensor> sensors, PoI o) {
        double res = 0;
        for (Sensor s : sensors) {
            res += Utility(s, o);
        }
        return res;
    }

    //整个网络的效用（前提：传感器的感知方案已经确定）
    static double totalUtility(List<Sensor> sensors, List<PoI> poIS) {
        double res = 0;
        for (PoI o : poIS) {
            res += Utility(sensors, o);
        }
        return res;
    }

    //求o在s的方位角(0 ~ 2π)
    static double angle(Sensor s, PoI o) {
        double sox = o.x - s.x;
        double soy = o.y - s.y;
        double cos = soy / Math.sqrt(sox * sox + soy * soy);
        if (sox > 0) {
            return 2 * Math.PI - Math.acos(cos);
        }
        return Math.acos(cos);
    }

    static double getOptAngle(Sensor s) {
        return getOptAngle(s, 0, 2 * Math.PI);
    }

    static double getOptAngle(Sensor s, double start, double end) {
        double r = start;
        //当前方向下的感知质量
        double tmp = 0;
        //当前方向
        double cur = start;
        //每次改变的步长
        double delta = 0.12;
        while (cur < end) {
            double t = QualityAll(s, cur);
            if (t > tmp) {
                r = cur;
                tmp = t;
            }
            cur += delta;
        }
        return r;
    }

    static double getOptAngle(Sensor s, Collection<PoI> poIS, double start, double end) {
        double r = start;
        //当前方向下的感知质量
        double tmp = 0;
        //当前方向
        double cur = start;
        //每次改变的步长
        double delta = 0.02;
        while (cur < end) {
            double t = QualityCollection(s, poIS, cur);
            if (t > tmp) {
                r = cur;
                tmp = t;
            }
            cur += delta;
        }
        return r;
    }

    static void initSensor(double[] ErArray, List<Sensor> sensorList, List<PoI> poIList) {
        for (int i = 0; i < ErArray.length; i++) {
            sensorList.get(i).Er = ErArray[i];
            sensorList.get(i).T.clear();
            sensorList.get(i).getPoIs(poIList);
        }
    }

    static void initPoI(double[] UrArray, List<PoI> poIList) {
        for (int i = 0; i < UrArray.length; i++) {
            poIList.get(i).Ur = UrArray[i];
        }
    }

    //恢复能量、效用到初始状态(ErArray、UrArray)
    static void init(double[] ErArray, List<Sensor> sensorList, double[] UrArray, List<PoI> poIList) {
        initPoI(UrArray, poIList);
        initSensor(ErArray, sensorList, poIList);
    }

    //（整体）算法1：根据开启方案，确定总效用
    static double totalUtility(int[] H, List<Charger> chargerList, List<Sensor> sensorList, List<PoI> poIList) {
        //先开启充电器
        for (int i = 0; i < H.length; i++) {
            chargerList.get(i).h = H[i];
        }

        double sum = 0;
        //再计算传感器充电后的能量
        for (Sensor s : sensorList) {
            s.calEnergy(chargerList);
//            System.out.println(s.Er);
            sum += senseSchedule(s);
        }
        return sum;
    }

    //（单个充电器）确定传感器的感知方案
    static double senseSchedule(Sensor s) {
        double U = 0;
        List<double[]> res = s.T;
        while (s.Er > 0 && s.poISet.size() > 0) {
            double[] tmp = new double[2];
            double r = getOptAngle(s);
            //充电器最长工作时间
            double workTime = s.Er / s.e;
            //当前方向上的感知时间
            double senseTime = Integer.MAX_VALUE;
            for (PoI p : s.poISet) {
                if (Quality(s, p, r) > 0) {
                    double time = p.Ur / Quality(s, p, r);
                    senseTime = Math.min(senseTime, time);
                }
            }
            tmp[0] = r;
            tmp[1] = Math.min(workTime, senseTime);
            res.add(tmp);
            //更新剩余能量、效用
            s.Er -= s.e * tmp[1];
            //记录效用为0的兴趣点
            List<PoI> zero = new ArrayList<>();
            for (PoI p : s.poISet) {
                U += Quality(s, p, r) * tmp[1];
                p.Ur -= Quality(s, p, r) * tmp[1];
                if (p.Ur <= 0) zero.add(p);
            }
            for (PoI p : zero) {
                s.poISet.remove(p);
            }
        }
        return U;
    }

    //算法2：构建最优角集合
    static void partition(Sensor sensor) {
        Set<PoI> poISet = sensor.poISet;
        double delta = 0.02;
        double cur = 0;
        //之前感知范围内，兴趣点的数量
        int preCount = 0;
        for (PoI p : poISet) {
            if (Quality(sensor, p, 0) > 0) preCount++;
        }

        double angle = 0;
        while (cur < 2 * Math.PI) {
            //现在，感知范围内，兴趣点的数量
            int curCount = 0;
            for (PoI p : poISet) {
                if (Quality(sensor, p, cur) > 0) curCount++;
            }
            if (preCount != curCount) {
                Sensor.Node node = new Sensor.Node();
                //兴趣点覆盖情况
                for (PoI p : poISet) {
                    if (Quality(sensor, p, cur - delta) > 0) {
                        node.set.add(p);
                    }
                }
                //角度区间
                node.interval[0] = angle;
                node.interval[1] = cur;
                //获取角度区间上的最优感知角
                node.r = getOptAngle(sensor, angle, cur);
                sensor.nodeList.add(node);
                //更新angle、cur
                angle = cur;
                preCount = curCount;
            }
            cur += delta;
        }

        //收尾
        Sensor.Node node = new Sensor.Node();
        for (PoI p : poISet) {
            if (Quality(sensor, p, 2 * Math.PI) > 0) {
                node.set.add(p);
            }
        }
        node.interval[0] = angle;
        node.interval[1] = 2 * Math.PI;
        node.r = getOptAngle(sensor, angle, 2 * Math.PI);
        sensor.nodeList.add(node);
    }

    //算法2 V2：构建1最优角集合
    static void partitionV2(Sensor sensor){
        Set<PoI> poISet = sensor.poISet;
        List<PoI> list = new ArrayList<>(poISet);
        double[][] array = new double[poISet.size() + 1][2];
        array[array.length - 1][1] = 2 * Math.PI;
        for(int i = 0; i < poISet.size(); i++){
            array[i][0] = i;
            array[i][1] = angle(sensor, list.get(i));
        }

        Arrays.sort(array, (a, b)->(a[1] >= b[1] ? 1: -1));

        double start = 0;
        double end = 0;
        double preEnd;
        LinkedList<PoI> queue = new LinkedList<>();

        for(double[] e : array){
            preEnd = end;
            end = e[1];

            while (end - start > Sensor.A){
                Sensor.Node node = new Sensor.Node();
                node.set.addAll(new LinkedList<>(queue));
                node.r = getOptAngle(sensor, node.set, start, preEnd);
                node.interval[0] = start;
                node.interval[1] = preEnd;
                sensor.nodeList.add(node);

                if (queue.size() != 0) queue.removeFirst();
                if(queue.size() == 0){
                    start = end;
                }else{
                    start = angle(sensor, queue.getFirst());
                }
            }
            //加入最后一个教读区间
            {
                Sensor.Node node = new Sensor.Node();
                node.set.addAll(new LinkedList<>(queue));
                node.r = getOptAngle(sensor, start, preEnd);
                node.interval[0] = start;
                node.interval[1] = preEnd;
                sensor.nodeList.add(node);
            }
            if(end - start <= Sensor.A && end != 2 * Math.PI){
                queue.addLast(list.get((int)e[0]));
            }


        }



    }

    //（单个充电器）算法1 v2：确定传感器的感知方案
    static double senseScheduleV2(Sensor s) {
        //调用算法2
        partition(s);
        double U = 0;
        List<double[]> res = s.T;
        while (s.Er > 0 && s.nodeList.size() > 0) {
            double[] tmp = new double[2];
            Sensor.Node node = null;
            double quality = 0;
            double r = 0;
            for (Sensor.Node e : s.nodeList) {
                double v = QualityAll(s, e.r);
                if (v > quality) {
                    quality = v;
                    node = e;
                    r = e.r;
                }
            }
            if(node == null) return 0;

            double workTime = s.Er / s.e;
            double senseTime = Integer.MAX_VALUE;
            for (PoI p : node.set) {
                if (Quality(s, p, r) > 0) {
                    double time = p.Ur / Quality(s, p, r);
                    senseTime = Math.min(senseTime, time);
                }
            }
            tmp[0] = r;
            tmp[1] = Math.min(workTime, senseTime);
            res.add(tmp);

            s.Er -= s.e * tmp[1];
            List<PoI> zero = new ArrayList<>();
            for (PoI p : node.set) {
                U += Quality(s, p, r) * tmp[1];
                p.Ur -= Quality(s, p, r) * tmp[1];
                if (p.Ur <= 0) zero.add(p);
            }

            List<Sensor.Node> emptyNodeList = new ArrayList<>();
            for (Sensor.Node node1 : s.nodeList) {
                for (PoI p : zero) {
                    if (node1.set.contains(p)) {
                        node1.set.remove(p);
                        node1.r = getOptAngle(s, node1.interval[0], node1.interval[1]);
                        if (node1.set.isEmpty()) emptyNodeList.add(node1);
                    }
                }
            }

            for (Sensor.Node node2 : emptyNodeList) {
                s.nodeList.remove(node2);
            }
        }
        return U;
    }

    //（整体）算法1 v2：确定传感器的感知方案
    static double totalUtilityV2(int[] H, List<Charger> chargerList, List<Sensor> sensorList){
        for(int i = 0; i <H.length; i++){
            chargerList.get(i).h = H[i];
        }

        double U = 0;
        for(Sensor s : sensorList){
            s.calEnergy(chargerList);
            U += senseScheduleV2(s);
        }

        return U;
    }

    //算法1： v3确定单个传感器的感知方案
    static double senseScheduleV3(Sensor s){
        double U = 0;
        List<double[]> res = s.T;
        while(s.Er > 0 && s.poISet.size() > 0){
            partitionV2(s);
            double[] tmp = new double[2];
            Sensor.Node node = null;
            double quality = 0;
            double r = 0;
            for (Sensor.Node e : s.nodeList) {
                double v = QualityCollection(s, e.set, e.r);
                if (v > quality) {
                    quality = v;
                    node = e;
                    r = e.r;
                }
            }
            if(node == null) return 0;

            double workTime = s.Er / s.e;
            double senseTime = Integer.MAX_VALUE;
            for (PoI p : node.set) {
                if (Quality(s, p, r) > 0) {
                    double time = p.Ur / Quality(s, p, r);
                    senseTime = Math.min(senseTime, time);
                }
            }

            tmp[0] = r;
            tmp[1] = Math.min(workTime, senseTime);
            res.add(tmp);

            s.Er -= s.e * tmp[1];

            List<PoI> zero = new ArrayList<>();
            for (PoI p : s.poISet) {
                U += Quality(s, p, r) * tmp[1];
                p.Ur -= Quality(s, p, r) * tmp[1];
                if (p.Ur <= 0.01) zero.add(p);
            }
            for (PoI p : zero) {
                s.poISet.remove(p);
            }

        }
        return U;
    }

    static double totalUtilityV3(int[] H, List<Charger> chargerList, List<Sensor> sensorList){
        for(int i = 0; i < H.length; i++){
            chargerList.get(i).h = H[i];
        }

        double U = 0;
        for(Sensor s : sensorList){
            s.calEnergy(chargerList);
            U += senseScheduleV3(s);
        }
        return U;
    }

    static void sayEnergy(List<Sensor> sensors){
        for(Sensor s :sensors){
            System.out.println(s.Er);
        }
    }

    static void sayUtility(List<PoI> PoIs){
        for(PoI s :PoIs){
            System.out.println(s.Ur);
        }
    }
}




