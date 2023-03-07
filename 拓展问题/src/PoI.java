import java.util.Random;

/**
 * @author cj
 * @create 2022-10-17 11:05
 */
public class PoI extends Entity{
    static Random random = new Random();
    //权重上限
    static  int wkUp = 3;
    static double Uk_up = 14;
    //剩余的效用，取值范围
    public double Ur = 1 + random.nextDouble() * Uk_up;
    //兴趣点的权重，取值范围[1,5]
    public double wk = random.nextInt(wkUp) + 1;

    public PoI(double x, double y){
        this.x = x;
        this.y = y;
    }

    public PoI(double x, double y, double Ur){
        this.x = x;
        this.y = y;
        this.Ur = Ur;
    }

    public String toString() {
        return x + "," + y +"," + Ur;
    }
}
