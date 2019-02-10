package com.weisente.xpose;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by wl on 2019/1/17.
 *
 * 动态加载插件包，也可以把自己当作插件包，这样就不必每次重启手机（只需重启一次），如果插件测试稳定后，可以直接更改xposed_init的指向即可。
 */
public class DynamicHookApp implements IXposedHookLoadPackage, IXposedHookInitPackageResources {

    /**
     * 平时需要hook的包，如：抖音，百度
     */
    private static List<String> hookAppPackages = new ArrayList<>();

    static {
        hookAppPackages.add("com.wingsofts.zoomimageheader");
        hookAppPackages.add("xxx.xxx.xxx");
        hookAppPackages.add("xxx.xxx.xxx");
        hookAppPackages.add("xxx.xxx.xxx");
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (hookAppPackages.contains(loadPackageParam.packageName) || allOpen()) {
            // 将loadPackageParam的classloader替换为被hook程序Application的classloader,解决hook程序存在多个.dex文件时,有时候ClassNotFound的问题
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    // 注意：context.getClassLoader(); loadPackageParam.classLoader, 插件plugin中的classLoader都是同一个对象.且是当前被hook对象的PathClassLoader
                    Context context = (Context) param.args[0];
                    File apkFile = ApkFindHelper.findApkFile(context, Const.modulePackage);
                    if (apkFile == null) {
                        XLogHelper.d(loadPackageParam.packageName + " 寻找插件apk失败");
                    }

                    // 加载指定的hook逻辑处理类，并调用它的 handleLoadPackage方法
                    PathClassLoader pathClassLoader = new PathClassLoader(apkFile.getAbsolutePath(), ClassLoader.getSystemClassLoader());
                    Class<?> cls = Class.forName(Const.dynamicHookClass, true, pathClassLoader);
                    Object instance = cls.newInstance();
                    // 插件的方法名和我们平时单独写的一样
                    // hook 代码
                    Method method1 = cls.getDeclaredMethod("handleLoadPackage", XC_LoadPackage.LoadPackageParam.class);
                    method1.invoke(instance, loadPackageParam);
                }
            });
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                File apkFile = ApkFindHelper.findApkFile(context, Const.modulePackage);
                if (apkFile == null) {
                    XLogHelper.d(resparam.packageName + " 寻找插件apk失败");
                }

                PathClassLoader pathClassLoader = new PathClassLoader(apkFile.getAbsolutePath(), ClassLoader.getSystemClassLoader());
                Class<?> cls = Class.forName(Const.dynamicHookClass, true, pathClassLoader);
                Object instance = cls.newInstance();
                // hook 资源
                Method method = cls.getDeclaredMethod("handleInitPackageResources", XC_InitPackageResources.InitPackageResourcesParam.class);
                method.invoke(instance, resparam);
            }
        });
    }

    /**
     * 默认开启hook所有的App
     */
    private boolean allOpen() {
        return true;
    }

}
