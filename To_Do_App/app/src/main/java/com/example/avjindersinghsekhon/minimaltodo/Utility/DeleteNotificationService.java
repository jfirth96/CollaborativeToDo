package com.example.avjindersinghsekhon.minimaltodo.Utility;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.avjindersinghsekhon.minimaltodo.Main.MainFragment;

import java.util.ArrayList;
import java.util.UUID;

public class DeleteNotificationService extends IntentService {
    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;

    /*
     * Tamara Charchoghlyan
     * Board ID for the specific ToDoItem
     * TODO: change boardID type to be int (unique ID from DB)
     */
    private UUID boardID;

    public DeleteNotificationService() {
        super("DeleteNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        storeRetrieveData = new StoreRetrieveData(this, MainFragment.FILENAME);
        UUID todoID = (UUID) intent.getSerializableExtra(TodoNotificationService.TODOUUID);
        boardID = (UUID) intent.getSerializableExtra(TodoNotificationService.BOARDUUID);

        mToDoItems = loadData();
        if (mToDoItems != null) {
            for (ToDoItem item : mToDoItems) {
                if (item.getIdentifier().equals(todoID)) {
                    mItem = item;
                    break;
                }
            }

            if (mItem != null) {
                mToDoItems.remove(mItem);
                dataChanged();
                saveData();
            }
        }
    }

    private void dataChanged() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true);
        editor.apply();
    }

    private void saveData() {
        try {
            storeRetrieveData.saveToDoItemsToFile(mToDoItems, boardID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
    }

    private ArrayList<ToDoItem> loadData() {
        try {
            return storeRetrieveData.loadToDoItemsFromFile(boardID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
