import org.junit.Test;

import java.util.*;

/**
    感知算法
 */
public class BaseAlgorithm {

    //RS在开启方案H下带来的效用
    static double RandomUtility(int[] H, List<Charger> chargerList, List<Sensor> sensorList){
        //先开启充电器
        for(int i = 0; i < H.length; i++){
            chargerList.get(i).h = H[i];
        }

        double sum = 0;
        //再计算传感器充电后的能量
        for(Sensor s : sensorList){
            s.calEnergy(chargerList);
            sum += RandomSchedule(s);
        }
        return sum;
    }
    //RS算法（对单个传感器）
    private static double RandomSchedule(Sensor s){
        Random random = new Random();
        double U = 0;
        List<double[]> res= s.T;
        while(s.Er > 0.01 && s.poISet.size() > 0){
            double[] tmp = new double[2];
            //感知的方向是随机的
            double r = random.nextDouble() * 2 * Math.PI;
            //充电器最长工作时间
            double workTime = s.Er / s.e;
            //当前方向上的感知时间
            double senseTime = Integer.MAX_VALUE;
            for(PoI p : s.poISet){
                if(Util.Quality(s, p, r) > 0){
                    double time = p.Ur / Util.Quality(s, p, r);
                    senseTime = Math.min(senseTime, time);
                }
            }
            tmp[0] = r;
            tmp[1] = Math.min(workTime, senseTime);
            //感知的时间是随机的
            double rTime = random.nextDouble() * tmp[1];
            if(rTime <= tmp[1]){
                tmp[1] = rTime;
            }
            res.add(tmp);
            //更新剩余能量、效用
            s.Er -= s.e * tmp[1];
            //记录效用为0的兴趣点
            List<PoI> zero = new ArrayList<>();
            for(PoI p : s.poISet){
                U += Util.Quality(s, p, r) * tmp[1];
                p.Ur -= Util.Quality(s, p, r) * tmp[1];
                if(p.Ur <= 0) zero.add(p);
            }
            for(PoI p : zero){
                s.poISet.remove(p);
            }
        }
        return U;
    }

    //根据H，计算总充电量
    static double totalEnergy(int[] H, List<Charger> chargerList, List<Sensor> sensorList){
        for(int i = 0; i < H.length; i++){
            chargerList.get(i).h = H[i];
        }

        double totalEnergy = 0;
        for(Sensor s : sensorList){
            s.calEnergy(chargerList);
            totalEnergy += s.Er;
        }

        return totalEnergy;
    }

    //FOPA在开启方案H下带来的效用
    static double FOPAUtility(int[] H, List<Charger> chargerList, List<Sensor> sensorList){
        for(int i = 0; i < H.length; i++){
            chargerList.get(i).h = H[i];
        }

        double sum = 0;
        for(Sensor s : sensorList){
            s.calEnergy(chargerList);
            sum += FOPASchedule(s);
        }

        return sum;
    }
    //FOPA算法（对单个传感器）
    private static double FOPASchedule(Sensor s){
        double U = 0;
        List<double[]> res= s.T;
        while(s.Er > 0 && s.poISet.size() > 0){
            double[] tmp = new double[2];

            //考虑感知质量最大的某一个传感器
            double r = 0;
            double quality = 0;
            for(PoI p : s.poISet){
                double cur = Util.angle(s, p);
                double curQuality = Util.Quality(s, p, cur);
                if(curQuality > quality){
                    r = cur;
                    quality = curQuality;
                }
            }

            //充电器最长工作时间
            double workTime = s.Er / s.e;
            //当前方向上的感知时间
            double senseTime = Integer.MAX_VALUE;
            for(PoI p : s.poISet){
                if(Util.Quality(s, p, r) > 0){
                    double time = p.Ur / Util.Quality(s, p, r);
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
            for(PoI p : s.poISet){
                double tmpUtility = Util.Quality(s, p, tmp[0]) * tmp[1];
                U += tmpUtility;
                p.Ur -= tmpUtility;
                //防止可能出现的死循环
                if(p.Ur <= 0.1) zero.add(p);
            }
            for(PoI p : zero){
                s.poISet.remove(p);
            }
        }
        return U;
    }

    //MC算法在开启方案H下带来的感知效用
    static double MCUtility(int[] H, List<Charger> chargerList, List<Sensor> sensorList){
        for(int i = 0; i < H.length; i++){
            chargerList.get(i).h = H[i];
        }

        double sum = 0;
        for(Sensor s : sensorList){
            s.calEnergy(chargerList);
            sum += MCSchedule(s);
        }

        return sum;
    }
    //MC算法（对单个传感器）
    private static double MCSchedule(Sensor s){
        Random random = new Random();
        double U = 0;
        List<double[]> T = s.T;
        double step = 0.02;
        while(s.Er > 0 && s.poISet.size() != 0){
            //最多能覆盖多少传感器
            int MaxNum = 0;
            //覆盖最多时的起始、结束角度
            double start = 0;
            double end = 0;
            double[] tmp = new double[2];
            double workTime = s.Er / Sensor.e;
            double senseTime = Integer.MAX_VALUE;
            double r = 0;
            while(r < 2 * Math.PI){
                int tmpNum = 0;
                for(PoI p : s.poISet){
                    if(Util.Quality(s, p, r) > 0){
                        tmpNum++;
                    }
                }
                if(tmpNum > MaxNum){
                    MaxNum = tmpNum;
                    start = r;
                    end = r;
                }else if(MaxNum == tmpNum){
                    end = r;
                }
                r += step;
            }
            //在最大覆盖区间上随机选取一个角度
            tmp[0] = start + random.nextDouble() * (end - start);

            for(PoI p : s.poISet){
                if(Util.Quality(s, p, tmp[0]) > 0){
                    double time = p.Ur / Util.Quality(s, p, tmp[0]);
                    senseTime = Math.min(senseTime, time);
                }
            }
            //计算感知时间
            tmp[1] = Math.min(senseTime, workTime);
            T.add(tmp);

            //更新剩余能量和效用
            s.Er -= Sensor.e * tmp[1];
            List<PoI> zero = new ArrayList<>();
            for(PoI p : s.poISet){
                U += Util.Quality(s, p, tmp[0]) * tmp[1];
                p.Ur -= Util.Quality(s, p, tmp[0]) * tmp[1];
                if(p.Ur <= 0) zero.add(p);
            }
            for(PoI p : zero){
                s.poISet.remove(p);
            }

        }
        return U;
    }

    //CSA暴力搜索版本，返回结果等价于CSA算法
    static double CSAUtility(int[] H, List<Charger> chargerList, List<Sensor> sensorList){
        for(int i = 0; i < H.length; i++){
            chargerList.get(i).h = H[i];
        }

        double sum = 0;
        for(Sensor s : sensorList){
            s.calEnergy(chargerList);
            sum += CSASchedule(s);
        }

        return sum;
    }
    //CSA暴力搜索版本（对单个传感器）
    private static double CSASchedule(Sensor s) {
        double U = 0;
        double step = 0.03;
        List<double[]> T= s.T;
        while(s.Er > 0 && s.poISet.size() > 0){
            double[] tmp = new double[2];
            double r = 0;
            double quality = 0;
            while(r < 2 * Math.PI){
                double tmpQuality = 0;
                for(PoI p : s.poISet){
                    if (Util.Quality(s, p, r) > 0){
                        tmpQuality += Util.Quality(s, p, r);
                    }
                }
                if(tmpQuality > quality){
                    quality = tmpQuality;
                    tmp[0] = r;
                }
                r += step;
            }

            //充电器最长工作时间
            double workTime = s.Er / s.e;
            //当前方向上的感知时间
            double senseTime = Integer.MAX_VALUE;
            for(PoI p : s.poISet){
                if(Util.Quality(s, p, tmp[0]) > 0){
                    double time = p.Ur / Util.Quality(s, p, tmp[0]);
                    senseTime = Math.min(senseTime, time);
                }
            }

            tmp[1] = Math.min(workTime, senseTime);

            T.add(tmp);
            //更新剩余能量、效用
            s.Er -= s.e * tmp[1];
            //记录效用为0的兴趣点
            List<PoI> zero = new ArrayList<>();
            for(PoI p : s.poISet){
                double tmpUtility = Util.Quality(s, p, tmp[0]) * tmp[1];
                U += tmpUtility;
                p.Ur -= tmpUtility;
                if(p.Ur <= 0) zero.add(p);
            }
            for(PoI p : zero){
                s.poISet.remove(p);
            }
        }

        return U;
    }


    //OPT感知
    static double maxU = 0;
    static int delta = 15;
    static double timeStep = 1;
    static double[] Er;
    static double OPTUtility(int[] H, List<Charger> chargerList, List<Sensor> sensorList, List<PoI> poIList){
        for(int i = 0; i < H.length; i++){
            chargerList.get(i).h = H[i];
        }
        Er = new double[sensorList.size()];
        for(int i = 0; i < sensorList.size(); i++){
            Sensor s = sensorList.get(i);
            s.calEnergy(chargerList);
            Er[i] = s.Er;
        }
        double[][] senseDuration = new double[sensorList.size()][360/delta];

        OPTSchedule(0, senseDuration, sensorList, 0, (int)(sensorList.get(0).Er / Sensor.e / timeStep), poIList);
        return maxU;
    }

    private static void OPTSchedule(int curIndx, double[][] senseDuration, List<Sensor> sensorList, int i, int ErRemain, List<PoI> poIList) {
        if(ErRemain <= 0){
            //是否还有传感器未调度
            if(i != sensorList.size()-1){
                //调度下一个传感器
                OPTSchedule(0, senseDuration, sensorList, i + 1, (int)(Er[i + 1] / Sensor.e / timeStep), poIList);
                return;
            }else{
                Util.init(Er, sensorList, Algorithm.UrArray, poIList);
                //计算感知效用
                double curUtility = calUtility(senseDuration, sensorList);
               if(maxU < curUtility){
                   maxU = curUtility;
               }
                return;
            }
        }

        for(int j = curIndx; j < senseDuration[0].length; j++){
            double totalQuality = 0;
            for(PoI p : sensorList.get(i).poISet){
                totalQuality += Util.Quality(sensorList.get(i), p, 1.0 * j * delta / 360 * 2 * Math.PI);
            }
            if(totalQuality <= 0){
                continue;
            }
            senseDuration[i][j] += timeStep;
            OPTSchedule(j, senseDuration, sensorList, i, ErRemain - 1, poIList);
            senseDuration[i][j] -= timeStep;
        }
    }

    private static double calUtility(double[][] senseDuration, List<Sensor> sensorList){
        double res = 0;
        for(int i = 0; i < sensorList.size(); i++){
            for(int j = 0; j < 360 / delta; j++){
                List<PoI> zero = new ArrayList<>();
                for(PoI p : sensorList.get(i).poISet){
                    double quality = Util.Quality(sensorList.get(i), p, 1.0 * j * delta / 360 * 2 * Math.PI);
                    if(quality > 0){
                        double realUtility = Math.min(p.Ur, quality * senseDuration[i][j]);
                        res += realUtility;
                        p.Ur -= realUtility;
                        if(p.Ur <= 0) zero.add(p);
                    }
                }
                for(PoI p : zero){
                    sensorList.get(i).poISet.remove(p);
                }
            }
        }
        return res;
    }

    @Test
    public void test(){
        Sensor.Ecap = 25;
        PoI.UrUp = 10;
        Sensor s1 = new Sensor(0, 0);
        Sensor s2 = new Sensor(3, 0);
        PoI p1 = new PoI(1, 1);
        PoI p2 = new PoI(2, 0);
        PoI p3 = new PoI(0, 2);
        List<Sensor> sensorList = new ArrayList<>();
        List<PoI>  poIList = new ArrayList<>();
        sensorList.add(s1);
        sensorList.add(s2);
        poIList.add(p1);
        poIList.add(p2);
        poIList.add(p3);
        s1.getPoIs(poIList);
        s2.getPoIs(poIList);
        Algorithm.UrArray = new double[poIList.size()];
        for(int i = 0; i < poIList.size(); i++){
            Algorithm.UrArray[i] = poIList.get(i).Ur;
        }
        double[][] senseDuration = new double[sensorList.size()][360/delta];
        Er = new double[sensorList.size()];
        for(int i = 0; i < sensorList.size(); i++){
            Er[i] = sensorList.get(i).Er;
        }
        OPTSchedule(0, senseDuration, sensorList, 0, (int)(s1.Er / Sensor.e / timeStep), poIList);
        System.out.println(maxU);
    }

    @Test
    public void test_opt(){
        Sensor.Ecap = 25;
        PoI.UrUp = 10;
        Algorithm.B = 8;
        Algorithm.chargerList = new ArrayList<>();
        Algorithm.sensorList = new ArrayList<>();
        Algorithm.poIList = new ArrayList<>();
        {
            Charger c1 = new Charger(16, 10);
            Charger c2 = new Charger(0, 10);
            Algorithm.chargerList.add(c1);
            Algorithm.chargerList.add(c2);

            Sensor s1 = new Sensor(7, 10);
            Sensor s2 = new Sensor(20, 10);
            Sensor s3 = new Sensor(5, 8);
            Algorithm.sensorList.addAll(Arrays.asList(s1, s2));

            PoI p1 = new PoI(0, 10);
            PoI p2 = new PoI(5, 5);
            PoI p3 = new PoI(18, 10);
            PoI p4 = new PoI(21, 11);
            PoI p5 = new PoI(6, 9);
            PoI p6 = new PoI(9, 1);
            PoI p7 = new PoI(18, 11);
            PoI p8 = new PoI(0, 10.5);
            PoI p9 = new PoI(5, 6);
            PoI p10 = new PoI(18, 9);
            Algorithm.poIList.addAll(Arrays.asList(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10));
        }
        {
            Algorithm.UrArray = new double[Algorithm.poIList.size()];
            Algorithm.ErArray = new double[Algorithm.sensorList.size()];
            for(int i = 0; i < Algorithm.sensorList.size(); i++){
                Sensor s = Algorithm.sensorList.get(i);
                s.getPoIs(Algorithm.poIList);
                Algorithm.ErArray[i] = s.Er;
            }
            for(int i = 0; i < Algorithm.poIList.size(); i++){
                PoI p = Algorithm.poIList.get(i);
                Algorithm.UrArray[i] = p.Ur;
            }
            for(Charger c : Algorithm.chargerList){
                c.getSensors(Algorithm.sensorList);
            }
        }
        long start = System.currentTimeMillis();
        double v = Algorithm.OPT_OPT();
        long end = System.currentTimeMillis();

        long start1 = System.currentTimeMillis();
        double v1 = Algorithm.CSA_AA();
        long end1 = System.currentTimeMillis();
        System.out.print("OPT: ");
        System.out.print(v);
        System.out.println(";  时间: " + (end - start) / 1000.0 + "s");

        System.out.print("CSA_SSA: ");
        System.out.print(v);
        System.out.println(";  时间: " + (end1 - start1) / 1000.0 + "s");
    }
}
