package com.lkl.android.lint.plugin;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO Utils  Apache
 *
 * Created by chentong on 29/12/15.
 */
public class IOUtils {
    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null) {
            try {
                for (Closeable closeable : closeables) {
                    closeable.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L ? -1 : (int) count;
    }

    private static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;

        int n1;
        for (boolean n = false; -1 != (n1 = input.read(buffer)); count += (long) n1) {
            output.write(buffer, 0, n1);
        }

        return count;
    }
}
