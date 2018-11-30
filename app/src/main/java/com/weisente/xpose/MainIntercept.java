package com.weisente.xpose;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainIntercept implements IXposedHookLoadPackage {

    private  final String TAG = this.getClass().getName();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(lpparam.packageName.equals("com.tencent.mm")){
            Log.d(TAG,"拦截 packageName: " + lpparam.packageName);
        }
    }
}
