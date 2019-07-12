package com.tempegembus.zakaria.tempegembus;

import android.app.Activity;

import com.tempegembus.zakaria.tempegembus.Fragments.ProfileFragment;

/**
 * Created by putuguna on 26/10/16.
 */

public class ForceCloseDebugger {

    public static void handle(MainActivity context){
        if (!BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug")) {
            Thread.setDefaultUncaughtExceptionHandler(new ForceCloseException(context));
            String errorCaused  = context.getIntent().getStringExtra("bugs");
            System.out.println("FORCE CLOSE CAUSED BY : " + errorCaused);
        }else{
            Thread.setDefaultUncaughtExceptionHandler(new ForceCloseException(context));
            String errorCaused  = context.getIntent().getStringExtra("bugs");
            System.out.println("FORCE CLOSE CAUSED BY  : " + errorCaused);
        }
    }
}