package com.weisente.xpose;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

/**
 * Created by wl on 2019/1/23.
 */
public class ApkFindHelper {

    /**
     * 根据包名构建目标Context,并调用getPackageCodePath()来定位apk
     *
     * @param context       context参数
     * @param modulePackage 当前模块包名
     * @return return apk file
     */
    public static File findApkFile(Context context, String modulePackage) {
        if (context == null) {
            return null;
        }
        try {
            Context moudleContext = context.createPackageContext(modulePackage, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            String apkPath = moudleContext.getPackageCodePath();
            return new File(apkPath);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
