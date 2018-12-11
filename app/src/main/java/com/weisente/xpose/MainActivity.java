package com.weisente.xpose;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private Messenger mService;
    private Messenger mReplyMessenger;
    private Bitmap mDefaultIcon;
    private Bundle data = new Bundle();
    private Message msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button wx_button = (Button) findViewById(R.id.wx_bt);
        Button qq_button = (Button) findViewById(R.id.qq_bt);

        wx_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQrData();
                msg = Message.obtain(null,Constant.WE_GENERATE_QR);
                msg.setData(data);
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        qq_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQrData();
            }
        });


        Intent service = new Intent();
        service.setClassName(this, "com.weisente.xpose.CommandService");
        bindService(service, mConnection, BIND_AUTO_CREATE);

        mDefaultIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_head);
        mReplyMessenger = new Messenger(new ClientMessengerHandler(this));

    }

    public void initQrData(){
        data.putString(Constant.QR_DES,"test");
        data.putFloat(Constant.QR_MONEY, 22.00f);
        data.putParcelable(Constant.QR_ICON,mDefaultIcon);
    }




    private static class ClientMessengerHandler extends Handler{

        private WeakReference<MainActivity> mActivity;

        ClientMessengerHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }


    }




    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message msg = Message.obtain(null, Constant.TEST_JOIN);
            try {
                msg.replyTo = mReplyMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
