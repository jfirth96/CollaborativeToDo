package com.example.avjindersinghsekhon.minimaltodo.Utility;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

public class StoreRetrieveData {
    private Context mContext;
    private String mFileName;

    public StoreRetrieveData(Context context, String filename) {
        mContext = context;
        mFileName = filename;
    }


    /*
    * Tamara Charchoghlyan
    * Accepts a generic type that implements JSONSerializable interface
    *   i.e. TdDoItem or BoardItem
    */
    public static  <T extends JSONSerializable> JSONArray toJSONArray(ArrayList<T> items) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (T item : items) {
            JSONObject jsonObject = item.toJSON();
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    /*
     * Tamara Charchoghlyan
     * Saves array list of BoardItem to file
     * TODO: change to make API call and store data in DB
     */
    public void saveToFile(ArrayList<BoardItem> items) throws JSONException, IOException {
        FileOutputStream fileOutputStream;
        OutputStreamWriter outputStreamWriter;
        fileOutputStream = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
        outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(toJSONArray(items).toString());
        outputStreamWriter.close();
        fileOutputStream.close();
        Log.d( "DEBUGGING", toJSONArray(items).toString() );
    }

    /*
     * Tamara Charchoghlyan
     * Load all boards from file. Return array list of BoardItem
     * TODO: change to make API call and retrieve data from DB
     */
    public ArrayList<BoardItem> loadFromFile() throws IOException, JSONException {
        ArrayList<BoardItem> items = new ArrayList<>();
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = mContext.openFileInput(mFileName);
            StringBuilder builder = new StringBuilder();
            String line;
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }

            JSONArray jsonArray = (JSONArray) new JSONTokener(builder.toString()).nextValue();

            for (int i = 0; i < jsonArray.length(); i++) {
                BoardItem item = new BoardItem(jsonArray.getJSONObject(i));
                items.add(item);
            }

        } catch (FileNotFoundException fnfe) {
            //do nothing about it
            //file won't exist first time app is run
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }

        }
        return items;
    }

    /*
     * Tamara Charchoghlyan
     * Load all ToDos for a specific board from file. Return array list of ToDoItem
     * TODO: change to make API call and retrieve data from DB
     * TODO: change boardID type to be int (unique ID from DB)
     */
    public ArrayList<ToDoItem> loadToDoItemsFromFile(UUID boardID) throws IOException, JSONException {
        ArrayList<BoardItem> boards = loadFromFile();
        ArrayList<ToDoItem> todoItems = null;

        for(BoardItem b: boards) {
            if (b.getBoardIdentifier().equals(boardID)) {
                todoItems = b.getToDoItems();
                return todoItems;
            }

        }

        return todoItems;
    }

    /*
     * Tamara Charchoghlyan
     * Save all ToDos for a specific board to file
     * TODO: change to make API call and store data in DB
     * TODO: change boardID type to be int (unique ID from DB)
     */
    public void saveToDoItemsToFile(ArrayList<ToDoItem> items, UUID boardID) throws JSONException, IOException {
        ArrayList<BoardItem> boards = loadFromFile();

        for(BoardItem b: boards) {
            if (b.getBoardIdentifier().equals(boardID)) {
                b.setToDoItems(items);

                saveToFile(boards);
                return;
            }
        }
    }

}
