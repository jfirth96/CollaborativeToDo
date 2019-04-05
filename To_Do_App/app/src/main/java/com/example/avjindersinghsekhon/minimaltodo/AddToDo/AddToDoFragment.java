package com.example.avjindersinghsekhon.minimaltodo.AddToDo;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner; // Added by Sopulu
import android.widget.TextView;

import android.widget.TimePicker;
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultFragment;
import com.example.avjindersinghsekhon.minimaltodo.Main.MainFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Utility.ToDoItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.support.v4.content.ContextCompat;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class AddToDoFragment extends AppDefaultFragment implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {
    private EditText mToDoTextBodyEditText;
    private EditText mToDoTextBodyDescription;
    private LinearLayout mUserDateSpinnerContainingLinearLayout;
    private LinearLayout mUserDueDateSpinnerContainingLinearLayout; // Added by Jackson Firth - 5569
    private EditText mDateEditText;
    private EditText mTimeEditText;
    private EditText mDueDateEditText; // Added by Jackson Firth - 5569
    private EditText mDueTimeEditText; // Added by Jackson Firth - 5569
    private ToDoItem mUserToDoItem;
    private String mUserEnteredText;
    private String mUserEnteredDescription;
    private String mToDoPriority; // Added by Sopulu
    private String format; // Added by Max
    private boolean time24; // Added by Max
    private boolean mUserHasReminder;
    private boolean mUserHasDueDate; // Added by Jackson Firth - 5569
    private Date mUserReminderDate;
    private Date mUserDueDate; // Added by Jackson Firth - 5569
    private int mUserColor;
    private ImageButton priorityImg;
    // Variables used throughout the program: Max Collie
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    private class DueTimeListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet( TimePicker view, int hourOfDay, int minute ) {
            setDueTime( hourOfDay, minute );
        }
    }

    private class ReminderTimerListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTime( hourOfDay, minute );
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar;
        Spinner mSpinner; // Added by Sopulu
        final SwitchCompat mToDoDateSwitch;
        final SwitchCompat toDoDueDateSwitch; // Added by Jackson Firth
        final FloatingActionButton mToDoSendFloatingActionButton;
        final ImageButton reminderIconImageButton, dueDateImg;
        final FragmentActivity activity;
        LinearLayout mContainerLayout;
        Toolbar mToolbar;
        Context context;

        if ((activity = getActivity()) == null) return;
        if ((context = getContext()) == null) return;

        time24 = DateFormat.is24HourFormat(getContext());
        if (time24) format = "k:mm";
        else format = "h:mm a";

        // Get the ToDoItem based on which one was clicked in the list
        mUserToDoItem = (ToDoItem) activity.getIntent().getSerializableExtra( MainFragment.TODOITEM );
        mUserEnteredText = mUserToDoItem.getToDoText();
        mUserEnteredDescription = mUserToDoItem.getToDoDescription();
        mUserHasReminder  = mUserToDoItem.hasReminder();
        mUserReminderDate = mUserToDoItem.getToDoDate();
        mUserHasDueDate = mUserToDoItem.hasDueDate(); // Added by Jackson Firth - 5569
        mUserDueDate = mUserToDoItem.getToDoDueDate(); // Added by Jackson Firth - 5569
        mUserColor = mUserToDoItem.getTodoColor();
        mToDoPriority = mUserToDoItem.getToDoPriority(); // Added by Sopulu

        // Find all the required views by their IDs
        mToolbar = view.findViewById(R.id.toolbar);
        reminderIconImageButton = view.findViewById(R.id.userToDoReminderIconImageButton);
        mContainerLayout = view.findViewById( R.id.todoReminderAndDateContainerLayout );
        mUserDateSpinnerContainingLinearLayout = view.findViewById( R.id.toDoEnterDateLinearLayout );
        mUserDueDateSpinnerContainingLinearLayout = view.findViewById( R.id.toDoEnterDueDateLinearLayout ); // Added by Jackson Firth - 5569
        mToDoTextBodyEditText = view.findViewById( R.id.userToDoEditText );
        mToDoTextBodyDescription= view.findViewById( R.id.userToDoDescription );
        mToDoDateSwitch = view.findViewById( R.id.toDoHasDateSwitchCompat );
        toDoDueDateSwitch = view.findViewById( R.id.toDoHasDueDateSwitch ); // Added by Jackson Firth - 5569
        mToDoSendFloatingActionButton = view.findViewById( R.id.makeToDoFloatingActionButton );
        mDateEditText = view.findViewById( R.id.newTodoDateEditText );
        mTimeEditText = view.findViewById( R.id.newTodoTimeEditText );
        mDueTimeEditText = view.findViewById( R.id.newTodoDueTimeEditText ); // Added by Jackson Firth - 5569
        mDueDateEditText = view.findViewById( R.id.newTodoDueDateEditText ); // Added by Jackson Firth - 5569
        mSpinner = view.findViewById( R.id.priority_spinner ); // Added by Sopulu
        priorityImg = view.findViewById(R.id.addToDoPriorityImg); // Added by Max Collie
        dueDateImg = view.findViewById(R.id.dueDateIcon); // Added by Max Collie

        // Sets colors according to theme
        if (themeIsLight()) {
            reminderIconImageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_alarm_grey_200_24dp));
            dueDateImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_date_range_grey_24dp));
        }
        else {
            reminderIconImageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alarm_add_white_24dp));
            dueDateImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_date_range_white_24dp));
        }

        // Add button has a cross in place of <-
        final Drawable cross = ContextCompat.getDrawable(context, R.drawable.ic_clear_white_24dp);
        if (cross != null) cross.setColorFilter(ContextCompat.getColor(context, R.color.icons), PorterDuff.Mode.SRC_ATOP);

        ((AppCompatActivity) activity).setSupportActionBar(mToolbar);
        actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(cross);
        }

        // Added by Sopulu
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( getContext(), R.array.priority_array, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( R.layout.spinner_text_view );
        mSpinner.setAdapter( adapter );
        mSpinner.setOnItemSelectedListener( this );

        switch (mToDoPriority) {
            case "Low":
                mSpinner.setSelection(0);
                priorityImg.setImageResource(R.drawable.ic_priority_low_24dp);
                break;
            case "Medium":
                mSpinner.setSelection(1);
                priorityImg.setImageResource(R.drawable.ic_priority_medium_24dp);
                break;
            default:
                mSpinner.setSelection(2); // High
                priorityImg.setImageResource(R.drawable.ic_priority_high_24dp);
                break;
        }

        mToDoTextBodyEditText.setText( mUserEnteredText );
        mToDoTextBodyEditText.setSelection( mToDoTextBodyEditText.length() );
        mToDoTextBodyDescription.setText( mUserEnteredDescription );
        mToDoTextBodyDescription.setSelection( mToDoTextBodyDescription.length() );

        mContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                hideKeyboard( mToDoTextBodyEditText );
                hideKeyboard( mToDoTextBodyDescription );
            }
        });

        mToDoTextBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                mUserEnteredText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mToDoTextBodyDescription.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                mUserEnteredDescription = s.toString();
            }

            @Override
            public void afterTextChanged( Editable s ) {
            }
        });

        // Due Date Switch Handler: Added by Jackson Firth - 5569
        toDoDueDateSwitch.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mUserDueDate = null;
                }
                mUserHasDueDate = isChecked;
                setDueDateAndTimeEditText();
                setEnterDueDateLayoutVisible( isChecked );
                hideKeyboard( mToDoTextBodyEditText );
            }
        });

        // Reminder Switch Handler
        mToDoDateSwitch.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                mUserHasReminder = isChecked;
                if (isChecked) {
                    Log.i("reminder", "Switch ON");
                    setDateAndTimeEditText();
                }
                else {
                    Log.i("reminder", "Switch OFF");
                    mUserReminderDate = null;
                }

                setEnterDateLayoutVisible(isChecked);
                hideKeyboard( mToDoTextBodyEditText );
            }
        });

        // Submit Task Click Handler
        mToDoSendFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToDoTextBodyEditText.length() <= 0) {
                    mToDoTextBodyEditText.setError(getString(R.string.todo_error));
                }
                else if (mUserReminderDate != null && mUserReminderDate.before(new Date())) {
                    makeResult(RESULT_CANCELED);
                } else {
                    makeResult(RESULT_OK);
                    activity.finish();
                }
            }
        });

        // Added by Jackson Firth - 5569
        mDueDateEditText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Date dueDate;
                hideKeyboard( mToDoTextBodyEditText );
                if (mUserToDoItem.getToDoDueDate() != null) {
                    dueDate = mUserDueDate;
                } else {
                    dueDate = new Date();
                }

                calendar.setTime( dueDate );

                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance( AddToDoFragment.this, year, month, day );
                // Function returns whether theme is dark
                datePickerDialog.setThemeDark(!themeIsLight());
                datePickerDialog.show( activity.getFragmentManager(), "DueDateFragment" );
            }
        });

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                if (mUserToDoItem.getToDoDate() != null) {
                    date = mUserReminderDate;
                } else {
                    date = new Date();
                }

                calendar.setTime( date );
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddToDoFragment.this, year, month, day);
                // Function returns whether theme is dark
                datePickerDialog.setThemeDark(!themeIsLight());
                datePickerDialog.show(activity.getFragmentManager(), "DateFragment");
            }
        });

        mDueTimeEditText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Date dueDate;
                hideKeyboard( mToDoTextBodyEditText );
                if (mUserToDoItem.getToDoDueDate() != null) {
                    dueDate = mUserDueDate;
                } else {
                    dueDate = new Date();
                }

                calendar.setTime( dueDate );

                int hour = calendar.get( Calendar.HOUR_OF_DAY );
                int minute = calendar.get( Calendar.MINUTE );

                TimePickerDialog timePickerDialog;
                if (!themeIsLight()) {
                    timePickerDialog = new TimePickerDialog( getContext(), R.style.CustomStyle_DarkTheme,new AddToDoFragment.DueTimeListener(), hour, minute, time24 );
                } else {
                    timePickerDialog = new TimePickerDialog( getContext(),new AddToDoFragment.DueTimeListener(), hour, minute, time24 );
                }
                timePickerDialog.show();
            }
        });

        mTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Date date;
                if (mUserToDoItem.getToDoDate() != null) {
                    date = mUserReminderDate;
                } else {
                    date = new Date();
                }

                calendar.setTime( date );

                int hour = calendar.get( Calendar.HOUR_OF_DAY );
                int minute = calendar.get( Calendar.MINUTE );

                TimePickerDialog timePickerDialog;
                if (!themeIsLight()) {
                    timePickerDialog = new TimePickerDialog( getContext(), R.style.CustomStyle_DarkTheme,new AddToDoFragment.ReminderTimerListener(), hour, minute, time24 );
                } else {
                    timePickerDialog = new TimePickerDialog( getContext(),new AddToDoFragment.ReminderTimerListener(), hour, minute, time24 );
                }
                timePickerDialog.show();
            }
        });

        // Sets the reminder date and switch if one exists: Max Collie
        mToDoDateSwitch.setChecked(mUserHasReminder);
        setEnterDateLayoutVisible(mUserHasReminder);

        // Sets the due date and switch if one exists: Added by Jackson Firth - 5569: Updated by Max Collie
        toDoDueDateSwitch.setChecked( mUserHasDueDate );
        setEnterDueDateLayoutVisible( mUserHasDueDate );
    }// End of OnViewCreated

    // Added by Jackson Firth - 5569
    private void setDueDateAndTimeEditText() {
        String dueTime;

        if (mUserDueDate != null && mUserHasDueDate) {
            mDueDateEditText.setText(formatDate("d MMM, yyyy", mUserDueDate));
        }
        else {
            mDueDateEditText.setText(getString(R.string.date_reminder_default));
            Calendar cal = Calendar.getInstance();
            if (time24) cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
            else cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);

            cal.set(Calendar.MINUTE, 0);
            mUserDueDate = cal.getTime();
        }
        dueTime = formatDate(format, mUserDueDate);
        mDueTimeEditText.setText(dueTime);
    }

    // Updated by Max Collie
    private void setDateAndTimeEditText() {
        String reminderTime;
        if (mUserHasReminder && mUserReminderDate != null) {
            mDateEditText.setText(formatDate("d MMM, yyyy", mUserReminderDate));
        }
        else {
            mDateEditText.setText(getString(R.string.date_reminder_default));
            Calendar cal = Calendar.getInstance();
            if (time24) cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
            else cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);

            cal.set(Calendar.MINUTE, 0);
            mUserReminderDate = cal.getTime();
        }

        reminderTime = formatDate(format, mUserReminderDate);
        mTimeEditText.setText(reminderTime);
    }

    public void hideKeyboard(EditText et) {
        FragmentActivity activity;
        if ((activity = getActivity()) != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    // Added by Jackson Firth - 5569
    public void setDueDate( int year, int month, int day ) {
        int hour, minute;
        Calendar calendar = Calendar.getInstance();
        Calendar reminderCalendar = Calendar.getInstance();

        reminderCalendar.set(year, month, day);

        if (reminderCalendar.before(calendar)) {
            return;
        }

        if (mUserDueDate != null) {
            calendar.setTime( mUserDueDate );
        }

        if (time24) {
            hour = calendar.get( Calendar.HOUR_OF_DAY );
        } else {
            hour = calendar.get( Calendar.HOUR );
        }

        minute = calendar.get( Calendar.MINUTE );

        calendar.set( year, month, day, hour, minute );

        mUserDueDate = calendar.getTime();

        mDueDateEditText.setText( formatDate( "d MMM, yyyy", mUserDueDate ) );
    }

    // Called every time a reminder date is changed
    public void setDate( int year, int month, int day ) {
        int hour, minute;
        Calendar calendar = Calendar.getInstance();
        Calendar reminderCalendar = Calendar.getInstance();

        reminderCalendar.set(year, month, day);

        if (reminderCalendar.before(calendar)) {
            return;
        }

        if (mUserReminderDate != null) {
            calendar.setTime( mUserReminderDate );
        }

        if (time24) {
            hour = calendar.get( Calendar.HOUR_OF_DAY );
        } else {
            hour = calendar.get( Calendar.HOUR );
        }

        minute = calendar.get( Calendar.MINUTE );
        calendar.set( year, month, day, hour, minute );

        mUserReminderDate = calendar.getTime();

        mDateEditText.setText(formatDate("d MMM, yyyy", mUserReminderDate));
    }

    // Added by Jackson Firth - 5569
    public void setDueTime( int hour, int minute ) {
        if (mUserDueDate != null) {
            calendar.setTime( mUserDueDate );
        }

        calendar.set( year, month, day, hour, minute, 0 );
        mUserDueDate = calendar.getTime();
        mDueTimeEditText.setText( formatDate( format, mUserDueDate ) );
    }

    public void setTime( int hour, int minute ) {
        if (mUserReminderDate != null) {
            calendar.setTime( mUserReminderDate );
        }

        calendar.set(year, month, day, hour, minute, 0);
        mUserReminderDate = calendar.getTime();
        mTimeEditText.setText( formatDate( format, mUserReminderDate ) );
    }

    public void makeResult(int result) {
        FragmentActivity activity;
        Intent i = new Intent();
        if (mUserEnteredText.length() > 0) {
            mUserToDoItem.setToDoText(mUserEnteredText);
            mUserToDoItem.setToDoDescription(mUserEnteredDescription);
        }

        if (mUserReminderDate != null) {
            Log.i("reminder", "reminder date is not null!");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime( mUserReminderDate );
            calendar.set( Calendar.SECOND, 0 );
            mUserReminderDate = calendar.getTime();
        }

//         Added by Jackson Firth - 5569
        if (mUserHasDueDate) {
            Calendar cal = Calendar.getInstance();
            cal.setTime( mUserDueDate );
            cal.set( Calendar.SECOND, 0 );
            mUserDueDate = cal.getTime();
        } else {
            mUserDueDate = null;
            mUserHasDueDate = false;
        }

        mUserToDoItem.setHasReminder(mUserHasReminder);
        mUserToDoItem.setToDoDate(mUserReminderDate);
        mUserToDoItem.setTodoColor(mUserColor);
        mUserToDoItem.setHasDueDate( mUserHasDueDate ); // Added by Jackson Firth - 5569
        mUserToDoItem.setToDoDueDate( mUserDueDate ); // Added by Jackson Firth - 5569
        mUserToDoItem.setToDoPriority( mToDoPriority ); // Added by Sopulu
        i.putExtra(MainFragment.TODOITEM, mUserToDoItem);

        if ((activity = getActivity()) != null) activity.setResult(result, i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentActivity activity;
        switch (item.getItemId()) {
            case android.R.id.home:
                if ((activity = getActivity()) != null && NavUtils.getParentActivityName(activity) != null) {
                    makeResult(RESULT_CANCELED);
                    NavUtils.navigateUpFromSameTask(activity);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String formatDate(String formatString, Date dateToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString, Locale.US);
        return simpleDateFormat.format(dateToFormat);
    }

    @Override
    public void onDateSet( DatePickerDialog datePickerDialog, int year, int month, int day ) {
        if (datePickerDialog.getTag().equals("DateFragment")) {
            setDate( year, month, day );
        } else {
            setDueDate( year, month, day );
        }
    }

    // Added by Jackson Firth - 5569
    public void setEnterDueDateLayoutVisible( boolean checked ) {
        if (checked) {
            mUserDueDateSpinnerContainingLinearLayout.setVisibility( View.VISIBLE );
        } else {
            mUserDueDateSpinnerContainingLinearLayout.setVisibility( View.GONE );
        }
    }

    public void setEnterDateLayoutVisible( boolean checked ) {
        if (checked) {
            mUserDateSpinnerContainingLinearLayout.setVisibility( View.VISIBLE );
        } else {
            mUserDateSpinnerContainingLinearLayout.setVisibility( View.GONE );
        }
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_add_to_do;
    }

    public static AddToDoFragment newInstance() {
        return new AddToDoFragment();
    }

    // Max Collie
    // Helper function to determine the theme
    public boolean themeIsLight() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            String theme = activity.getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
            if (theme == null) return false;
            return theme.equals(MainFragment.LIGHTTHEME);
        }
        return false;
    }

    // Added by Sopulu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mToDoPriority = parent.getItemAtPosition( position ).toString();
        // Sets the text color for the item selected to white, if the theme is dark
        if (!themeIsLight()) ((TextView) view).setTextColor(Color.WHITE);
        mUserToDoItem.setToDoPriority( mToDoPriority );


        switch (mToDoPriority) {
            case "Low":
                priorityImg.setImageResource(R.drawable.ic_priority_low_24dp);
                break;
            case "Medium":
                priorityImg.setImageResource(R.drawable.ic_priority_medium_24dp);
                break;
            default:
                priorityImg.setImageResource(R.drawable.ic_priority_high_24dp);
                break;
        }
    }

    // Added by Sopulu
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mUserToDoItem.setToDoPriority( "Low" );
    }
}