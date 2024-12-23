package com.child.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            Intent i = new Intent(context, BackgroundService.class);
            context.startService(i);
        }
        catch (Exception e){}
    }
}
