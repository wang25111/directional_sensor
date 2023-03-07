import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author cj
 * 批处理数据
 */

public class TestUP {
    //迭代次数
    static int t = 100;
    public static void main(String[] args) throws IOException {
        //参数变化值列表
        int addNum = 200;
        int[] SList = {400, 600, 800, 1000, 1200, 1400};
        //存放所有结果
        double[][][] res = new double[t][2][SList.length];
        long[][][] runTime = new long[t][2][SList.length];
        for(int i = 0; i < t; i++){
            //初始化网络
            Algorithm.init(300, 200, 6);
            for(int j = 0; j < SList.length; j++){
                long start = 0;
                long end = 0;
                Algorithm.add(0, addNum);
                System.out.println("L = " + SList[j] + "-----------------------");
                System.out.println("SSA+RSA启动");
                start = System.currentTimeMillis();
                res[i][0][j] = Algorithm.SSA_RSA();
                end = System.currentTimeMillis();
                runTime[i][0][j] = end - start;
                System.out.println("SSA+RSA结束，耗时：" + (runTime[i][0][j] / 1000));

                res[i][1][j] = Algorithm.getUP();
            }
            System.out.println("第" + (i + 1) + "次已完成，剩余次数：" + (t - i - 1));
        }

        double[][] ans = new double[SList.length][2];
        for(int i = 0; i < SList.length; i++){
            System.out.println("L = " + SList[i] + ":");
            for(int j = 0; j < 2; j++){
                for(int k = 0; k < t; k++){
                    ans[i][j] += res[k][j][i];
                }
                System.out.println(ans[i][j] / t);
            }
        }

    }

}
