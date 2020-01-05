package mempress;


import mempress.serialization.Serializer;
import mempress.serialization.ZipFileSerializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bartek on 2014-12-18.
 */
public class ZipFileSerializerTest {

    private static final String ORIGINAL_TEXT = "The path of the righteous man is beset on all sides by the inequities "
            + "of the selfish and the tyranny of evil men. Blessed is he who, in the "
            + "name of charity and good will, shepherds the weak through the valley of"
            + " the darkness, for he is truly his brother's keeper and the finder of "
            + "lost children. And I will strike down upon thee with great vengeance and"
            + " furious anger those who attempt to poison and destroy My brothers. And you"
            + " will know I am the Lord when I lay My vengeance upon you.";

    @Test
    public void testSerAndDes() {
        Serializer serializer = new ZipFileSerializer();
        String deserializedText = (String) serializer.des(serializer.ser(ORIGINAL_TEXT));

        assertEquals(ORIGINAL_TEXT, deserializedText);
    }
}
