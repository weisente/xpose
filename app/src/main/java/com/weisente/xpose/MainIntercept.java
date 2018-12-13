package com.weisente.xpose;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

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

//            hookwork(lpparam.classLoader);
            hookbitmap(lpparam.classLoader);
        }
    }

    private void hookbitmap(ClassLoader classLoader) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.biz.qrcode.util.QRUtils", classLoader);
        XposedHelpers.findAndHookMethod(clazz, "a", Context.class, Bitmap.class, Bitmap.class, int.class, String.class,
                Bitmap.class, Bitmap.class, int.class, int.class, String.class, Rect.class, ArrayList.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Bitmap result = (Bitmap)param.getResult();
                        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/xpcracker/test" );
                        Log.d(TAG,Environment.getExternalStorageDirectory().getAbsolutePath() + "/xpcracker/test");
                        if (!appDir.exists()) {
                            appDir.mkdirs();
                        }
                        File file = new File(appDir, "asd.jpeg");
                        FileOutputStream fos = new FileOutputStream(file);
                        result.compress(Bitmap.CompressFormat.JPEG, 40, fos);
                        fos.flush();
                        fos.close();
                        Log.d(TAG,"输出了");
                    }
                });
    }

    //修改发送的语言

    private void hookwork(ClassLoader classLoader) {

        Class<?> clazz = XposedHelpers.findClass("com.tencent.mobileqq.activity.BaseChatPie", classLoader);
        XposedHelpers.findAndHookMethod(clazz, "a", String.class, "com.tencent.mobileqq.activity.ChatActivityFacade$SendMsgParams", ArrayList.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = "asdasd";
                        Log.d(TAG,"改变信息");
                    }
                });

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
