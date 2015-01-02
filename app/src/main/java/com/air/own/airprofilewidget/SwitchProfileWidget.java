package com.air.own.airprofilewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Vibrator;
import android.widget.RemoteViews;

/**
 * version 1.0
 * can change RingerMode by click widget
 * version 1.1
 * when user change status to vibrate,then the widget is vibrating
 * version 2.0
 * receiver user change volume and change the status of widget
 * Created by Air on 2014/12/28.
 */
public class SwitchProfileWidget extends AppWidgetProvider {
    private final static String BROAD = "com.air.changeProfile";

    public static final String RING = "正常";
    public static final String VIBRATE = "震动";
    public static final String SILENT = "静音";
//    static enum AUDIO_MODE {SILENT, VIBRATE, RING }



    /**
     * called when the AppWidget is deleted
     * */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds){
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * called when the last AppWidget is deleted
     * */
    @Override
    public void onDisabled(Context context){
        super.onDisabled(context);
    }

    /**
     * called when the first AppWidget is created
     * */
    @Override
    public void onEnabled(Context context){
        super.onEnabled(context);
    }

    /**
     * received the broadcast
     * */
    @Override
    public void onReceive(Context context, Intent intent) {
        if(context == null || intent == null )
            return;
        String action = intent.getAction();
        //get All appwidget created by this app
        ComponentName componentName = new ComponentName(context,
                SwitchProfileWidget.class);
        //get AppWidgetManager instance，用于管理appwidget以便进行更新操作
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (action.equals(BROAD)){
            //update appwidget
            appWidgetManager.updateAppWidget(componentName, clickWidget(context));
        }else if(action.equals("android.media.VOLUME_CHANGED_ACTION")){
            appWidgetManager.updateAppWidget(componentName, addToDesktop(context));
        }
        super.onReceive(context, intent);
    }

    /**
     * called when the AppWidget is added to desktop or the time AppWidget update
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            //create a Intent Object with an action and extra data
            Intent intent = new Intent(BROAD);

            //setting the action of pendingIntent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            //只能通过远程对象来设置appwidget中的控件状态
            RemoteViews remoteViews = addToDesktop(context);
            //bind the event to remoteViews
            remoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
            //update Appwidget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private AudioManager getAudioManager(Context context){
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    //get default ringer mode
    private int getCurrentRingMode(AudioManager audio) {
        return audio.getRingerMode();
    }

    //set ringer mode
    private void setRing(AudioManager audioManager) {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    private void setVibrate(AudioManager audioManager) {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    private void setSilent(AudioManager audioManager) {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    private RemoteViews clickWidget(Context context){
        RemoteViews remoteViews  = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        AudioManager audioManager = getAudioManager(context);
        switch (getCurrentRingMode(audioManager)){
            case AudioManager.RINGER_MODE_SILENT :
                setRing(audioManager);
                setImageAndText(remoteViews, R.drawable.profile_normal, RING);
                break;
            case AudioManager.RINGER_MODE_VIBRATE :
                setSilent(audioManager);
                setImageAndText(remoteViews, R.drawable.profile_silent, SILENT);
                break;
            case AudioManager.RINGER_MODE_NORMAL :
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(160);
                setVibrate(audioManager);
                setImageAndText(remoteViews, R.drawable.profile_vibrate, VIBRATE);
                break;
            default:
                break;
        }
        return remoteViews;
    }

    private RemoteViews addToDesktop(Context context){
        RemoteViews remoteViews  = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        AudioManager audioManager = getAudioManager(context);

        switch (getCurrentRingMode(audioManager)){
            case AudioManager.RINGER_MODE_NORMAL :
                setImageAndText(remoteViews, R.drawable.profile_normal, RING);
                break;
            case AudioManager.RINGER_MODE_SILENT :
                setImageAndText(remoteViews, R.drawable.profile_silent, SILENT);
                break;
            case AudioManager.RINGER_MODE_VIBRATE :
                setImageAndText(remoteViews, R.drawable.profile_vibrate, VIBRATE);
                break;
            default:
                break;
        }

        return remoteViews;
    }

    private void setImageAndText(RemoteViews remoteViews, int resId, String text){
        remoteViews.setImageViewResource(R.id.widget_icon, resId);
        remoteViews.setTextViewText(R.id.widget_label, text);
    }

}
