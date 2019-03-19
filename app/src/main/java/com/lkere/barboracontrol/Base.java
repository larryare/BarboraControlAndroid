package com.lkere.barboracontrol;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;

public class Base extends Application {
    public static final String CHANNEL_1_ID = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels() {
        NotificationChannel channel1 = new NotificationChannel(
                CHANNEL_1_ID,
                "Channel 1",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel1.enableLights(true);
        channel1.setLightColor(Color.GREEN);
        channel1.enableVibration(true);
        channel1.setDescription("Appears when bulbs turn on or off");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel1);

    }

}
