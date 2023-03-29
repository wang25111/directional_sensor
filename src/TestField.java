import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

public class TestField {
    static {
        Algorithm.B = 15;
        Sensor.Ecap = 20;
        Sensor.R = 0.5;
        Sensor.a = 0.32;
        Sensor.b = 0.1;

        Charger.D = 1.6;
        Charger.alpha = 0.09;
        Charger.beta = 0.01;
        Sensor.e = 1.6;
        Charger.tao = 2;
    }
    public static void main(String[] args) throws IOException {
        List<Charger> chargerList = new ArrayList<>();
        List<Sensor> sensorList = new ArrayList<>();
        List<PoI> poIList = new ArrayList<>();
        double total_U = 0;
        {
            double[] charger_x = {0.7, 2.0, 2.2};
            double[] charger_y = {0.7, 2.7, 1.1};

            double[] sensor_x = {.7, .2,  1.3,   1.0,  1.4,  1.9, 1.7, 2.5};
            double[] sensor_y = {.45, .5, 1.0,  1.52,  1.5,  2.1, 2.5, 0.96};
            double[] sensor_Er = {5.2, 1.8, 4.6, 4.6, 3.2, 5.9, 2.3, 2.8};

            double[] poi_x =  {.52, .75,  0.67, .4, 2.23, 1.40,  1.65,   2.25,  1.5, 1.15, 1.25,  1.64,  1.93};
            double[] poi_y =  {.35, .25, 1.46, .56, 0.7,  2.4,  2.36,  .95,    1.2,   1.6, 1.36, 1.75,   1.8};
            double[] poi_Ur = { 10,   24,   16,  15,   15,   23,    14,   15,   18,   12,   7,   24,   18};
            double[] poi_wk = {  1,    2,    2,   1,    1,    3,     3,    3,   1,    3,    3,    3,    3
            };
            for(int i = 0; i < charger_x.length; i++){
                chargerList.add(new Charger(charger_x[i], charger_y[i]));
            }
            for(int i = 0; i < sensor_x.length; i++){
                sensorList.add(new Sensor(sensor_x[i], sensor_y[i], sensor_Er[i]));
            }
            for(int i = 0; i < poi_x.length; i++){
                PoI p = new PoI(poi_x[i], poi_y[i]);
                p.wk = poi_wk[i];
                total_U += p.Ur;
                poIList.add(p);
            }
        }
        Algorithm.init(chargerList, sensorList, poIList);
        System.out.println("PoIU_k^up:");
        for(PoI p : poIList){
            System.out.print(p.Ur + ", ");
        }
        System.out.println();
        System.out.println("total: " + total_U);
        double ssa_csa = Algorithm.SSA_CSA();
        getScheme("SSA_CSA", chargerList, sensorList);
        double fopa_pc = Algorithm.FOPA_PC();
        getScheme("FOPA_PC", chargerList, sensorList);
        double fopa_aa = Algorithm.FOPA_AA();
        getScheme("FOPA_AA", chargerList, sensorList);
        double mc_aa = Algorithm.MC_AA();
        getScheme("MC_AA", chargerList, sensorList);
        double mc_pc = Algorithm.MC_PC();
        getScheme("MC_PC", chargerList, sensorList);

        System.out.println("SSA_CSA: " + ssa_csa);
        System.out.println("FOPA_PC: " + fopa_pc);
        System.out.println("FOPA_AA: " + fopa_aa);
        System.out.println("MC_AA: " + mc_aa);
        System.out.println("MC_PC: " + mc_pc);
    }

    private static void getScheme(String AlgName, List<Charger> chargerList, List<Sensor> sensorList){
        FileWriter fw = null;
        BufferedWriter bf = null;
        try {
            fw = new FileWriter("C:\\Users\\HP\\Desktop\\data\\field\\"+ AlgName+".txt");
            bf = new BufferedWriter(fw);
            for(Charger c : chargerList){
                bf.write(c.h + " ");
            }
            bf.newLine();
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
