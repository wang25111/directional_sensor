import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

public class TestField {
    static {
        Algorithm.B = 10;
        Sensor.Ecap = 20;
        Sensor.R = 3;
        Charger.D = 10;
        Sensor.e = 1.6;
        PoI.UrUp = 10;
        PoI.wkUp = 3;
    }
    public static void main(String[] args) throws IOException {
        List<Charger> chargerList = new ArrayList<>();
        List<Sensor> sensorList = new ArrayList<>();
        List<PoI> poIList = new ArrayList<>();
        double total_U = 0;
        {
            double[] charger_x = {8, 13, 22};
            double[] charger_y = {8, 21, 14};

            double[] sensor_x = {5, 2, 13, 11, 14, 20, 17, 23};
            double[] sensor_y = {5, 5, 12, 16, 15, 22, 23, 11};
            double[] sensor_Er = {5.2, 1.8, 4.6, 4.6, 3.2, 1.9, 2.3, 2.8};

            double[] poi_x = {4, 1, 10, 3, 20.5, 19, 17.5, 22.5, 14, 11.5, 13, 15, 20};
            double[] poi_y = {4, 2.5, 15, 6, 9, 21,  21.5,  9.5,  10, 14.5, 16, 17, 20};
            double[] poi_Ur = {2, 5, 7, 9, 3, 7, 6, 3, 1, 2, 4, 6, 8};

            for(int i = 0; i < charger_x.length; i++){
                chargerList.add(new Charger(charger_x[i], charger_y[i]));
            }
            for(int i = 0; i < sensor_x.length; i++){
                sensorList.add(new Sensor(sensor_x[i], sensor_y[i], sensor_Er[i]));
            }
            for(int i = 0; i < poi_x.length; i++){
                PoI p = new PoI(poi_x[i], poi_y[i], poi_Ur[i]);
                p.wk = PoI.wkUp;
                total_U += p.Ur;
                poIList.add(p);
            }
        }
        Algorithm.init(chargerList, sensorList, poIList);

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
