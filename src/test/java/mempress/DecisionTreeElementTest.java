package mempress;

import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bartek on 2014-11-21.
 */
public class DecisionTreeElementTest {

    @Test
    public void castTest() {
        List<A> list = new SmartList<>();
        list.add(new A());
        list.add(new B());
        list.add(new C());

        int[] expectedValues = { 1, 2, 3};
        for(int i = 0; i < expectedValues.length; ++i) {
            Assert.assertEquals(list.get(i).getNum(), expectedValues[i]);
        }

    }


    private static class A implements Serializable {
        public int getNum() { return 1; }
    }

    private static class B extends A {
        @Override
        public int getNum() { return 2; }
    }

    private static class C extends A {
        @Override
        public int getNum() { return 3; }
    }
}
