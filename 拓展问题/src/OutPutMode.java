import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OutPutMode {
    static {
        EntityFactory.size = 40;
        Charger.B = 10;
        Sensor.Ecap = 20;
        Sensor.R = 3;
        Charger.D = 10;
        Sensor.e = 1.6;
        PoI.Uk_up = 10;
        PoI.wkUp = 3;
    }
    public static void main(String[] args) {
        Garage g1 = new Garage(12, 25, 4, 2);
        Garage g2 = new Garage(24, 8, 4, 2);
        Garage g3 = new Garage(28, 32, 3, 1);
        List<Garage> garages = new ArrayList<>(Arrays.asList(g1, g2, g3));
        List<Sensor> sensors = EntityFactory.generateSensor(20);
        List<PoI> poIs = EntityFactory.generatePoI(35);

        getModeInfo(sensors, poIs, garages);

    }

    private static void getModeInfo(List<Sensor> sensorList, List<PoI> poIList, List<Garage> garageList){
        FileWriter fw = null;
        BufferedWriter bf = null;
        try {
            fw = new FileWriter("C:\\Users\\HP\\Desktop\\data\\field\\extend\\info.txt");
            bf = new BufferedWriter(fw);

            for (int i = 0; i < sensorList.size(); i++) {
                Sensor s = sensorList.get(i);
                bf.write(s.toString());
                bf.newLine();
            }
            for(int i = 0; i < poIList.size(); i++){
                PoI p = poIList.get(i);
                bf.write(p.toString());
                bf.newLine();
            }
            for(int i = 0; i < garageList.size(); i++){
                Garage g = garageList.get(i);
                StringBuilder paths = new StringBuilder();
                for(int j = 0; j < g.RouteList.size(); j++){
                    paths.append(g.RouteList.get(j).toString());
                    if(j != g.RouteList.size() - 1){
                        paths.append(";");
                    }
                }
                bf.write(paths.toString());
                if(i != garageList.size() - 1){
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
