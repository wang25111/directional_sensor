import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author cj
 * 批处理数据
 */

public class TestMinScale {
    static String type = "MinScale";
    static int t = 1;
    public static void main(String[] args) throws IOException {
        EntityFactory.size = 40;
        Algorithm.B = 10;
        Sensor.Ecap = 25;
        PoI.UrUp = 10;
        //参数变化值列表
        int addNum = 1;
        int[] MList = {2};
        //存放所有结果
        double[][][] res = new double[t][2][MList.length];
        long[][][] runTime = new long[t][2][MList.length];

        for(int i = 0; i < t; i++){
            //初始化网络
            Algorithm.init(2, 2, 10);
            for(int j = 0; j < MList.length; j++){
                long start = 0;
                long end = 0;
                //Algorithm.add(addNum, 0, 0);

                start = System.currentTimeMillis();
                res[i][0][j] = Algorithm.SSA_CSA();
                end = System.currentTimeMillis();
                runTime[i][0][j] = end - start;

                start = System.currentTimeMillis();
                res[i][1][j] = Algorithm.OPT_OPT();
                end = System.currentTimeMillis();
                runTime[i][1][j] = end - start;

            }
            System.out.println("第" + (i + 1) + "次已完成，剩余次数：" + (t - i - 1));
        }

        FileWriter fw = new FileWriter("C:\\Users\\HP\\Desktop\\data\\" + type +".txt");
        BufferedWriter bf = new BufferedWriter(fw);
        FileWriter fw1 = new FileWriter("C:\\Users\\HP\\Desktop\\data\\runTime_" + type + ".txt");
        BufferedWriter bf1 = new BufferedWriter(fw1);

        for(int k = 0; k < MList.length; k++){
            double[] ans = new double[2];
            double[] timeList = new double[2];
            for(int i = 0; i < 2; i++){
                double sum = 0;
                double times = 0;
                for(int j = 0; j < t; j++){
                    sum += res[j][i][k];
                    times += runTime[j][i][k] / 1000.0;
                }
                ans[i] = sum / t;
                timeList[i] = times / t;
            }

            //输出结果到控制台
            {
                System.out.println(type + " = " + MList[k]);
                System.out.println("SSA_CSA：" + ans[0]);
                System.out.println("OPT_OPT：" + ans[1]);
            }
            //运行时间
            {
                System.out.println("--");
                System.out.println("runTime_SSA_CSA：" + timeList[0]);
                System.out.println("runTime_OPT_OPT：" + timeList[1]);
            }

            //数据输出到文件
            {
                bf.write(type + " = " + MList[k]);
                bf.newLine();
                bf.write("SSA_CSA：" + ans[0]);
                bf.newLine();
                bf.write("OPT_OPT：" + ans[1]);
                if(k != MList.length - 1)
                    bf.newLine();
            }
            //运行时间
            {
                bf1.write(type + " = " + MList[k]);
                bf1.newLine();
                bf1.write("runTime_SSA_CSA：" + timeList[0]);
                bf1.newLine();
                bf1.write("runTime_RS_RC：" + timeList[1]);
                bf1.newLine();
                if(k != MList.length - 1)
                    bf1.newLine();
            }
        }

        bf.close();
        bf1.close();
    }

}
