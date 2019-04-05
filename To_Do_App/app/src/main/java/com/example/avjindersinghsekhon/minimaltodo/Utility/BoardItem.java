package com.example.avjindersinghsekhon.minimaltodo.Utility;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class BoardItem implements Serializable, JSONSerializable {
    private String boardTitle;
    private Date dateCreated;
    private int boardColor;

    /*
     * Tamara Charchoghlyan
     * TODO: change boardIdentifier type to be int (unique ID from DB)
     */
    private UUID boardIdentifier;

    private boolean boardCompleted;
    private int boardOwnerID;
    private String boardOwnerUser;
    private ArrayList<ToDoItem> toDoItems;

    // description for JSON object
    private static final String BOARDTITLE = "boardtitle";
    private static final String BOARDIDENTIFIER = "boardidentifier";
    private static final String BOARDCOLOR = "boardcolor";
    private static final String BOARDCOMPLETED = "boardcompleted";
    private static final String BOARDOWNERID = "boardownerid";
    private static final String BOARDOWNERUSER = "boardowneruser";
    private static final String DATECREATED = "dateCreated";
    private static final String TODOITEMS = "todoitems";

    // TODO: pass in owner user and ID once account creation is a feature
    public BoardItem(String title) {
        boardTitle = title;
        dateCreated = new Date();
        boardColor = ColorGenerator.MATERIAL.getRandomColor();
        boardIdentifier = UUID.randomUUID();
        boardCompleted = false;
        boardOwnerID = 1;
        boardOwnerUser = "Current User";
        toDoItems = new ArrayList<>();
    }

    public BoardItem(JSONObject jsonObject) throws JSONException {
        boardTitle = jsonObject.getString( BOARDTITLE );
        dateCreated = new Date( jsonObject.getLong(DATECREATED) );
        boardColor = jsonObject.getInt( BOARDCOLOR );
        boardIdentifier = UUID.fromString( jsonObject.getString(BOARDIDENTIFIER) );
        boardCompleted = jsonObject.getBoolean( BOARDCOMPLETED );
        boardOwnerUser = jsonObject.getString( BOARDTITLE );
        boardOwnerID = jsonObject.getInt( BOARDOWNERID );
        toDoItems = new ArrayList<>();

        // Get the list of ToDos as JSONArray and parse to array list of ToDoItems
        JSONArray toDoItemsJsonArray = jsonObject.getJSONArray( TODOITEMS );
        if (toDoItemsJsonArray != null) {
            for (int i = 0; i < toDoItemsJsonArray.length(); i++) {
                ToDoItem item = new ToDoItem(toDoItemsJsonArray.getJSONObject(i));
                addToDoItem(item);
            }
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put( BOARDTITLE, boardTitle );
        jsonObject.put( DATECREATED, dateCreated.getTime() );
        jsonObject.put( BOARDCOLOR, boardColor );
        jsonObject.put( BOARDIDENTIFIER, boardIdentifier.toString() );
        jsonObject.put( BOARDCOMPLETED, boardCompleted );
        jsonObject.put( BOARDOWNERID, boardOwnerID );
        jsonObject.put( BOARDOWNERUSER, boardOwnerUser );
        jsonObject.put( TODOITEMS, StoreRetrieveData.toJSONArray(toDoItems) );

        return jsonObject;
    }

    /*********** Getters and Setters ***********/

    //board title
    public String getBoardTitle() { return boardTitle; }

    public void setBoardTitle(String boardTitle) { this.boardTitle = boardTitle; }

    // date created
    public Date getDateCreated() { return dateCreated; }

    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    // board color
    public int getBoardColor() { return boardColor; }

    public void setBoardColor(int boardColor) { this.boardColor = boardColor; }

    // board identifier
    public UUID getBoardIdentifier() { return boardIdentifier; }

    public void setBoardIdentifier(UUID boardIdentifier) { this.boardIdentifier = boardIdentifier; }

    // board completed
    public boolean getBoardCompleted(){ return boardCompleted; }

    public void setBoardCompleted( boolean completed ) { this.boardCompleted = completed; }

    // board owner ID
    public int getBoardOwnerID() { return boardOwnerID; }

    public void setBoardOwnerID(int boardOwnerID) { this.boardOwnerID = boardOwnerID; }

    // board owner user
    public String getBoardOwnerUser() { return boardOwnerUser; }

    public void setBoardOwnerUser(String boardOwnerUser) { this.boardOwnerUser = boardOwnerUser; }

    // todoitems
    public ArrayList<ToDoItem> getToDoItems() { return toDoItems; }

    public void setToDoItems(ArrayList<ToDoItem> toDoItems) { this.toDoItems = toDoItems; }

    public void addToDoItem(ToDoItem toDoItem) { this.toDoItems.add(toDoItem); }
}
