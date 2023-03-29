import org.junit.Test;

import java.util.*;

public class Algorithm {
    static List<Charger> chargerList;
    static List<Sensor> sensorList;
    static  List<PoI> poIList;
    //默认时间片的数量
    static int B = 120;

    //记录兴趣点的初始Er、传感器的初始Er
    static double[] UrArray;
    static double[] ErArray;

    //初始化chargerList、sensorList、poIList
    static void init(int m, int n, int l){
        chargerList = EntityFactory.generateCharger(n);
        sensorList = EntityFactory.generateSensor(m);
        poIList = EntityFactory.generatePoI(l);

        UrArray = new double[poIList.size()];
        ErArray = new double[sensorList.size()];
        for(int i = 0; i < sensorList.size(); i++){
            Sensor s = sensorList.get(i);
            s.getPoIs(poIList);
            ErArray[i] = s.Er;
        }
        for(int i = 0; i < poIList.size(); i++){
            PoI p = poIList.get(i);
            UrArray[i] = p.Ur;
        }
        for(Charger c : chargerList){
            c.getSensors(sensorList);
        }
    }

    static void init(List<Charger> chargers, List<Sensor> sensors, List<PoI> poIs){
        chargerList = chargers;
        sensorList = sensors;
        poIList = poIs;

        UrArray = new double[poIList.size()];
        ErArray = new double[sensorList.size()];
        for(int i = 0; i < sensorList.size(); i++){
            Sensor s = sensorList.get(i);
            s.getPoIs(poIList);
            ErArray[i] = s.Er;
        }
        for(int i = 0; i < poIList.size(); i++){
            PoI p = poIList.get(i);
            UrArray[i] = p.Ur;
        }
        for(Charger c : chargerList){
            c.getSensors(sensorList);
        }
    }

    //新增m个传感器、n个充电器、l个兴趣点
    static void add(int m, int n, int l){

        Util.init(ErArray, sensorList, UrArray, poIList);

        List<Charger> chargers = EntityFactory.generateCharger(n);
        List<Sensor> sensors = EntityFactory.generateSensor(m);
        List<PoI> poIS = EntityFactory.generatePoI(l);
        chargerList.addAll(chargers);
        sensorList.addAll(sensors);
        poIList.addAll(poIS);

        UrArray = new double[poIList.size()];
        ErArray = new double[sensorList.size()];
        for(int i = 0; i < sensorList.size(); i++){
            Sensor s = sensorList.get(i);
            s.getPoIs(poIList);
            ErArray[i] = s.Er;
        }
        for(int i = 0; i < poIList.size(); i++){
            PoI p = poIList.get(i);
            UrArray[i] = p.Ur;
        }
        for(Charger c : chargerList){
            c.getSensors(sensorList);
        }
    }

    //测试方法
    public static void main(String[] args) {

    }

    //SSA+CSA
    public static double SSA_CSA(){
        int B = Algorithm.B;
        int[] H = new int[chargerList.size()];
        while(B > 0){
            //初始化传感器的剩余能量、感知方案、兴趣点的剩余效用
            Util.init(ErArray, sensorList, UrArray, poIList);

            int index = -1;
            double diff = 0;
            double u1 = Util.totalUtility(H, chargerList, sensorList, poIList);

            for(int i = 0; i < chargerList.size(); i++){
                //计算完后，需要重置传感器的剩余能量，兴趣点的剩余效用
                Util.init(ErArray, sensorList, UrArray, poIList);
                H[i]++;
                double u2 = Util.totalUtility(H, chargerList, sensorList, poIList);
                H[i]--;

                if(u2 - u1 > diff){
                    diff = u2 - u1;
                    index = i;
                }
            }
            if(index != -1) H[index]++;
            B--;
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        //调用CSA感知算法，并最终输出每个poi获取的效用、每个sensor获取的效用
        double res = Util.totalUtility(H, chargerList, sensorList, poIList, "SSA+CSA");
        for(int i = 0; i < UrArray.length; i++){
            System.out.print(UrArray[i] - poIList.get(i).Ur + ",");
        }
        System.out.println();
        return res;
    }

    public static double SSA_CSA_V2(){
        int B = Algorithm.B;
        int[] H = new int[chargerList.size()];
        while(B > 0){
            //初始化传感器的剩余能量、感知方案、兴趣点的剩余效用
            Util.init(ErArray, sensorList, UrArray, poIList);
            int index = -1;
            double diff = 0;
            double u1 = BaseAlgorithm.CSAUtility(H, chargerList, sensorList);

            for(int i = 0; i < chargerList.size(); i++){
                //计算完后，需要重置传感器的剩余能量，兴趣点的剩余效用
                Util.init(ErArray, sensorList, UrArray, poIList);
                H[i]++;
                double u2 = BaseAlgorithm.CSAUtility(H, chargerList, sensorList);
                H[i]--;

                if(u2 - u1 > diff){
                    diff = u2 - u1;
                    index = i;
                }
            }
            if(index != -1) H[index]++;
            B--;
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        double res = BaseAlgorithm.CSAUtility(H, chargerList, sensorList);
        return res;
    }

    //本文算法 版本3.0
    public static double SSA_CSA_V3(){
        int B = Algorithm.B;
        int[] H = new int[chargerList.size()];
        while(B > 0){
            //初始化传感器的剩余能量、感知方案、兴趣点的剩余效用
            Util.init(ErArray, sensorList, UrArray, poIList);
            int index = -1;
            double diff = 0;
            double u1 = Util.totalUtilityV3(H, chargerList, sensorList);

            for(int i = 0; i < chargerList.size(); i++){
                //计算完后，需要重置传感器的剩余能量，兴趣点的剩余效用
                Util.init(ErArray, sensorList, UrArray, poIList);
                H[i]++;
                double u2 = Util.totalUtilityV3(H, chargerList, sensorList);
                H[i]--;

                if(u2 - u1 > diff){
                    diff = u2 - u1;
                    index = i;
                }
            }
            if(index != -1) H[index]++;
            B--;
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        double res = Util.totalUtilityV3(H, chargerList, sensorList);
        return res;
    }

    //RS+RC
    public static double RS_RC(){
        int B = Algorithm.B;
        int[] H = new int[chargerList.size()];
        Random random = new Random();
        while(B > 0){
            H[random.nextInt(H.length)]++;
            B--;
        }
        Util.init(ErArray, sensorList, UrArray, poIList);
        double res = BaseAlgorithm.RandomUtility(H, chargerList, sensorList);
        return res;
    }

    //CSA + AA充电
    public static double CSA_AA(){
        int B = Algorithm.B;
        int[] H = new int[chargerList.size()];
        while(B > 0){
            Util.init(ErArray, sensorList, UrArray, poIList);

            int index = -1;
            double diff = 0;
            double preEnergy = BaseAlgorithm.totalEnergy(H, chargerList, sensorList);
            for(int i = 0; i < H.length; i++){
                Util.init(ErArray, sensorList, UrArray, poIList);
                H[i]++;
                double curEnergy = BaseAlgorithm.totalEnergy(H, chargerList, sensorList);
                H[i]--;
                if(curEnergy - preEnergy > diff){
                    diff = curEnergy - preEnergy;
                    index = i;
                }
            }
            B--;
            if(index != -1) H[index]++;
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        double res = Util.totalUtility(H, chargerList, sensorList, poIList);
        for(int i = 0; i < UrArray.length; i++){
            System.out.print(UrArray[i] - poIList.get(i).Ur + ",");
        }
        System.out.println();
        return res;
    }

    //FOPA感知 + AA充电
    public static double FOPA_AA(){
        int B = Algorithm.B;
        Util.init(ErArray, sensorList, UrArray, poIList);
        int[] H = new int[chargerList.size()];

        while(B > 0){
            Util.init(ErArray, sensorList, UrArray, poIList);

            int index = -1;
            double diff = 0;
            double preEnergy = BaseAlgorithm.totalEnergy(H, chargerList, sensorList);
            for(int i = 0; i < H.length; i++){
                Util.init(ErArray, sensorList, UrArray, poIList);
                H[i]++;
                double curEnergy = BaseAlgorithm.totalEnergy(H, chargerList, sensorList);
                H[i]--;
                if(curEnergy - preEnergy > diff){
                    diff = curEnergy - preEnergy;
                    index = i;
                }
            }
            B--;
            if(index != -1) H[index]++;
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        double res = BaseAlgorithm.FOPAUtility(H, chargerList, sensorList, "FOPA+AA");
        for(int i = 0; i < UrArray.length; i++){
            System.out.print(UrArray[i] - poIList.get(i).Ur + ",");
        }
        System.out.println();
        return res;
    }

    //FOPA感知 + CSA充电
    public static  double FOPA_CSA(){
        int B = Algorithm.B;
        int[] H = new int[chargerList.size()];
        while(B > 0){
            //初始化传感器的剩余能量、感知方案、兴趣点的剩余效用
            Util.init(ErArray, sensorList, UrArray, poIList);

            int index = -1;
            double diff = 0;
            double u1 = BaseAlgorithm.FOPAUtility(H, chargerList, sensorList);

            for(int i = 0; i < chargerList.size(); i++){
                //计算完后，需要重置传感器的剩余能量，兴趣点的剩余效用
                Util.init(ErArray, sensorList, UrArray, poIList);
                H[i]++;
                double u2 = BaseAlgorithm.FOPAUtility(H, chargerList, sensorList);
                H[i]--;

                if(u2 - u1 > diff){
                    diff = u2 - u1;
                    index = i;
                }
            }
            if(index != -1) H[index]++;
            B--;
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        double res = BaseAlgorithm.FOPAUtility(H, chargerList, sensorList);
        return res;
    }

    //MC感知 + PC充电
    public static double MC_PC(){
        Util.init(ErArray, sensorList, UrArray, poIList);
        int B = Algorithm.B;
        double U = 0;
        int[] H = new int[chargerList.size()];
        //记录每个充电器覆盖的兴趣点数量
        int[] PoINumList = new int[chargerList.size()];
        //计算比例的分母
        int den = 0;
        for(int i = 0; i < chargerList.size(); i++){
            int PoINum = 0;
            for(Sensor s : chargerList.get(i).sensorSet){
                PoINum += s.poISet.size();
            }
            den += PoINum;
            PoINumList[i] = PoINum;
        }

        int remain = B;
        if(den != 0){
            for(int i = 0; i < H.length - 1; i++){
                H[i] = B * PoINumList[i] / den;
                remain -= H[i];
            }
        }
        //保证时间片全部用掉
        H[H.length - 1] = remain;

        Util.init(ErArray, sensorList, UrArray, poIList);
        //调用感知算法
        U += BaseAlgorithm.MCUtility(H, chargerList, sensorList, "MC+PC");
        for(int i = 0; i < UrArray.length; i++){
            System.out.print(UrArray[i] - poIList.get(i).Ur + ",");
        }
        System.out.println();
        return U;
    }

    //FOPA感知 + PC充电
    public static double FOPA_PC(){
        Util.init(ErArray, sensorList, UrArray, poIList);
        int B = Algorithm.B;
        double U = 0;
        int[] H = new int[chargerList.size()];
        //记录每个充电器覆盖的兴趣点数量
        int[] PoINumList = new int[chargerList.size()];
        //计算比例的分母
        int den = 0;
        for(int i = 0; i < chargerList.size(); i++){
            int PoINum = 0;
            for(Sensor s : chargerList.get(i).sensorSet){
                PoINum += s.poISet.size();
            }
            den += PoINum;
            PoINumList[i] = PoINum;
        }


        int remain = B;
        if(den != 0){
            for(int i = 0; i < H.length - 1; i++){
                H[i] = B * PoINumList[i] / den;
                remain -= H[i];
            }
        }
        H[H.length - 1] = remain;
        Util.init(ErArray, sensorList, UrArray, poIList);
        //调用感知算法
        U += BaseAlgorithm.FOPAUtility(H, chargerList, sensorList, "FOPA+PC");
        for(int i = 0; i < UrArray.length; i++){
            System.out.print(UrArray[i] - poIList.get(i).Ur + ",");
        }
        System.out.println();
        return U;
    }

    //MC感知 + AA充电
    public static double MC_AA(){
        int B = Algorithm.B;
        Util.init(ErArray, sensorList, UrArray, poIList);
        int[] H = new int[chargerList.size()];

        while(B > 0){
            Util.init(ErArray, sensorList, UrArray, poIList);

            int index = -1;
            double diff = 0;
            double preEnergy = BaseAlgorithm.totalEnergy(H, chargerList, sensorList);
            for(int i = 0; i < H.length; i++){
                Util.init(ErArray, sensorList, UrArray, poIList);
                H[i]++;
                double curEnergy = BaseAlgorithm.totalEnergy(H, chargerList, sensorList);
                H[i]--;
                if(curEnergy - preEnergy > diff){
                    diff = curEnergy - preEnergy;
                    index = i;
                }
            }
            B--;
            if(index != -1) H[index]++;
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        double res = BaseAlgorithm.MCUtility(H, chargerList, sensorList, "MC+AA");
        for(int i = 0; i < UrArray.length; i++){
            System.out.print(UrArray[i] - poIList.get(i).Ur + ",");
        }
        System.out.println();
        return res;
    }

    static double maxU = 0;
    public static double OPT_OPT(){
        trace(B, new int[chargerList.size()], 0);
        return maxU;
    }

    private static void trace(int Remain, int[] H, int index){
        if(Remain == 0){
            Util.init(ErArray, sensorList, UrArray, poIList);
            //调用OPT感知算法
            double tmpU = BaseAlgorithm.OPTUtility(H, chargerList, sensorList, poIList);
            maxU = Math.max(tmpU, maxU);
            return;
        }

        for(int i = index; i < H.length; i++){
            H[i]++;
            trace(Remain - 1, H, i);
            H[i]--;
        }
    }

    @Test
    public void test(){
        B = 10;
        init(5, 2, 20);
        Algorithm.OPT_OPT();
    }

}
