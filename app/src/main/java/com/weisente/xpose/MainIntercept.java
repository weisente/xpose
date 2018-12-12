package com.weisente.xpose;

import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainIntercept implements IXposedHookLoadPackage {

    private  final String TAG = this.getClass().getName();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //确定是什么包发出的请求
        if(lpparam.packageName.equals("com.tencent.mobileqq")){
            Log.d(TAG,"拦截 packageName: " + lpparam.packageName);
//            hookcContext(lpparam.classLoader);
//            hookstep(lpparam.classLoader);
        }
    }
    //修改qq的步数
    private void hookstep(ClassLoader loader) {
        try {
            //4.4 nexus 通过
            Class clazz = XposedHelpers.findClass("android.hardware.SystemSensorManager$SensorEventQueue", loader);
            XposedBridge.hookAllMethods(clazz, "dispatchSensorEvent", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    ((float[]) param.args[1])[0] = (((float[]) param.args[1])[0]) + 1234;
                    Log.e(TAG,"asdasd"+((float[]) param.args[1])[0]);


                }

            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }


    }


    private void hookcContext(ClassLoader loader){

    }
}
