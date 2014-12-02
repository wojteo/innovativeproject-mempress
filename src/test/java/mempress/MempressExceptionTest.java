package mempress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Bartek on 2014-12-01.
 */
public class MempressExceptionTest {
    private MempressException mempressException;

    @Before
    public void initTest() {

    }

    @Test
    public void testConstructor() {
        new MempressException();
    }

    @Test
    public void testConstructorMessage() {
        String message = "You do it wrong!";
        mempressException = new MempressException(message);
        Assert.assertEquals(message, mempressException.getMessage());
    }

}
