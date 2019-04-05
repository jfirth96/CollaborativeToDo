package com.example.avjindersinghsekhon.minimaltodo.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class ToDoItem implements Serializable, JSONSerializable {

    private String mToDoText;
    private boolean mHasReminder;
    private String mToDoDescription;
    private int mTodoColor;
    private Date mToDoDate;
    private UUID mTodoIdentifier;
    private boolean mToDoCompleted; // added by Jackson Firth
    private String mToDoPriority; // Added by Sopulu
    private Date mToDoDueDate; // Added by Jackson - 5569
    private boolean mHasDueDate; // Added by Jackson - 5569

    private static final String TODODESCRIPTION = "tododescription";
    private static final String TODOTEXT = "todotext";
    private static final String TODOREMINDER = "todoreminder";
    private static final String TODOCOLOR = "todocolor";
    private static final String TODODATE = "tododate";
    private static final String TODOIDENTIFIER = "todoidentifier";
    private static final String TODOCOMPLETED = "todocompleted"; // added by Jackson Firth
    private static final String TODODUEDATE = "tododuedate"; // Added by Jackson Firth - 5569
    private static final String TODOPRIORITY = "todopriority"; // Added by Sopulu

    // added by Sopulu
    private static final String LOWPRIORITY = "Low";
//    private static final String MEDPRIORITY = "Medium";
//    private static final String HIGHPRIORITY = "High";

    public ToDoItem( String toDoBody, String toDoDescription,  boolean hasReminder, Date toDoDate ) {
        mToDoText = toDoBody;
        mHasReminder = hasReminder;
        mToDoDate = toDoDate;
        mToDoDescription = toDoDescription;
        mTodoColor = 1677725;
        mTodoIdentifier = UUID.randomUUID();
        mToDoCompleted = false; // added by Jackson Firth
        mToDoPriority = LOWPRIORITY; // added by Sopulu
        mToDoDueDate = null; // Added by Jackson Firth
        mHasDueDate = false; // Added by Jackson Firth
    }

    public ToDoItem( JSONObject jsonObject ) throws JSONException {
        mToDoText = jsonObject.getString( TODOTEXT );
        mToDoDescription = jsonObject.getString( TODODESCRIPTION );
        mHasReminder = jsonObject.getBoolean( TODOREMINDER );
        mTodoColor = jsonObject.getInt( TODOCOLOR );

        mTodoIdentifier = UUID.fromString( jsonObject.getString( TODOIDENTIFIER ) );

        if (jsonObject.has( TODODATE )) {
            mToDoDate = new Date( jsonObject.getLong( TODODATE ) );
        } else {
            mToDoDate = null;
        }

        mToDoCompleted = jsonObject.getBoolean( TODOCOMPLETED ); // added by Jackson Firth

        // Added by Jackson Firth - 5569
        if (jsonObject.has( TODODUEDATE )) {
            mToDoDueDate = new Date( jsonObject.getLong(( TODODUEDATE ) ) );
        } else {
            mToDoDueDate = null;
        }

        mToDoPriority = jsonObject.getString( TODOPRIORITY ); // Added by Sopulu

        //Log.d( "JSON_CONSTRUCTOR ", mToDoPriority );
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put( TODOTEXT, mToDoText );
        jsonObject.put( TODOREMINDER, mHasReminder );
        jsonObject.put( TODODESCRIPTION, mToDoDescription );

        if (mToDoDate != null) {
            jsonObject.put( TODODATE, mToDoDate.getTime() );
        }
        jsonObject.put( TODOCOLOR, mTodoColor );
        jsonObject.put( TODOIDENTIFIER, mTodoIdentifier.toString() );
        jsonObject.put( TODOCOMPLETED, mToDoCompleted ); // added by Jackson Firth

        // Added by Jackson Firth - 5569
        if (mToDoDueDate != null) {
            jsonObject.put( TODODUEDATE, mToDoDueDate.getTime() );
            setHasDueDate(true);
        }

        jsonObject.put( TODOPRIORITY, mToDoPriority ); // Added by Sopulu
        return jsonObject;
    }

    public String getToDoDescription() {
        return mToDoDescription;
    }

    public void setToDoDescription(String mToDoDescription ) {
        this.mToDoDescription = mToDoDescription;
    }

    public String getToDoText() {
        return mToDoText;
    }

    public void setToDoText( String mToDoText ) {
        this.mToDoText = mToDoText;
    }

    public boolean hasReminder() {
        return mHasReminder;
    }

    public void setHasReminder( boolean mHasReminder ) {
        this.mHasReminder = mHasReminder;
    }

    public Date getToDoDate() {
        return mToDoDate;
    }

    public int getTodoColor() {
        return mTodoColor;
    }

    public void setTodoColor( int mTodoColor ) {
        this.mTodoColor = mTodoColor;
    }

    public void setToDoDate( Date mToDoDate ) {
        this.mToDoDate = mToDoDate;
    }

    public UUID getIdentifier() {
        return mTodoIdentifier;
    }

    // added by Jackson Firth
    public boolean getToDoCompleted() {
        return this.mToDoCompleted;
    }

    // added by Jackson Firth
    public void setToDoCompleted( boolean mToDoCompleted ) {
        this.mToDoCompleted = mToDoCompleted;
    }

    // Added by Jackson Firth - 5569
    public Date getToDoDueDate( ) {
        return this.mToDoDueDate;
    }

    // Added by Jackson Firth - 5569
    public void setToDoDueDate( Date newDueDate ) {
        this.mToDoDueDate = newDueDate;
        if (newDueDate != null) {
            this.setHasDueDate( true );
        } else {
            this.setHasDueDate( false );
        }
    }

    // Added by Jackson Firth - 5569
    public boolean hasDueDate() {
        return this.mHasDueDate;
    }

    // Added by Jackson Firth - 5569
    public void setHasDueDate( boolean val ) {
        this.mHasDueDate = val;
    }

    // Added by Sopulu
    public void setToDoPriority( String priority ) {
        if (priority != null) {
            mToDoPriority = priority;
        }
    }

    // Added by Sopulu
    public String getToDoPriority() {
        return mToDoPriority;
    }
}

