import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author cj
 * 批处理数据
 */

public class TestB {
    static String type = "B";
    //迭代次数
    static int t = 100;
    public static void main(String[] args) throws IOException {
        //参数变化值列表
        int[] SList = {1500, 3000, 4500, 6000, 7500, 9000, 10500};
        //存放所有结果
        double[][][] res = new double[t][6][SList.length];
        long[][][] runTime = new long[t][6][SList.length];
        for(int i = 0; i < t; i++){
            //初始化网络
            for(int j = 0; j < SList.length; j++){
                Charger.B = SList[j];
                Algorithm.init(300, 800, 6);
                long start = 0;
                long end = 0;
                System.out.println(type + " = " + SList[j] + "-----------------------");
                System.out.println("SSA+RSA启动");
                start = System.currentTimeMillis();
                res[i][0][j] = Algorithm.SSA_RSA();
                end = System.currentTimeMillis();
                runTime[i][0][j] = end - start;
                System.out.println("SSA+RSA结束，耗时：" + (runTime[i][0][j] / 1000));

                System.out.println("RS+RR启动");
                start = System.currentTimeMillis();
                res[i][1][j] = Algorithm.RS_RR();
                end = System.currentTimeMillis();
                runTime[i][1][j] = end - start;
                System.out.println("RS+RR结束，耗时：" + (runTime[i][1][j] / 1000));

                System.out.println("FOPA+IAA启动");
                start = System.currentTimeMillis();
                res[i][2][j] = Algorithm.FOPA_IAA();
                end = System.currentTimeMillis();
                runTime[i][2][j] = end - start;
                System.out.println("FOPA+IAA结束，耗时：" + (runTime[i][2][j] / 1000));

                System.out.println("FOPA+IGSA启动");
                start = System.currentTimeMillis();
                res[i][3][j] = Algorithm.FOPA_IGSA();
                end = System.currentTimeMillis();
                runTime[i][3][j] = end - start;
                System.out.println("FOPA+IGSA结束，耗时：" + (runTime[i][3][j] / 1000));

                System.out.println("MC+IAA启动");
                start = System.currentTimeMillis();
                res[i][4][j] = Algorithm.MC_IAA();
                end = System.currentTimeMillis();
                runTime[i][4][j] = end - start;
                System.out.println("MC+IAA结束，耗时：" + (runTime[i][4][j] / 1000));

                System.out.println("MC+IGSA启动");
                start = System.currentTimeMillis();
                res[i][5][j] = Algorithm.MC_IGSA();
                end = System.currentTimeMillis();
                runTime[i][5][j] = end - start;
                System.out.println("MC+IGSA结束，耗时：" + (runTime[i][5][j] / 1000));
            }
            System.out.println("第" + (i + 1) + "次已完成，剩余次数：" + (t - i - 1));
        }


        FileWriter fw = new FileWriter("C:\\Users\\HP\\Desktop\\data\\extend\\"+type+".txt");
        BufferedWriter bf = new BufferedWriter(fw);
        FileWriter fw1 = new FileWriter("C:\\Users\\HP\\Desktop\\data\\extend\\runTime_"+ type+".txt");
        BufferedWriter bf1 = new BufferedWriter(fw1);

        //记录当B = SList[k]时，6个算法的各自平均值
        for(int k = 0; k < SList.length; k++){
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
                System.out.println(type + " = " + SList[k]);
                System.out.println("SSA_RSA：" + ans[0]);
                System.out.println("RS_RR：" + ans[1]);
                System.out.println("FOPA_IAA：" + ans[2]);
                System.out.println("FOPA_IGSA：" + ans[3]);
                System.out.println("MC_IAA：" + ans[4]);
                System.out.println("MC_IGSA：" + ans[5]);
            }
            //运行时间
            {
                System.out.println(type + " = " + SList[k]);
                System.out.println("runTime_SSA_RSA：" + timeList[0]);
                System.out.println("runTime_RS_RR：" + timeList[1]);
                System.out.println("runTime_FOPA_IAA：" + timeList[2]);
                System.out.println("runTime_FOPA_IGSA：" + timeList[3]);
                System.out.println("runTime_MC_IAA：" + timeList[4]);
                System.out.println("runTime_MC_IGSA：" + timeList[5]);
            }

            //数据输出到文件
            {
                bf.write(type + " = " + SList[k]);
                bf.newLine();
                bf.write("SSA_RSA：" + ans[0]);
                bf.newLine();
                bf.write("RS_RR：" + ans[1]);
                bf.newLine();
                bf.write("FOPA_IAA：" + ans[2]);
                bf.newLine();
                bf.write("FOPA_IGSA：" + ans[3]);
                bf.newLine();
                bf.write("MC_IAA：" + ans[4]);
                bf.newLine();
                bf.write("MC_IGSA：" + ans[5]);
                if(k != SList.length - 1)
                    bf.newLine();
            }
            //运行时间
            {
                bf1.write(type + " = " + SList[k]);
                bf1.newLine();
                bf1.write("runTime_SSA_RSA：" + timeList[0]);
                bf1.newLine();
                bf1.write("runTime_RS_RR：" + timeList[1]);
                bf1.newLine();
                bf1.write("runTime_FOPA_IAA：" + timeList[2]);
                bf1.newLine();
                bf1.write("runTime_FOPA_IGSA：" + timeList[3]);
                bf1.newLine();
                bf1.write("runTime_MC_IAA：" + timeList[4]);
                bf1.newLine();
                bf1.write("runTime_MC_IGSA：" + timeList[5]);
                if(k != SList.length - 1)
                    bf1.newLine();
            }
        }

        bf.close();
        bf1.close();
    }

}
