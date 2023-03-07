import java.util.Random;

/**
 * @author cj
 * @create 2022-10-17 11:05
 */
public class PoI extends Entity{
    static Random random = new Random();
    //权重上限
    static  int wkUp = 3;
    //剩余效用上限
    static double UrUp = 14;

    //剩余的效用，取值范围
    public double Ur = 1 + random.nextDouble() * UrUp;
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
}
