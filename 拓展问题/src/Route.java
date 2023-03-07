import org.w3c.dom.ranges.Range;

import java.util.Random;

/*
* 路径用三个点表示，行驶轨迹为：node1->node2->node3->node1
* */
public class Route{
    //路径最远处到起点的距离，可看作是车库的覆盖半径
    public static int Range = EntityFactory.size / 3;
    public static class Node extends Entity{}
    public static Random random = new Random();

    //四个驻留点
    public Node node1 = new Node();
    public Node node2 = new Node();
    public Node node3 = new Node();
    public Node node4 = new Node();

    //当前路径的长度
    public double length = 0;
    //该路径归属哪个车库
    public Garage g;
    //当前路径上充电器的移动速度
    public double v;

    public Route(Garage g){
        this.g = g;
        node1.x = g.x;
        node1.y = g.y;

        node2.x = g.x + Math.pow(-1, random.nextInt(2)) * (10 + random.nextInt(Range - 10));
        node2.y = g.y + Math.pow(-1, random.nextInt(2)) * (10 + random.nextInt(Range - 10));

        node3.x = g.x + Math.pow(-1, random.nextInt(2)) * (10 + random.nextInt(Range - 10));
        node3.y = g.y + Math.pow(-1, random.nextInt(2)) * (10 + random.nextInt(Range - 10));

        node4.x = g.x - Range + random.nextInt( 2 * Range);
        node4.y = g.y - Range + random.nextInt(2 * Range);

        length += Util.distance(node1, node2);
        length += Util.distance(node2, node3);
        length += Util.distance(node3, node4);
        length += Util.distance(node4, node1);

        this.v = Charger.P * length / (Charger.B - Charger.eta * length);
    }

    //通过车库和额外三个驻留点，手动生成一个路径
    public Route(Garage g, double x1, double y1, double x2, double y2, double x3, double y3){
        this.g = g;
        node1.x = g.x;
        node1.y = g.y;

        node2.x = x1;
        node2.y = y1;

        node3.x = x2;
        node3.y = y2;

        node4.x = x3;
        node4.y = y3;

        length += Util.distance(node1, node2);
        length += Util.distance(node2, node3);
        length += Util.distance(node3, node4);
        length += Util.distance(node4, node1);

        this.v = Charger.P * length / (Charger.B - Charger.eta * length);
        if(this.v < 0){
            this.v = 0;
        }
    }

    @Override
    public String toString() {
        return    node1.x + "," + node1.y + ";"
                + node2.x + "," + node2.y + ";"
                + node3.x + "," + node3.y + ";"
                + node4.x + "," + node4.y + ";"
                + g.x + "," + g.y;

    }
}
