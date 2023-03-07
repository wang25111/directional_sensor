import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author cj
 * 批处理数据
 */

public class TestEcap {
    static String type = "Ecap";
    //迭代次数
    static int t = 120;
    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("C:\\Users\\HP\\Desktop\\data\\" + type +".txt");
        BufferedWriter bf = new BufferedWriter(fw);
        FileWriter fw1 = new FileWriter("C:\\Users\\HP\\Desktop\\data\\runTime_" + type + ".txt");
        BufferedWriter bf1 = new BufferedWriter(fw1);

        //参数变化值列表
        double[] EList = {20, 35, 50, 65, 80, 95};
        //存放所有结果
        double[][][] res = new double[t][6][EList.length];
        long[][][] runTime = new long[t][6][EList.length];
        for(int i = 0; i < t; i++){
            //初始化网络
            for(int j = 0; j < EList.length; j++){
                Sensor.Ecap = EList[j];
                Algorithm.init(120, 50, 400);

                long start = 0;
                long end = 0;
                start = System.currentTimeMillis();
                res[i][0][j] = Algorithm.SSA_CSA();
                end = System.currentTimeMillis();
                runTime[i][0][j] = end - start;

                start = System.currentTimeMillis();
                res[i][1][j] = Algorithm.RS_RC();
                end = System.currentTimeMillis();
                runTime[i][1][j] = end - start;

                start = System.currentTimeMillis();
                res[i][2][j] = Algorithm.FOPA_PC();
                end = System.currentTimeMillis();
                runTime[i][2][j] = end - start;

                start = System.currentTimeMillis();
                res[i][3][j] = Algorithm.FOPA_AA();
                end = System.currentTimeMillis();
                runTime[i][3][j] = end - start;

                start = System.currentTimeMillis();
                res[i][4][j] = Algorithm.MC_AA();
                end = System.currentTimeMillis();
                runTime[i][4][j] = end - start;

                start = System.currentTimeMillis();
                res[i][5][j] = Algorithm.MC_PC();
                end = System.currentTimeMillis();
                runTime[i][5][j] = end - start;
            }
            System.out.println("第" + (i + 1) + "次已完成，剩余次数：" + (t - i - 1));
        }

        //记录当B = EList[k]时，6个算法的各自平均值
        for(int k = 0; k < EList.length; k++){
            //效用
            double[] ans = new double[6];
            //单位：s
            double[] timeList = new double[6];
            //第i个算法
            for(int i = 0; i < 6; i++){
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
                System.out.println(type + " = " + EList[k]);
                System.out.println("SSA_CSA：" + ans[0]);
                System.out.println("RS_RC：" + ans[1]);
                System.out.println("FOPA_PC：" + ans[2]);
                System.out.println("FOPA_AA：" + ans[3]);
                System.out.println("MC_AA：" + ans[4]);
                System.out.println("MC_PC：" + ans[5]);
            }
            //运行时间
            {
                System.out.println(type + " = " + EList[k]);
                System.out.println("runTime_SSA_CSA：" + timeList[0]);
                System.out.println("runTime_RS_RC：" + timeList[1]);
                System.out.println("runTime_FOPA_PC：" + timeList[2]);
                System.out.println("runTime_FOPA_AA：" + timeList[3]);
                System.out.println("runTime_MC_AA：" + timeList[4]);
                System.out.println("runTime_MC_PC：" + timeList[5]);
            }

            //数据输出到文件
            {
                bf.write(type + " = " + EList[k]);
                bf.newLine();
                bf.write("SSA_CSA：" + ans[0]);
                bf.newLine();
                bf.write("RS_RC：" + ans[1]);
                bf.newLine();
                bf.write("FOPA_PC：" + ans[2]);
                bf.newLine();
                bf.write("FOPA_AA：" + ans[3]);
                bf.newLine();
                bf.write("MC_AA：" + ans[4]);
                bf.newLine();
                bf.write("MC_PC：" + ans[5]);
                if(k != EList.length - 1)
                    bf.newLine();
            }
            //运行时间
            {
                bf1.write(type + " = " + EList[k]);
                bf1.newLine();
                bf1.write("runTime_SSA_CSA：" + timeList[0]);
                bf1.newLine();
                bf1.write("runTime_RS_RC：" + timeList[1]);
                bf1.newLine();
                bf1.write("runTime_FOPA_PC：" + timeList[2]);
                bf1.newLine();
                bf1.write("runTime_FOPA_AA：" + timeList[3]);
                bf1.newLine();
                bf1.write("runTime_MC_AA：" + timeList[4]);
                bf1.newLine();
                bf1.write("runTime_MC_PC：" + timeList[5]);
                if(k != EList.length - 1)
                    bf1.newLine();
            }
        }

        bf.close();
        bf1.close();
    }

}
