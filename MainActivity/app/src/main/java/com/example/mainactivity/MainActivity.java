package com.example.mainactivity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.graphics.Color;
import android.media.Image;
import android.media.metrics.Event;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.mainactivity.Data.DataBaseHandler;
import com.example.mainactivity.Model.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

public class MainActivity extends AppCompatActivity {

    private Calendar todayCal,currCal;

    private ArrayList<String> dataSetTypes;

    private ArrayList<String> items;

    private PieChart pieChart;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    //calender view variables
    private TextView CalendarView;
    private TextView monthView;
    private TextView prevDay;
    private TextView nextDay;
    private ImageView[] dotsIndicators;

    //log all-in-one variables
    private EditText newcontactpopup_firstname, newcontactpopup_lastname;
    private Button newcontactpopup_save, newcontactpopup_cancel, button_test;
    private DataBaseHandler db;
    private ListView listview;

    //settings menu variables
    private Button settings_button, settings_button_close;

    //graphs menu variables
    private Button graphs_button, graphs_button_close;

    //adventure log variables
    private Button adventure_log_button, adventure_log_button_close;

    //log all-in-one mood menu variables
    private Button log_all_in_one_button, mood_button_ok, mood_button_skip;
    private EditText moodInput_edittext;

    //log all-in-one food menu variables
    private Button food_button_ok, food_button_skip;
    private EditText foodInput_edittext;

    //log all-in-one exercise menu variables
    private Button exercise_button_ok, exercise_button_skip;
    private EditText exerciseInput_edittext;


    //unused test popup
    public void createNewContactDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popup, null);
        newcontactpopup_firstname = (EditText) contactPopupView.findViewById(R.id.newcontactpopup_firstname);
        newcontactpopup_lastname = (EditText) contactPopupView.findViewById(R.id.newcontactpopup_lastname);

        newcontactpopup_save = (Button) contactPopupView.findViewById(R.id.saveButton);
        newcontactpopup_cancel = (Button) contactPopupView.findViewById(R.id.cancelButton);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        newcontactpopup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //define cancel button here!
                dialog.dismiss();
            }
        });
    }

    public void openCalendarMenu(){
        final View calendarMenuView = getLayoutInflater().inflate(R.layout.popup_calendar,null);
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(calendarMenuView);
        dialog = dialogBuilder.create();
        dialog.show();

        ImageView[] filledDot = new ImageView[35];
        for (int i =1; i<=35; i++) {
            String imageID = "day" + i;
            int resID = getResources().getIdentifier(imageID, "id", getPackageName());
            filledDot[i - 1] = (ImageView) calendarMenuView.findViewById(resID);
            if (filledDot[i - 1] != null) {
                filledDot[i - 1].setVisibility(ImageView.INVISIBLE);
            }
        }
        MaterialCalendarView calView = (MaterialCalendarView) calendarMenuView.findViewById(R.id.calendarView);

        calView.setOnDateChangedListener(new OnDateSelectedListener() {
             @Override
             public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                 currCal.set(date.getYear(), date.getMonth(), date.getDay());
             }
         });

        Button backButton = (Button) calendarMenuView.findViewById(R.id.CalBackBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCalendarView();
                dialog.dismiss();
            }
        });
    }

    //function called when clicking the log all-in-one button, opens the mood menu
    public void openMoodMenu(){
        dataSetTypes.clear();
        //gets the layout of the mood menu
        final View moodMenuView = getLayoutInflater().inflate(R.layout.popup_mood, null);

        //edittext for the mood rating
        moodInput_edittext = (EditText) moodMenuView.findViewById(R.id.moodRatingInput);

        //creates and shows the popup
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(moodMenuView);
        dialog = dialogBuilder.create();
        dialog.show();

        //ok and skip button for the mood menu
        mood_button_ok = (Button) moodMenuView.findViewById(R.id.okButton);
        mood_button_skip = (Button) moodMenuView.findViewById(R.id.skipButton);

        //click listener for the mood menu ok button
        mood_button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //insert save stuff here

                saveMood();
                //refreshdata();

                dialog.dismiss();
                openFoodMenu();
            }
        });

        //click listener for the mood menu skip button
        mood_button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dialog.dismiss();
                openFoodMenu();
            }
        });
    }

    //function called when clicking close/ok from the mood menu, opens the food menu
    public void openFoodMenu(){

        //gets the layout of the food menu
        final View foodMenuView = getLayoutInflater().inflate(R.layout.popup_food, null);

        //edittext for the food groups
        foodInput_edittext = (EditText) foodMenuView.findViewById(R.id.foodGroupInput);

        //creates and shows the popup
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(foodMenuView);
        dialog = dialogBuilder.create();
        dialog.show();

        //ok and skip button for the food menu
        food_button_ok = (Button) foodMenuView.findViewById(R.id.okButton);
        food_button_skip = (Button) foodMenuView.findViewById(R.id.skipButton);

        //click listener for the food menu ok button
        food_button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //insert save stuff here

                saveFood();
                //refreshdata();

                dialog.dismiss();
                openExerciseMenu();
            }
        });

        //click listener for the food menu skip button
        food_button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dialog.dismiss();
                openExerciseMenu();
            }
        });
    }

    //function called when clicking close/ok from the food menu, opens the exercise menu
    public void openExerciseMenu(){

        //gets the layout of the exercise menu
        final View exerciseMenuView = getLayoutInflater().inflate(R.layout.popup_exercise, null);

        //edittext for the exercise time
        exerciseInput_edittext = (EditText) exerciseMenuView.findViewById(R.id.exerciseTimeInput);

        //creates and shows the popup
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(exerciseMenuView);
        dialog = dialogBuilder.create();
        dialog.show();

        //ok and skip button for the exercise menu
        exercise_button_ok = (Button) exerciseMenuView.findViewById(R.id.okButton);
        exercise_button_skip = (Button) exerciseMenuView.findViewById(R.id.skipButton);

        //click listener for the food menu ok button
        exercise_button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //insert save stuff here

                saveExercise();
                //refreshdata();

                dialog.dismiss();
            }
        });

        //click listener for the food menu skip button
        exercise_button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dialog.dismiss();
            }
        });
    }

    //function called when opening the settings menu, opens the setting menu
    public void openSettingsMenu(){

        //gets the layout of the settings menu
        final View settingsMenuView = getLayoutInflater().inflate(R.layout.popup_settings, null);

        //creates and shows the popup
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(settingsMenuView);
        dialog = dialogBuilder.create();
        dialog.show();

        //close button for the settings menu
        settings_button_close = (Button) settingsMenuView.findViewById(R.id.closeButton);

        //click listener for the settings menu close button
        settings_button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dialog.dismiss();
            }
        });
    }

    public void openGraphsMenu(){

        //gets the layout of the graphs menu
        final View graphsMenuView = getLayoutInflater().inflate(R.layout.popup_graphs, null);

        //creates and shows the popup
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(graphsMenuView);
        dialog = dialogBuilder.create();
        dialog.show();

        pieChart = graphsMenuView.findViewById(R.id.piechart);
        setupPieChart();
        loadPirChartData();

        //close button for the graohs menu
        graphs_button_close = (Button) graphsMenuView.findViewById(R.id.closeButton);

        //click listener for the graphs menu close button
        graphs_button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dialog.dismiss();
            }
        });
    }

    private void setupPieChart(){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(10);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Intake by Food Category");
        pieChart.setCenterTextSize(16);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPirChartData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(0.2f, "Vegetables"));
        entries.add(new PieEntry(0.15f, "High Sugar"));
        entries.add(new PieEntry(0.35f, "Fruit"));
        entries.add(new PieEntry(0.3f, "Meat"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS){
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Food Intake");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    //function called when opening the adventure log, opens the adventure log
    public void openAdventureLog(){
        ArrayList<String> datasetTypes = db.getDatasetTypes();
        //gets the layout of the adventure log
        final View graphsMenuView = getLayoutInflater().inflate(R.layout.popup_adventure_log, null);
        //creates and shows the popup
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(graphsMenuView);
        dialog = dialogBuilder.create();
        dialog.show();
        ArrayList<String> output;
        try {
            output = db.getallItems(datasetTypes);
        }
        catch(Exception e){
            output = new ArrayList<>();
        }
        String text = "";
        for (int k = 0; k < output.size();) {
            for (int i = 0; i < datasetTypes.size(); i++,k++) {
                text += datasetTypes.get(i) + ": ";
                text += output.get(k) + "\n";
            }
        }
        if(text == ""){
            text = "No Data Available";
        }

        TextView databaseOut= (TextView) graphsMenuView.findViewById(R.id.logview);
        databaseOut.setText(text);

        //close button for the adventure log
        adventure_log_button_close = (Button) graphsMenuView.findViewById(R.id.closeButton);

        //click listener for the graphs menu close button
        adventure_log_button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dialog.dismiss();
            }
        });
    }

    public void updateCalendarView(){
        final int numSpace = 4;
        TextView tvdays = (TextView) findViewById(R.id.calendarMiniView);
        TextView tvMonths = (TextView) findViewById(R.id.monthTextView);

        //days of the week header
        String weekNames = "S&nbsp;&nbsp;M&nbsp;&nbsp;T&nbsp;&nbsp;W&nbsp;&nbsp;T&nbsp;&nbsp;F&nbsp;&nbsp;S";
        //holds the string of the days of the week
        String dayNums = "";

        int day = currCal.get(Calendar.DAY_OF_MONTH);
        int startDate, endDate = 0;

        //formatting for the days of the week
        String month = currCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        Calendar thisWeek = Calendar.getInstance();
        thisWeek.setTime(currCal.getTime());
        thisWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        startDate = thisWeek.get(Calendar.DAY_OF_MONTH);
        //loops through the week
        for(int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            thisWeek.set(Calendar.DAY_OF_WEEK, i);
            String html = thisWeek.get(Calendar.DAY_OF_MONTH)+"";
            //highlights the day the user is currently on
            if (thisWeek.get(Calendar.DAY_OF_MONTH) == day) {
                html = "<b><font color = \"blue\">" + html + "</font></b>";
            }

            if (i != 7) {
                dayNums += html + "&nbsp;";
                if (thisWeek.get(Calendar.DAY_OF_MONTH) < 10) {
                    dayNums += "&nbsp;";
                }
            }
            else {
                endDate = thisWeek.get(Calendar.DAY_OF_MONTH);
                dayNums += html;
                if (thisWeek.get(Calendar.DAY_OF_MONTH) >= 10) {
                    weekNames += "&nbsp;";
                }
            }
        }

        //prints the days and month for that week
        tvdays.setText(Html.fromHtml( weekNames + "<br>" + dayNums));
        tvMonths.setText(month);

        //adds in the dots
        /*thisWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        int tempMonth = thisWeek.get(Calendar.MONTH);
        int tempYear = thisWeek.get(Calendar.YEAR);
        Boolean[] filledDates = db.getDaysOfWeekFilled(startDate, endDate, tempMonth, tempYear);
        for (int i =0; i<7 ; i++){
            if (filledDates[i]){
                dotsIndicators[i].setVisibility(ImageView.VISIBLE);
            }
            else {
                dotsIndicators[i].setVisibility(ImageView.INVISIBLE);
            }
        }*/
    }

    //onCreate function that creates everything when the app is opened
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataSetTypes = new ArrayList<>();
        items = new ArrayList<>();
        db = new DataBaseHandler(getApplicationContext());
        //gets and shows the main screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.listViewID);

        //main screen buttons
        monthView = (TextView) findViewById(R.id.monthTextView);
        CalendarView = (TextView) findViewById(R.id.calendarMiniView);
        prevDay = (TextView) findViewById(R.id.prevDay);
        nextDay = (TextView) findViewById(R.id.nextDay);
        dotsIndicators = new ImageView[]{
                findViewById(R.id.SunDot),
                findViewById(R.id.MonDot),
                findViewById(R.id.TueDot),
                findViewById(R.id.WedDot),
                findViewById(R.id.ThuDot),
                findViewById(R.id.FriDot),
                findViewById(R.id.SatDot),
        };

        log_all_in_one_button = (Button) findViewById(R.id.buttonLog);
        settings_button = (Button) findViewById(R.id.buttonSettings);
        graphs_button = (Button) findViewById(R.id.buttonGraph);
        adventure_log_button = (Button) findViewById(R.id.buttonViewAdventure);

        //click listener for changing calender
        monthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendarMenu();
            }
        });
        CalendarView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openCalendarMenu();
            }
        });
        prevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {changeDayByIncrement(-1); updateCalendarView();}
        });
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {changeDayByIncrement(1); updateCalendarView();}
        });

        //click listener for opening the log all-in-one menu
        log_all_in_one_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMoodMenu();
            }
        });

        //click listener for opening the settings menu
        settings_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSettingsMenu();
            }
        });

        //click listener for opening the graphs menu
        graphs_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openGraphsMenu();
            }
        });

        //click listener for opening the adventure log
        adventure_log_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openAdventureLog();
            }
        });

        updateDateTime();
        updateCalendarView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateDateTime();
    }

    private void saveMood(){
        items.clear();
        dataSetTypes.clear();
        db = new DataBaseHandler(getApplicationContext());

        String moodValue = moodInput_edittext.getText().toString();

        items.add(moodValue);
        //db.Save(moodValue, "Mood", 1,2,3);
        dataSetTypes.add("Mood");

    }

    private void saveFood(){
        db = new DataBaseHandler(getApplicationContext());

        String foodGroups = foodInput_edittext.getText().toString();

        items.add(foodGroups);

        dataSetTypes.add("Food");
    }

    private void saveExercise(){
        String exerciseTime = exerciseInput_edittext.getText().toString();
        items.add(exerciseTime);
        dataSetTypes.add("Exercise");
        db.SaveList(items, dataSetTypes, currCal.get(Calendar.DAY_OF_MONTH),
                currCal.get(Calendar.MONTH), currCal.get(Calendar.YEAR));
    }

    //template
    /*private void saveToDataBase(){
        db = new DataBaseHandler(getApplicationContext());
        Item item = new Item();

        String value = newcontactpopup_firstname.getText().toString();

        item.setInput(value);
        db.Save(item, [type], 1, 2, 3);
    }*/

    public void updateDateTime() {
        Calendar cal = Calendar.getInstance();
        todayCal = cal;
        currCal = cal;
    }

    public void changeDayByIncrement(int incr){
        currCal.add(Calendar.DATE, incr);
    }
}