import java.util.*;

public class Garage extends Entity{
    int ChargerNumber;
    int RouteNumber;
    List<Route> RouteList;

    //路径随机生成
    public Garage(double x, double y, int RouteNumber, int ChargerNumber){
        this.x = x;
        this.y = y;
        this.RouteNumber = RouteNumber;
        this.ChargerNumber = ChargerNumber;
        RouteList = EntityFactory.generateRoute(RouteNumber, this);
    }

    //路径手动传入
    public Garage(double x, double y, int ChargerNumber, List<Route> RouteList){
        this.x = x;
        this.y = y;
        this.RouteNumber = RouteList.size();
        this.ChargerNumber = ChargerNumber;
        this.RouteList = RouteList;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ");";
    }
}
