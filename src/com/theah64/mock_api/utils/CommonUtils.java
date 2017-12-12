package com.theah64.mock_api.utils;

import java.io.File;

/**
 * Created by theapache64 on 12/12/17.
 */
public class CommonUtils {
    public static File getDataDir() {
        final File dataDir = new File("/var/www/html/mock_api_data");

        if (!dataDir.exists()) {
            dataDir.mkdirs();
            dataDir.setReadable(true, false);
            dataDir.setExecutable(true, false);
            dataDir.setWritable(true, false);
        }
        return dataDir;
    }
}
