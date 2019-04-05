package com.example.avjindersinghsekhon.minimaltodo.Board;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoFragment;
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication;
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Reminder.ReminderFragment;
import com.example.avjindersinghsekhon.minimaltodo.Main.MainActivity;
import com.example.avjindersinghsekhon.minimaltodo.Utility.BoardItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.ItemTouchHelperClass;
import com.example.avjindersinghsekhon.minimaltodo.Utility.RecyclerViewEmptySupport;
import com.example.avjindersinghsekhon.minimaltodo.Utility.StoreRetrieveData;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class BoardFragment extends AppDefaultFragment {
    private RecyclerViewEmptySupport recyclerView;
    private FloatingActionButton addBoardItemFAB;
    private ArrayList<BoardItem> boardItemsArrayList;
    private CoordinatorLayout coordLayout;
    public static final String BOARDITEM = "com.avjindersinghsekhon.com.avjindersinghsekhon.minimaltodo.BoardActivity";
    private BoardFragment.BasicListAdapter adapter;
    private static final int REQUEST_ID_BOARD_ITEM = 100;
    private BoardItem justDeletedBoardItem;
    private int indexOfDeletedBoardItem;
    public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a";
    public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm";
    public static final String FILENAME = "boarditems.json";
    private StoreRetrieveData storeRetrieveData;
    public ItemTouchHelper itemTouchHelper;
    public static final String SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged";
    public static final String CHANGE_OCCURED = "com.avjinder.changeoccured";
    private int theme = -1;
    private String themeString = "name_of_the_theme";
    public static final String THEME_PREFERENCES = "com.avjindersekhon.themepref";
    public static final String RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity";
    public static final String THEME_SAVED = "com.avjindersekhon.savedtheme";
    public static final String LIGHTTHEME = "com.avjindersekon.lighttheme";
    private AnalyticsApplication app;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (AnalyticsApplication) getActivity().getApplication();

        themeString = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME);

        if (themeString.equals(LIGHTTHEME)) {
            theme = R.style.CustomStyle_LightTheme;
        } else {
            theme = R.style.CustomStyle_DarkTheme;
        }
        this.getActivity().setTheme(theme);

        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED, false);
        editor.apply();

        storeRetrieveData = new StoreRetrieveData(getContext(), FILENAME);
        boardItemsArrayList = getLocallyStoredData(storeRetrieveData);

        adapter = new BoardFragment.BasicListAdapter(boardItemsArrayList);

        coordLayout = view.findViewById(R.id.myBoardCoordinatorLayout);
        addBoardItemFAB = view.findViewById(R.id.addBoardFAB);

        addBoardItemFAB.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                app.send(this, "Action", "FAB pressed");

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Board Name");

                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                toggleKeyboard(v);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String boardName = input.getText().toString();

                        if (boardName.length() <= 0) {
                            return;
                        }

                        Intent newBoard = new Intent(getContext(), MainActivity.class);
                        BoardItem board = new BoardItem(boardName);
                        int color = ColorGenerator.MATERIAL.getRandomColor();
                        board.setBoardColor(color);
                        newBoard.putExtra(BOARDITEM, board);

                        addToDataStore(board);
                        updateDataStorage();
                        InputMethodManager im = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        startActivityForResult(newBoard, REQUEST_ID_BOARD_ITEM);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager im = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        recyclerView = view.findViewById(R.id.boardRecyclerView);
        if (themeString.equals(LIGHTTHEME)) {
            recyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }

        recyclerView.setEmptyView(view.findViewById(R.id.boardEmptyView));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        recyclerView.setAdapter(adapter);

    }

    public void toggleKeyboard(View v) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(v.getWindowToken(), InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public static ArrayList<BoardItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData) {
        ArrayList<BoardItem> items = null;

        try {
            items = storeRetrieveData.loadFromFile();

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
        app.send(this);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ReminderFragment.EXIT, false);
            editor.apply();
            getActivity().finish();
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
        if (getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY, false);
            editor.apply();
            getActivity().recreate();
        }
    }

    @Override
    public void onStart() {
        app = (AnalyticsApplication) getActivity().getApplication();
        super.onStart();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {

            boardItemsArrayList = getLocallyStoredData(storeRetrieveData);
            adapter = new BoardFragment.BasicListAdapter(boardItemsArrayList);
            recyclerView.setAdapter(adapter);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED, false);
            editor.apply();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_BOARD_ITEM) {
            BoardItem board = (BoardItem) data.getSerializableExtra(BOARDITEM);
            if (board.getBoardTitle().length() <= 0) {
                return;
            }
            boolean existed = false;

            for (int i = 0; i < boardItemsArrayList.size(); i++) {
                if (board.getBoardIdentifier().equals(boardItemsArrayList.get(i).getBoardIdentifier())) {
                    boardItemsArrayList.set(i, board);
                    existed = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }

            if (!existed) {
                addToDataStore(board);
            }
        }
    }

    private void addToDataStore(BoardItem board) {
        boardItemsArrayList.add(board);
        adapter.notifyItemInserted(boardItemsArrayList.size() - 1);
    }



    public class BasicListAdapter extends RecyclerView.Adapter<BoardFragment.BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {
        private ArrayList<BoardItem> items;

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
            updateDataStorage();
        }

        @Override
        public void onItemRemoved(final int position) {
            //Remove this line if not using Google Analytics
            app.send(this, "Action", "Swiped Todo Away");

            justDeletedBoardItem = items.remove(position);
            indexOfDeletedBoardItem = position;
            notifyItemRemoved(position);
            updateDataStorage();

            String toShow = "Board";
            Snackbar.make(coordLayout, "Deleted " + toShow, Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            //Comment the line below if not using Google Analytics
                            app.send(this, "Action", "UNDO Pressed");
                            items.add(indexOfDeletedBoardItem, justDeletedBoardItem);
                            notifyItemInserted(indexOfDeletedBoardItem);
                            updateDataStorage();
                        }
                    }).show();
        }

        @Override
        public BoardFragment.BasicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_card, parent, false);
            return new BoardFragment.BasicListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final BoardFragment.BasicListAdapter.ViewHolder holder, final int position) {
            BoardItem item = items.get(position);

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);

            //Background color for each to-do item. Necessary for night/day mode
            int bgColor;
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            int boardTextColor;

            if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTTHEME)) {
                bgColor = Color.WHITE;
                boardTextColor = getResources().getColor(R.color.secondary_text);
            } else {
                bgColor = Color.DKGRAY;
                boardTextColor = Color.WHITE;
            }
            if (item.getBoardCompleted()) {
                boardTextColor = Color.LTGRAY;
            }
            holder.cardView.setBackgroundColor(bgColor);
            holder.colorView.setBackgroundColor(item.getBoardColor());

            holder.boardTitleView.setText(item.getBoardTitle());
            holder.boardTitleView.setTextColor(boardTextColor);

            // Set the date the board was created in the text view
            String format;
            String timeToShow;

            if (android.text.format.DateFormat.is24HourFormat( getContext() )) format = DATE_TIME_FORMAT_24_HOUR;
            else format = DATE_TIME_FORMAT_12_HOUR;
            timeToShow = "Created: " + AddToDoFragment.formatDate( format, item.getDateCreated() );
            holder.timeTextView.setText( timeToShow );
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        BasicListAdapter(ArrayList<BoardItem> items) {
            this.items = items;
        }


        @SuppressWarnings("deprecation")
        public class ViewHolder extends RecyclerView.ViewHolder {

            View view;
            CardView cardView;
            View colorView;
            TextView boardTitleView;
            TextView timeTextView;

            public ViewHolder(View v) {
                super(v);
                view = v;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BoardItem item = items.get(BoardFragment.BasicListAdapter.ViewHolder.this.getAdapterPosition());

                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.putExtra(BOARDITEM, item);
                        startActivityForResult(i, REQUEST_ID_BOARD_ITEM);
                    }
                });

                boardTitleView = v.findViewById(R.id.boardListItemTextview);
                timeTextView = v.findViewById(R.id.boardListItemTimeTextView);
                colorView = v.findViewById(R.id.color_view);
                cardView = v.findViewById(R.id.card_view);


            }

        }
    }

    public void updateDataStorage() {
        try {
            storeRetrieveData.saveToFile(boardItemsArrayList);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        updateDataStorage();
    }

    public static Fragment newInstance() { return new BoardFragment(); }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_board;
    }
}
