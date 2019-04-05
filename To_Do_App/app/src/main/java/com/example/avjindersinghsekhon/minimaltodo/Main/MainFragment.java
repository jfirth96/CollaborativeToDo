package com.example.avjindersinghsekhon.minimaltodo.Main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.avjindersinghsekhon.minimaltodo.About.AboutActivity;
import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoActivity;
import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoFragment;
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultFragment;
import com.example.avjindersinghsekhon.minimaltodo.Board.BoardFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Reminder.ReminderFragment;
import com.example.avjindersinghsekhon.minimaltodo.Settings.SettingsActivity;
import com.example.avjindersinghsekhon.minimaltodo.Utility.BoardItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.ItemTouchHelperClass;
import com.example.avjindersinghsekhon.minimaltodo.Utility.RecyclerViewEmptySupport;
import com.example.avjindersinghsekhon.minimaltodo.Utility.StoreRetrieveData;
import com.example.avjindersinghsekhon.minimaltodo.Utility.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.TodoNotificationService;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends AppDefaultFragment {
    private RecyclerViewEmptySupport mRecyclerView;
    private ArrayList<ToDoItem> mToDoItemsArrayList;
    private CoordinatorLayout mCoordLayout;
    public static final String TODOITEM = "com.avjindersinghsekhon.com.avjindersinghsekhon.minimaltodo.MainActivity";
    private static UUID boardUUID;
    private MainFragment.BasicListAdapter adapter;
    private ToDoItem mJustDeletedToDoItem;
    private boolean hideCompleted; // Added by Tamara Charchoghlyan
    private int mIndexOfDeletedToDoItem;
    private static final int REQUEST_ID_TODO_ITEM = 100;
    public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a";
    public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm";
    public static final String FILENAME = BoardFragment.FILENAME;
    private StoreRetrieveData storeRetrieveData;
    public ItemTouchHelper itemTouchHelper;
    public static final String SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged";
    public static final String CHANGE_OCCURED = "com.avjinder.changeoccured";
    public static final String THEME_PREFERENCES = "com.avjindersekhon.themepref";
    public static final String RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity";
    public static final String THEME_SAVED = "com.avjindersekhon.savedtheme";
    public static final String DARKTHEME = "com.avjindersekon.darktheme";
    public static final String LIGHTTHEME = "com.avjindersekon.lighttheme";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BoardItem board = (BoardItem) getActivity().getIntent().getExtras().get(BoardFragment.BOARDITEM);
        boardUUID = board.getBoardIdentifier();
        final FloatingActionButton mAddToDoItemFAB;
        int mTheme;
        FragmentActivity activity;

        if ((activity = getActivity()) == null) return;

        mRecyclerView = view.findViewById(R.id.toDoRecyclerView);

        if (themeIsLight()) {
            mTheme = R.style.CustomStyle_LightTheme;
        }
        else mTheme = R.style.CustomStyle_DarkTheme;

        activity.setTheme(mTheme);

        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED, false);
        editor.apply();

        storeRetrieveData = new StoreRetrieveData(getContext(), FILENAME);
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);

        /*
        * Added by Tamara Charchoghlyan
        * System currently doesn't save "user data" and so this setting is set to default every
        * time the app is launched
        */
        hideCompleted = false;
        adapter = new MainFragment.BasicListAdapter(mToDoItemsArrayList);
        setAlarms();

        mCoordLayout = view.findViewById(R.id.myCoordinatorLayout);
        mAddToDoItemFAB = view.findViewById(R.id.addToDoItemFAB);

        // Add Task Click Handler
        mAddToDoItemFAB.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                Intent newTodo = new Intent(getContext(), AddToDoActivity.class);
                ToDoItem item = new ToDoItem("","", false, null);
                newTodo.putExtra(TODOITEM, item);
                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
            }
        });

        mRecyclerView.setEmptyView(view.findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(adapter);
    }

    /**
     * @author Tamara Charchoghlyan
     * Moves an item in mToDoItemsArrayList to given position. Reloads fragments
     * @param fromPosition position of item to be moved
     * @param toPosition new position
     */
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mToDoItemsArrayList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mToDoItemsArrayList, i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * @author Tamara Charchoghlyan
     * Hide completed to do items: move completed items to the bottom, set them as invisible
     */
    public void hideCompletedToDos() {
        hideCompleted = true;
        int i = 0;
        int max = mToDoItemsArrayList.size();

        while (i < max) {
            if (mToDoItemsArrayList.get(i).getToDoCompleted()) {
                moveItem(i, mToDoItemsArrayList.size()-1);
                max --;
            } else {
                i++;
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * @author Tamara Charchoghlyan
     * Show completed to do items: set them as visible
     */
    public void showCompletedToDos() {
        hideCompleted = false;
        adapter.notifyDataSetChanged();
    }

    public static ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData) {
        ArrayList<ToDoItem> items = null;

        try {
            items = storeRetrieveData.loadToDoItemsFromFile(boardUUID);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity;

        if ((activity = getActivity()) == null) return;

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ReminderFragment.EXIT, false);
            editor.apply();
            activity.finish();
        }
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR
         */
        if (activity.getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)) {
            SharedPreferences.Editor editor = activity.getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY, false);
            editor.apply();
            activity.recreate();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentActivity activity;
        if ((activity = getActivity()) == null) return;

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {
            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
            adapter = new MainFragment.BasicListAdapter(mToDoItemsArrayList);
            mRecyclerView.setAdapter(adapter);
            setAlarms();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED, false);
            editor.apply();
        }
    }

    private void setAlarms() {
        if (mToDoItemsArrayList != null) {
            for (ToDoItem item : mToDoItemsArrayList) {
                if (item.hasReminder() && item.getToDoDate() != null) {
                    if (item.getToDoDate().before(new Date())) {
                        item.setToDoDate(null);
                        continue;
                    }
                    Intent i = new Intent(getContext(), TodoNotificationService.class);
                    i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                    i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                    i.putExtra(TodoNotificationService.BOARDUUID, boardUUID);
                    createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(getContext(), AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {
            ToDoItem item = (ToDoItem) data.getSerializableExtra(TODOITEM);
            if (item.getToDoText().length() <= 0) {
                return;
            }
            boolean existed = false;

            if (item.hasReminder() && item.getToDoDate() != null) {
                Intent i = new Intent(getContext(), TodoNotificationService.class);
                i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                i.putExtra(TodoNotificationService.BOARDUUID, boardUUID);
                createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
            }

            for (int i = 0; i < mToDoItemsArrayList.size(); i++) {
                if (item.getIdentifier().equals(mToDoItemsArrayList.get(i).getIdentifier())) {
                    mToDoItemsArrayList.set(i, item);
                    existed = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }

            if (!existed) {
                addToDataStore(item);
                if(hideCompleted) hideCompletedToDos();
            }

            updateDataStorage();
        }
    }

    private AlarmManager getAlarmManager() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        }
        return null;
    }

    private boolean doesPendingIntentExist(Intent i, int requestCode) {
        PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private void createAlarm(Intent i, int requestCode, long timeInMillis) {
        AlarmManager am = getAlarmManager();
        if (am != null) {
            PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
        }
    }

    private void deleteAlarm(Intent i, int requestCode) {
        AlarmManager am = getAlarmManager();
        if (doesPendingIntentExist(i, requestCode)) {
            PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE);
            pi.cancel();
            if (am != null) am.cancel(pi);
        }
    }

    private void addToDataStore(ToDoItem item) {
        mToDoItemsArrayList.add(item);
        adapter.notifyItemInserted(mToDoItemsArrayList.size() - 1);
    }

    public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {
        private ArrayList<ToDoItem> items;

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(items, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(items, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRemoved(final int position) {
            mJustDeletedToDoItem = items.remove(position);
            mIndexOfDeletedToDoItem = position;
            Intent i = new Intent(getContext(), TodoNotificationService.class);
            i.putExtra(TodoNotificationService.BOARDUUID, boardUUID);
            deleteAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode());
            notifyItemRemoved(position);

            Snackbar.make(mCoordLayout, "Deleted Todo", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
                            if (mJustDeletedToDoItem.getToDoDate() != null && mJustDeletedToDoItem.hasReminder()) {
                                Intent i = new Intent(getContext(), TodoNotificationService.class);
                                i.putExtra(TodoNotificationService.TODOTEXT, mJustDeletedToDoItem.getToDoText());
                                i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem.getIdentifier());
                                i.putExtra(TodoNotificationService.BOARDUUID, boardUUID);
                                createAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode(), mJustDeletedToDoItem.getToDoDate().getTime());
                            }
                            notifyItemInserted(mIndexOfDeletedToDoItem);
                        }
                    }).show();
        }

        @NonNull
        @Override
        public BasicListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final BasicListAdapter.ViewHolder holder, final int position) {
            ToDoItem item = items.get(position);
            String timeToShow, format;
            FragmentActivity activity = getActivity();
            if (activity == null) return;

            /*
             * Added by Tamara Charchoghlyan
             * Set the checkbox to checked or unchecked, depending on what the item completed status is
             */
            holder.mToDoCheckBox.setChecked(item.getToDoCompleted());

            holder.mTimeTextView.setVisibility( View.VISIBLE );
            holder.mToDoTextview.setMaxLines( 1 );
            holder.mToDoTextview.setText( item.getToDoText() );

            if (item.getToDoCompleted()) {
                holder.mToDoTextview.setTextColor( Color.LTGRAY );
                holder.mToDoTextview.setPaintFlags(holder.mToDoTextview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else holder.mToDoTextview.setPaintFlags(holder.mToDoTextview.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            /*
             * Added by Tamara Charchoghlyan
             * If hide completed setting is checked and item is completed, hide it. Else, show
             */
            if (hideCompleted && item.getToDoCompleted()) {
                holder.mView.setVisibility(View.GONE);
                // No need to process the rest of the task if it is not going to be displayed: Max Collie
                return;
            }
            else holder.mView.setVisibility(View.VISIBLE);

            if (android.text.format.DateFormat.is24HourFormat( getContext() )) format = DATE_TIME_FORMAT_24_HOUR;
            else format = DATE_TIME_FORMAT_12_HOUR;
            if (item.getToDoDueDate() != null) {
                timeToShow = "Due: " + AddToDoFragment.formatDate( format, item.getToDoDueDate() );
            }
            else if (item.getToDoDate() != null) {
                timeToShow = "Reminder: " + AddToDoFragment.formatDate(format, item.getToDoDate());
            }
            else timeToShow = "No due date set";
            holder.mTimeTextView.setText( timeToShow );

            // Priority Indicator on Task: Added by Jackson Firth
            TextView priorityTextView = holder.mPriorityTextView;
            String priorityValue = item.getToDoPriority();
            ImageView priorityImageView = holder.mPriorityImageView;
            priorityTextView.setText( priorityValue );
            switch (priorityValue) {
                case "Low":
                    priorityImageView.setImageResource(R.drawable.ic_priority_low_24dp);
                    break;
                case "Medium":
                    priorityImageView.setImageResource(R.drawable.ic_priority_medium_24dp);
                    break;
                default:
                    priorityImageView.setImageResource(R.drawable.ic_priority_high_24dp); // high
                    break;
            }
        } // End of onBindViewHolder

        @Override
        public int getItemCount() {
            return items.size();
        }

        BasicListAdapter(ArrayList<ToDoItem> items) {
            this.items = items;
        }

        @SuppressWarnings("deprecation")
        class ViewHolder extends RecyclerView.ViewHolder {
            View mView;
            TextView mToDoTextview;
            CheckBox mToDoCheckBox;
            TextView mTimeTextView;
            TextView mPriorityTextView;   // Added by Jackson Firth
            ImageView mPriorityImageView; // Added by Jackson Firth

            ViewHolder(View v) {
                super(v);
                mView = v;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
                        Intent i = new Intent(getContext(), AddToDoActivity.class);
                        i.putExtra(TODOITEM, item);
                        startActivityForResult(i, REQUEST_ID_TODO_ITEM);
                    }
                });
                // TODO: modify related list_circle_try.xml file
                mToDoTextview = v.findViewById(R.id.toDoListItemTextview);
                mTimeTextView = v.findViewById(R.id.todoListItemTimeTextView);

                mPriorityTextView = v.findViewById( R.id.priorityTextView );   // Added by Jackson Firth
                mPriorityImageView = v.findViewById( R.id.priorityImageView ); // Added by Jackson Firth

                /*
                * Added by Tamara Charchoghlyan
                * Get the checkbox by ID and set the callback function
                * setOnCheckedChangeListener is used (as opposed to setOnClickListener) because if
                * you change the state of the checkbox without clicking (i.e. call the setChecked()
                * as done on line 554), setOnClickListener will not be called but
                * setOnCheckedChangeListener will be called.
                */
                mToDoCheckBox = v.findViewById(R.id.checkDone);
                mToDoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
                        item.setToDoCompleted(isChecked);

                        /*
                        *  Added by Tamara Charchoghlyan & Max
                        *  If item is checked, strike through text
                        *  If item is checked by user and hide completed setting is selected, hide item
                        */
                        if (isChecked) {
                            mToDoTextview.setPaintFlags(mToDoTextview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            mToDoTextview.setTextColor(Color.LTGRAY);
                            if (hideCompleted && mToDoCheckBox.isShown()) {
                                hideCompletedToDos();
                            }
                        }
                        else {
                            if (themeIsLight()) {
                                mToDoTextview.setTextColor(getResources().getColor(R.color.secondary_text));
                            }
                            else mToDoTextview.setTextColor(Color.WHITE);
                            mToDoTextview.setPaintFlags(mToDoTextview.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }

                        updateDataStorage();
                    }
                });
            }
        }
    }

    public void updateDataStorage() {
      try {
            storeRetrieveData.saveToDoItemsToFile(mToDoItemsArrayList, boardUUID);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        updateDataStorage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_todo;
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    // Max Collie
    // Helper function to determine the theme
    public boolean themeIsLight() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            String theme = activity.getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME);
            if (theme == null) return false;
            return theme.equals(LIGHTTHEME);
        }
        return false;
    }
}
