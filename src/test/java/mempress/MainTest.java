package mempress;

import static org.junit.Assert.*;

import org.junit.Test;

public class MainTest {

	@Test
	public void testTukTuk() {
		assertEquals(Main.tukTuk(), "I'm rickshaw");
	}

}
