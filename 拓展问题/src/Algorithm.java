import java.util.*;

/**
 调用对比算法
 */
public class Algorithm {
    static Random random = new Random();

    static List<Sensor> sensorList;
    static List<PoI> poIList;
    static List<Garage> garageList;
    //每个车库的充电器数量：[1, chargerNumber]
    static int chargerNumber = 5;
    //每个车库的充电器数量：[chargerNumber, routeNumber]
    static int routeNumber = 12;
    //网络中的所有路径
    static List<Route> C;
    //记录兴趣点的初始Er、传感器的初始Er
    static double[] UrArray;
    static double[] ErArray;
   static List<Route> globalV = new ArrayList<>();
    //初始化未赋值的元素
    //随机生成
    static void init(int sensorNumber, int poINumber, int garageNumber){
        sensorList = EntityFactory.generateSensor(sensorNumber);
        poIList = EntityFactory.generatePoI(poINumber);
        garageList = EntityFactory.generateGarage(garageNumber, chargerNumber, routeNumber);
        C = new ArrayList<>();

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
        for(Garage g : garageList){
            C.addAll(g.RouteList);
        }
    }

    //手动生成
    static void init(List<Sensor> sensors, List<PoI> poIs, List<Garage> garages){
        sensorList = sensors;
        poIList = poIs;
        garageList = garages;
        C = new ArrayList<>();
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
        for(Garage g : garageList){
            C.addAll(g.RouteList);
        }
    }

    //在网络中新增m个传感器，l个兴趣点
    static void add(int m, int l){

        Util.init(ErArray, sensorList, UrArray, poIList);

        List<Sensor> sensors = EntityFactory.generateSensor(m);
        List<PoI> poIS = EntityFactory.generatePoI(l);
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
    }

    public static double SSA_RSA(){
        globalV.clear();;
        List<Route> CC = new ArrayList<>(C);
        List<Route> V = new ArrayList<>();
        //记录每个车库中选了多少条路径
        int[] routeNum = new int[garageList.size()];

        while(CC.size() != 0){
            Util.init(ErArray, sensorList, UrArray, poIList);
            double pre = Base.SSAUtility(V, sensorList);
            Route tmp = null;
            double diff = 0;

            for(Route r : CC){
                V.add(r);
                Util.init(ErArray, sensorList, UrArray, poIList);
                double tmpUtility = Base.SSAUtility(V, sensorList);
                if(tmpUtility - pre > diff){
                    tmp = r;
                    diff = tmpUtility - pre;
                }
                V.remove(r);
            }

            if(tmp == null){
                break;
            }else{
                CC.remove(tmp);
                boolean flag = false;
                for(int i = 0; i < garageList.size(); i++){
                    Garage g = garageList.get(i);
                    if(g.RouteList.contains(tmp)){
                        if(routeNum[i] + 1 > g.ChargerNumber){
                            flag = true;
                        }else{
                            routeNum[i]++;
                        }
                        break;
                    }
                }
                if(!flag){
                    V.add(tmp);
                }
            }

        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        globalV = V;
        return Base.SSAUtility(V, sensorList);
    }

    public static double RS_RR(){
        List<Route> V = new ArrayList<>();
        List<Garage> ggList = new ArrayList<>(garageList);
        for(Garage g : ggList){
            List<Route> rList = new ArrayList<>(g.RouteList);
            for(int i = 0; i < g.ChargerNumber; i++){
                Route r = rList.get(random.nextInt(rList.size()));
                V.add(r);
                rList.remove(r);
            }
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        return Base.RSUtility(V, sensorList);
    }

    public static double FOPA_IAA(){
        List<Route> V = new ArrayList<>();
        List<Route> CC = new ArrayList<>(C);

        //记录每个车库中选了多少条路径
        int[] routeNum = new int[garageList.size()];

        while(CC.size() != 0){
            Util.init(ErArray, sensorList, UrArray, poIList);
            double preEnergy = Base.energy(V, sensorList);
            Route tmp = null;
            double diff = 0;

            for(Route r : CC){
                V.add(r);
                Util.init(ErArray, sensorList, UrArray, poIList);
                double tmpEnergy = Base.energy(V, sensorList);
                if(tmpEnergy - preEnergy > diff){
                    tmp = r;
                    diff = tmpEnergy - preEnergy;
                }
                V.remove(r);
            }

            if(tmp == null){
                break;
            }else{
                CC.remove(tmp);
                boolean flag = false;
                for(int i = 0; i < garageList.size(); i++){
                    Garage g = garageList.get(i);
                    if(g.RouteList.contains(tmp)){
                        if(routeNum[i] + 1 > g.ChargerNumber){
                            flag = true;
                        }else{
                            routeNum[i]++;
                        }
                        break;
                    }
                }
                if(!flag){
                    V.add(tmp);
                }
            }

        }
        globalV.clear();
        globalV = V;
        Util.init(ErArray, sensorList, UrArray, poIList);
        return Base.FOPAUtility(V, sensorList);
    }

    public static double MC_IAA(){
        List<Route> V = new ArrayList<>();
        List<Route> CC = new ArrayList<>(C);

        //记录每个车库中选了多少条路径
        int[] routeNum = new int[garageList.size()];

        while(CC.size() != 0){
            Util.init(ErArray, sensorList, UrArray, poIList);
            double preEnergy = Base.energy(V, sensorList);
            Route tmp = null;
            double diff = 0;

            for(Route r : CC){
                V.add(r);
                Util.init(ErArray, sensorList, UrArray, poIList);
                double tmpEnergy = Base.energy(V, sensorList);
                if(tmpEnergy - preEnergy > diff){
                    tmp = r;
                    diff = tmpEnergy - preEnergy;
                }
                V.remove(r);
            }

            if(tmp == null){
                break;
            }else{
                CC.remove(tmp);
                boolean flag = false;
                for(int i = 0; i < garageList.size(); i++){
                    Garage g = garageList.get(i);
                    if(g.RouteList.contains(tmp)){
                        if(routeNum[i] + 1 > g.ChargerNumber){
                            flag = true;
                        }else{
                            routeNum[i]++;
                        }
                        break;
                    }
                }
                if(!flag){
                    V.add(tmp);
                }
            }

        }
        globalV.clear();
        globalV = V;
        Util.init(ErArray, sensorList, UrArray, poIList);
        return Base.MCUtility(V, sensorList);
    }

    public static double MC_CMP(){
        List<Route> V = new ArrayList<>();

        Util.init(ErArray, sensorList, UrArray, poIList);
        for(Garage g : garageList){
            List<Route> rs = new ArrayList<>(g.RouteList);
            if (rs.isEmpty()) continue;
            //用于记录每个路径覆盖的兴趣点数量
            int[] poINum = new int[rs.size()];
            for(int i = 0; i < rs.size(); i++){
                Set<PoI> set = new HashSet<>();
                for(Sensor s : sensorList){
                    if(Util.isCover(s, rs.get(i))){
                       set.addAll(s.poISet);
                    }
                }
                poINum[i] = set.size();
            }
            for(int i = 0; i < g.ChargerNumber; i++){
                int index = 0;
                for(int j = 0; j < poINum.length; j++){
                    if(poINum[j] > poINum[index]){
                        index = j;
                    }
                }
                poINum[index] = 0;
                V.add(rs.get(index));
            }
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        return Base.MCUtility(V, sensorList);
    }

    public static double FOPA_CMP(){
        List<Route> V = new ArrayList<>();

        Util.init(ErArray, sensorList, UrArray, poIList);
        for(Garage g : garageList){
            List<Route> rs = new ArrayList<>(g.RouteList);
            if(rs.isEmpty()) continue;
            //用于记录每个路径覆盖的兴趣点数量
            int[] poINum = new int[rs.size()];
            for(int i = 0; i < rs.size(); i++){
                Set<PoI> set = new HashSet<>();
                for(Sensor s : sensorList){
                    if(Util.isCover(s, rs.get(i))){
                        set.addAll(s.poISet);
                    }
                }
                poINum[i] = set.size();
            }
            for(int i = 0; i < g.ChargerNumber; i++){
                int index = 0;
                for(int j = 0; j < poINum.length; j++){
                    if(poINum[j] > poINum[index]){
                        index = j;
                    }
                }
                poINum[index] = 0;
                V.add(rs.get(index));
            }
        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        return Base.FOPAUtility(V, sensorList);
    }

    public static double SSA_IAA(){
        List<Route> V = new ArrayList<>();
        List<Route> CC = new ArrayList<>(C);

        //记录每个车库中选了多少条路径
        int[] routeNum = new int[garageList.size()];

        while(CC.size() != 0){
            Util.init(ErArray, sensorList, UrArray, poIList);
            double preEnergy = Base.energy(V, sensorList);
            Route tmp = null;
            double diff = 0;

            for(Route r : CC){
                V.add(r);
                Util.init(ErArray, sensorList, UrArray, poIList);
                double tmpEnergy = Base.energy(V, sensorList);
                if(tmpEnergy - preEnergy > diff){
                    tmp = r;
                    diff = tmpEnergy - preEnergy;
                }
                V.remove(r);
            }

            if(tmp == null){
                break;
            }else{
                CC.remove(tmp);
                boolean flag = false;
                for(int i = 0; i < garageList.size(); i++){
                    Garage g = garageList.get(i);
                    if(g.RouteList.contains(tmp)){
                        if(routeNum[i] + 1 > g.ChargerNumber){
                            flag = true;
                        }else{
                            routeNum[i]++;
                        }
                        break;
                    }
                }
                if(!flag){
                    V.add(tmp);
                }
            }

        }

        Util.init(ErArray, sensorList, UrArray, poIList);
        return Base.SSAUtility(V, sensorList);
    }

    public static double FOPA_IGSA(){
        List<Route> V = new ArrayList<>();

        Util.init(ErArray, sensorList, UrArray, poIList);
        for(Garage g : garageList){
            List<Route> rs = new ArrayList<>(g.RouteList);
            if(rs.isEmpty()) continue;
            //用于记录每个路径覆盖的充电器数量/路径长度
            double[] poINum = new double[rs.size()];
            for(int i = 0; i < rs.size(); i++){
                //记录当前路径覆盖了多少传感器
                int tmp = 0;
                for(Sensor s : sensorList){
                    if(Util.isCover(s, rs.get(i))){
                        tmp++;
                    }
                }
                poINum[i] = tmp / rs.get(i).length;
            }
            for(int i = 0; i < g.ChargerNumber; i++){
                int index = 0;
                for(int j = 0; j < poINum.length; j++){
                    if(poINum[j] > poINum[index]){
                        index = j;
                    }
                }
                poINum[index] = 0;
                V.add(rs.get(index));
            }
        }
        globalV.clear();
        globalV = V;
        Util.init(ErArray, sensorList, UrArray, poIList);
        return Base.FOPAUtility(V, sensorList);
    }

    public static double MC_IGSA(){
        List<Route> V = new ArrayList<>();

        Util.init(ErArray, sensorList, UrArray, poIList);
        for(Garage g : garageList){
            List<Route> rs = new ArrayList<>(g.RouteList);
            if(rs.isEmpty()) continue;
            //用于记录每个路径覆盖的充电器数量/路径长度
            double[] poINum = new double[rs.size()];
            for(int i = 0; i < rs.size(); i++){
                //记录当前路径覆盖了多少传感器
                int tmp = 0;
                for(Sensor s : sensorList){
                    if(Util.isCover(s, rs.get(i))){
                        tmp++;
                    }
                }
                poINum[i] = tmp / rs.get(i).length;
            }
            for(int i = 0; i < g.ChargerNumber; i++){
                int index = 0;
                for(int j = 0; j < poINum.length; j++){
                    if(poINum[j] > poINum[index]){
                        index = j;
                    }
                }
                poINum[index] = 0;
                V.add(rs.get(index));
            }
        }

        globalV.clear();
        globalV = V;
        Util.init(ErArray, sensorList, UrArray, poIList);
        return Base.MCUtility(V, sensorList);
    }

    public static double getUP(){
        Util.init(ErArray, sensorList, UrArray, poIList);
        double up = 0;
        for(PoI p : poIList){
            for(Sensor s : sensorList){
                if(s.poISet.contains(p)){
                    up += p.Ur;
                    break;
                }
            }
        }
        return  up;
    }
}
