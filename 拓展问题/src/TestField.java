import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TestField {
    static {
        Charger.B = 500;
        Sensor.Ecap = 20;
        Sensor.R = 3;
        Charger.D = 6;
        Charger.alpha = 13;
        Charger.beta = 3;
        Sensor.e = 1.6;
        PoI.Uk_up = 10;
        PoI.wkUp = 3;
        Charger.eta = 1.6;
    }
    static int m = 20;
    static int l = 35;
    static List<Garage> garageList;
    public static void main(String[] args) throws IOException {
        String input = "C:\\Users\\HP\\Desktop\\data\\field\\extend\\info.txt";
        Scanner scan = new Scanner(new FileReader(input));
        List<Sensor> sensorList = new ArrayList<>();
        List<PoI> poIList = new ArrayList<>();
        //初始化车库
        {
            Garage g1 = new Garage(12.0, 25.0, 3, 2);
            Route r11 = new Route(g1,4.0,30.0,10.2,36.7,19.5,30.0);
            Route r12 = new Route(g1,4.0,25.0,4.7,6.6,8.0,11.0);
            Route r13 = new Route(g1,22.0,24.0,26.6,20.5,16,15);
            g1.RouteList = new ArrayList<>(Arrays.asList(r11, r12, r13));

            Garage g2 = new Garage(24.0,8.0, 3, 1);
            Route r21 = new Route(g2,15,1.0,4.8,5.5,18,14.9);
            Route r22 = new Route(g2,35,3,34,11,28,11);
            Route r23 = new Route(g2,37,20,29,21,25,17);
            g2.RouteList = new ArrayList<>(Arrays.asList(r21, r22, r23));

            Garage g3 = new Garage(28.0,32.0, 2, 1);
            Route r31 = new Route(g3,36.5,31.5,35,35.5,24.5,36.5);
            Route r32 = new Route(g3,36.3,21.5,28.6,22,19.5,27.2);
            g3.RouteList = new ArrayList<>(Arrays.asList(r31, r32));

            garageList = new ArrayList<>(Arrays.asList(g1, g2, g3));
        }
        //初始化兴趣点和传感器
        int index = 0;
        while(scan.hasNextLine()){
            String line = scan.nextLine();
            if(index < m){
                String[] array = line.split(",");
                double x = Double.parseDouble(array[0]);
                double y = Double.parseDouble(array[1]);
                double Er = Double.parseDouble(array[2]);
                sensorList.add(new Sensor(x, y, Er));
            }else if(index < m + l){
                String[] array = line.split(",");
                double x = Double.parseDouble(array[0]);
                double y = Double.parseDouble(array[1]);
                double Ur = Double.parseDouble(array[2]);
                poIList.add(new PoI(x, y, Ur));
            }
            index++;
        }
        //网络之间建立连接
        Algorithm.init(sensorList, poIList, garageList);

        double ssa_rsa = Algorithm.SSA_RSA();
        getScheme("SSA_RSA", sensorList, Algorithm.globalV);
        double fopa_iaa = Algorithm.FOPA_IAA();
        getScheme("FOPA_IAA", sensorList, Algorithm.globalV);
        double fopa_igsa = Algorithm.FOPA_IGSA();
        getScheme("FOPA_IGSA", sensorList, Algorithm.globalV);
        double mc_iaa = Algorithm.MC_IAA();
        getScheme("MC_IAA", sensorList, Algorithm.globalV);
        double mc_igsa = Algorithm.MC_IGSA();
        getScheme("MC_IGSA", sensorList, Algorithm.globalV);

        System.out.println(ssa_rsa);
        System.out.println(fopa_iaa);
        System.out.println(fopa_igsa);
        System.out.println(mc_iaa);
        System.out.println(mc_igsa);
    }

    /**
     * 输出解
     * */
    private static void getScheme(String AlgName, List<Sensor> sensorList, List<Route> V){
        FileWriter fw = null;
        BufferedWriter bf = null;
        try {
            fw = new FileWriter("C:\\Users\\HP\\Desktop\\data\\field\\extend\\"+ AlgName+".txt");
            bf = new BufferedWriter(fw);
            //输出路径选择方案,一行即是一个车库的路径
            for (Route item : V) {
                if (garageList.get(0).RouteList.contains(item)) {
                    bf.write(item.toString());
                    bf.write(";");
                }
            }
            for (Route value : V) {
                if (garageList.get(1).RouteList.contains(value)) {
                    bf.write(value.toString());
                    bf.write(";");
                }
            }
            for (Route route : V) {
                if (garageList.get(2).RouteList.contains(route)) {
                    bf.write(route.toString());
                    bf.write(";");
                }
            }
            bf.newLine();
            //输出兴趣点的感知方案
            for (int i = 0; i < sensorList.size(); i++) {
                Sensor s = sensorList.get(i);
                for(int j = 0; j < s.T.size(); j++){
                    double[] t = s.T.get(j);
                    if(t[1] < 0.01){
                        continue;
                    }
                    bf.write(String.format("%.3f",t[0]));
                    bf.write(":");
                    bf.write(String.format("%.3f",t[1]));
                    if(j != s.T.size() - 1){
                        bf.write(",");
                    }
                }
                if(i != sensorList.size() - 1){
                    bf.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bf.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
