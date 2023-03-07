import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        Algorithm.init(120, 50, 400);
        //System.out.println(Algorithm.SSA_CSA_V2());
        System.out.println(Algorithm.SSA_CSA());
        System.out.println("==================");
        double rr = Algorithm.RS_RC();
        double mp = Algorithm.MC_PC();
        double ma = Algorithm.MC_AA();
        double fa = Algorithm.FOPA_AA();
        double fp = Algorithm.FOPA_PC();
        System.out.println("rr: " + rr);
        System.out.println("mp: " + mp);
        System.out.println("ma: " + ma);
        System.out.println("fa: " + fa);
        System.out.println("fp: " + fp);
        System.out.println("充电算法不同，带来差距：");
        System.out.println(mp - ma);
        System.out.println(fp - fa);
        System.out.println("感知算法不同，带来差距：");
        System.out.println(fa - ma);
        System.out.println(fp - mp);
    }

    @org.junit.Test
    public void test(){
        Sensor s = new Sensor(0, 0);
        PoI p1 = new PoI(0, 2);
        p1.wk = 1;
        PoI p2 = new PoI(0.5, 2);
        p2.wk = 1;

        System.out.println(Util.Quality(s, p1, 0));
        System.out.println(Util.Quality(s, p2, 0));
        System.out.println(Math.acos(2 / Math.sqrt(4.25))  * 360 / 2 / Math.PI);
    }

    @org.junit.Test
    public void testParam(){
        Charger.alpha = 105;
        Charger.D = 20;

        Charger c = new Charger(0, 0);
        Sensor s1 = new Sensor(0, Charger.D);
        PoI p1 = new PoI(0, 7);
        p1.wk = 1;

        double power = s1.power(c);
        double quality = Util.Quality(s1, p1, 0);

        System.out.println(p1.Ur);
        System.out.println(s1.Er);
        System.out.println("quality: " + quality);
        System.out.println("power: " + power);
    }

    @org.junit.Test
    public void input() throws IOException {
        FileWriter fw = new FileWriter("C:\\Users\\HP\\Desktop\\data\\B.txt");
        BufferedWriter bf = new BufferedWriter(fw);
        bf.write("111");
        bf.newLine();
        bf.write("222");
        bf.close();
    }

}
