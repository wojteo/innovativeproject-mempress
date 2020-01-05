package mempress.utils;

public class IOUtils {
    public static void closeQuietly(AutoCloseable... acArray) {
        for (AutoCloseable closeable : acArray) {
            closeQuietly(closeable);
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ignored) {
        }
    }
}
