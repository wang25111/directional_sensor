import org.junit.Test;

public class MyTest {

    @Test
    public void test(){
        Sensor s1 = new Sensor(0, 0);
        Charger c1 = new Charger(0, 10);
        System.out.println(s1.power(c1));
    }
}
