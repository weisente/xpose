package com.weisente.xpose;

import de.robv.android.xposed.XposedBridge;

public class XLogHelper {

    private static final boolean debug = true;
    private static final String prefix = "=========================================";
    private static final String suffix = "=========================================";

    public static void d(String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append("\r\n");
        builder.append(prefix);
        builder.append("\r\n");
        builder.append(msg);
        builder.append("\r\n");
        builder.append(suffix);

        if (debug) {
            XposedBridge.log(builder.toString());
        }
    }
}