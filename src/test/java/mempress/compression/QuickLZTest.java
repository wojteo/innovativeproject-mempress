package mempress.compression;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class QuickLZTest {
    @Test
    public void decompressEqualsOriginalText() {
        String originalText = "Hello World!";
        byte[] decompressed = QuickLZ.decompress(QuickLZ.compress(originalText.getBytes(), 1));
        String decompressedString = new String(decompressed);
        assertEquals(decompressedString, originalText);
    }
}