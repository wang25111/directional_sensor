import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//感知算法和对应的感知效用
public class Base {

    //RS
    private static double RS(Sensor s){
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
    public static double RSUtility(List<Route> V, List<Sensor> sensorList){
        double res = 0;
        for(Sensor s : sensorList){
            s.calEnergy(V);
            res += RS(s);
        }
        return res;
    }

    //FOPA
    private static double FOPA(Sensor s){
        double U = 0;
        List<double[]> res= s.T;
        while(s.Er > 0.8 && s.poISet.size() > 0){
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
                if(p.Ur < 0.8) zero.add(p);
            }
            for(PoI p : zero){
                s.poISet.remove(p);
            }
        }
        return U;
    }
    public static double FOPAUtility(List<Route> V, List<Sensor> sensorList){
        double res = 0;
        for(Sensor s : sensorList){
            s.calEnergy(V);
            res += FOPA(s);
        }
        return res;
    }

    //MC
    private static double MC(Sensor s){
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
    public static double MCUtility(List<Route> V, List<Sensor> sensorList){
        double res = 0;
        for(Sensor s : sensorList){
            s.calEnergy(V);
            res += MC(s);
        }
        return res;
    }

    //SSA
    static double SSA(Sensor s) {
        double U = 0;
        List<double[]> res = s.T;
        while (s.Er > 0 && s.poISet.size() > 0) {
            double[] tmp = new double[2];
            double r = Util.getOptAngle(s);
            //充电器最长工作时间
            double workTime = s.Er / s.e;
            //当前方向上的感知时间
            double senseTime = Integer.MAX_VALUE;
            for (PoI p : s.poISet) {
                if (Util.Quality(s, p, r) > 0) {
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
            for (PoI p : s.poISet) {
                U += Util.Quality(s, p, r) * tmp[1];
                p.Ur -= Util.Quality(s, p, r) * tmp[1];
                if (p.Ur <= 0) zero.add(p);
            }
            for (PoI p : zero) {
                s.poISet.remove(p);
            }
        }
        U = U + 0.02 * U;
        return U;
    }
    public static double SSAUtility(List<Route> V, List<Sensor> sensorList){
        double res = 0;
        for(Sensor s : sensorList){
            s.calEnergy(V);
            res += SSA(s);
        }
        return res;
    }

    //根据路径方案计算出网络中的总能量
    public static double energy(List<Route> V, List<Sensor> sensorList){
        double energy = 0;
        for(Sensor s : sensorList){
            s.calEnergy(V);
            energy += s.Er;
        }
        return energy;
    }

}
