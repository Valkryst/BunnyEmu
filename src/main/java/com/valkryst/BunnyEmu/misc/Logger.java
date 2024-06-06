package com.valkryst.BunnyEmu.misc;

public class Logger {
    public static final int LOG_TYPE_VERBOSE = 0;
    public static final int LOG_TYPE_INFO = 1;
    public static final int LOG_TYPE_WARNING = 2;
    public static final int LOG_TYPE_ERROR = 3;

    public static void writeLog(final String message, final int logType) {
        System.out.println(message);
    }
}
