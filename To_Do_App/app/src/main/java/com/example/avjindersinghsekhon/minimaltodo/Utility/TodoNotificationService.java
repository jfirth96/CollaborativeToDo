package com.example.avjindersinghsekhon.minimaltodo.Utility;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Reminder.ReminderActivity;

import java.util.UUID;

public class TodoNotificationService extends IntentService {
    public static final String TODOTEXT = "com.avjindersekhon.todonotificationservicetext";
    public static final String TODOUUID = "com.avjindersekhon.todonotificationserviceuuid";
    public static final String BOARDUUID = "com.avjindersekhon.todonotificationserviceboarduuid";
    private String mTodoText;
    private UUID mTodoUUID;
    private UUID mBoardUUID;

    public TodoNotificationService() {
        super("TodoNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mTodoText = intent.getStringExtra(TODOTEXT);
        mTodoUUID = (UUID) intent.getSerializableExtra(TODOUUID);
        mBoardUUID = (UUID) intent.getSerializableExtra(BOARDUUID);

        Log.d("OskarSchindler", "onHandleIntent called");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, ReminderActivity.class);
        i.putExtra(TODOUUID, mTodoUUID);
        i.putExtra(BOARDUUID, mBoardUUID);

        Intent deleteIntent = new Intent(this, DeleteNotificationService.class);
        deleteIntent.putExtra(TODOUUID, mTodoUUID);
        deleteIntent.putExtra(BOARDUUID, mBoardUUID);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(mTodoText)
                .setSmallIcon(R.drawable.ic_done_white_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDeleteIntent(PendingIntent.getService(this, mTodoUUID.hashCode(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentIntent(PendingIntent.getActivity(this, mTodoUUID.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        manager.notify(100, notification);
    }
}
