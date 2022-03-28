package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.media.Image;
import android.media.metrics.Event;
import android.os.Bundle;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.example.mainactivity.Data.DataBaseHandler;
import com.example.mainactivity.Model.Item;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mainactivity.Data.Constants;
import com.example.mainactivity.Model.Item;
import java.util.Date;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Calendar todayCal,currCal;

    private ArrayList<String> dataSetTypes;

    private ArrayList<String> items;

    private BarChart dayBarChart;
    private BarChart weekBarChart;
    private BarChart monthBarChart;
    private BarChart yearBarChart;
    private int chartIndex;

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
    private Button graphs_button, graphs_button_close, graphs_button_change;

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

    //adventure scenario variables
    private Button adventure_scenario_close;
    private ImageView scenario_bg1, scenario_bg2, scenario_bg3, scenario_bg4, scenario_bg5, scenario_bg6, opponent;
    private TextView scenario_text;

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

    /*
    // This opens the calendar menu
    // the user can see a list of dates in a month and can change the month with the arrows on the side
    // Here the user can select a date and confirm it by selecting the back button
    // once a date is selected, the calendar view changes to match the week
    */
    // creates the calenar view
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

    /*
    // opens the mood menus for the user to input their daily information
     */
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

                generateScenario();
            }
        });

        //click listener for the food menu skip button
        exercise_button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dialog.dismiss();

                generateScenario();
            }
        });
    }

    public void generateScenario(){
        //gets the layout of the adventure scenario
        final View scenarioMenuView = getLayoutInflater().inflate(R.layout.popup_adventure_scenario, null);

        //creates and shows the popup
        dialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault);
        dialogBuilder.setView(scenarioMenuView);
        dialog = dialogBuilder.create();
        dialog.show();

        //accounting for the month
        int monthCheck = currCal.get(Calendar.MONTH);
        scenario_text = (TextView) scenarioMenuView.findViewById(R.id.scenarioText);
        opponent = (ImageView) scenarioMenuView.findViewById(R.id.opponent);

        scenario_bg1 = (ImageView) scenarioMenuView.findViewById(R.id.testBG1);
        scenario_bg2 = (ImageView) scenarioMenuView.findViewById(R.id.testBG2);
        scenario_bg3 = (ImageView) scenarioMenuView.findViewById(R.id.testBG3);
        scenario_bg4 = (ImageView) scenarioMenuView.findViewById(R.id.testBG4);
        scenario_bg5 = (ImageView) scenarioMenuView.findViewById(R.id.testBG5);
        scenario_bg6 = (ImageView) scenarioMenuView.findViewById(R.id.testBG6);
        scenario_bg1.setAlpha(0f);
        scenario_bg2.setAlpha(0f);
        scenario_bg3.setAlpha(0f);
        scenario_bg4.setAlpha(0f);
        scenario_bg5.setAlpha(0f);
        scenario_bg6.setAlpha(0f);

        if (monthCheck >= 0 && monthCheck <= 1) {
            scenario_bg1.setAlpha(1f);
            scenario_text.setTextColor(Color.BLACK);
        }
        else if (monthCheck >= 2 && monthCheck <= 3) {
            scenario_bg2.setAlpha(1f);
            scenario_text.setTextColor(Color.WHITE);
        }
        else if (monthCheck >= 4 && monthCheck <= 5) {
            scenario_bg3.setAlpha(1f);
            scenario_text.setTextColor(Color.BLACK);
        }
        else if (monthCheck >= 6 && monthCheck <= 7) {
            scenario_bg4.setAlpha(1f);
            scenario_text.setTextColor(Color.BLACK);
        }
        else if (monthCheck >= 8 && monthCheck <= 9) {
            scenario_bg5.setAlpha(1f);
            scenario_text.setTextColor(Color.BLACK);
        }
        else if (monthCheck >= 10 && monthCheck <= 11) {
            scenario_bg6.setAlpha(1f);
            scenario_text.setTextColor(Color.WHITE);
        }

        //grab the scenario
        String[] scenario = db.getDailyScenario();

        //set the text to the scenario and the opponent image
        scenario_text.setText(scenario[1]);
        Constants c = new Constants();
        opponent.setImageResource(c.getImageByID(Integer.parseInt(scenario[0])));

        //close button for the adventure scenario popup
        adventure_scenario_close = (Button) scenarioMenuView.findViewById(R.id.closeButton);

        //click listener for the adventure scenario close button
        adventure_scenario_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dialog.dismiss();
            }
        });

    }


    /*
    // shows the settings menu
    */
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

    /*
    // loads the graph to show the daily,weekly, and yearly progress of the user based on their inputs
     */
    public void openGraphsMenu(boolean addPopup){
        ArrayList<String> chartList = db.getGraphTypes();
        //gets the layout of the graphs menu
        final View graphsMenuView = getLayoutInflater().inflate(R.layout.popup_graphs, null);

        //creates and shows the popup
        if(addPopup) {
            dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setView(graphsMenuView);
            dialog = dialogBuilder.create();
            dialog.show();

            //close button for the graphs menu
            graphs_button_close = (Button) graphsMenuView.findViewById(R.id.closeButton);
            graphs_button_change = (Button) graphsMenuView.findViewById(R.id.changegraphbutton);
            graphs_button_change.setText(chartList.get(chartIndex));

            //click listener for the graphs menu close button
            graphs_button_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    dialog.dismiss();
                }
            });
            //changes between the different labels of the graph
            graphs_button_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    ArrayList<String> list = db.getGraphTypes();
                    chartIndex = (chartIndex + 1)% list.size();
                    graphs_button_change.setText(chartList.get(chartIndex));
                    openGraphsMenu(false);
                }
            });

            dayBarChart = graphsMenuView.findViewById(R.id.daybarchart);
            weekBarChart = graphsMenuView.findViewById(R.id.weekbarchart);
            monthBarChart = graphsMenuView.findViewById(R.id.monthbarchart);
            yearBarChart = graphsMenuView.findViewById(R.id.yearbarchart);
            dayBarChart.getAxisLeft().setDrawGridLines(false);
            weekBarChart.getAxisLeft().setDrawGridLines(false);
            monthBarChart.getAxisLeft().setDrawGridLines(false);
            yearBarChart.getAxisLeft().setDrawGridLines(false);
            dayBarChart.getAxisRight().setDrawGridLines(false);
            weekBarChart.getAxisRight().setDrawGridLines(false);
            monthBarChart.getAxisRight().setDrawGridLines(false);
            yearBarChart.getAxisRight().setDrawGridLines(false);
        }
        loadDayBarChartData(chartList.get(chartIndex));
        loadWeekBarChartData(chartList.get(chartIndex));
        loadMonthBarCharData(chartList.get(chartIndex));
        loadYearBarChartData(chartList.get(chartIndex));
    }

    private void loadDayBarChartData(String setting){
        String Days[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        XAxis xaxis = dayBarChart.getXAxis();
        xaxis.setValueFormatter(new IndexAxisValueFormatter(Days));
        xaxis.setCenterAxisLabels(true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularity(1);
        xaxis.setGranularityEnabled(true);
        xaxis.setDrawGridLines(false);

        ArrayList<BarEntry> values = new ArrayList<>();
        //ArrayList<String> graphList = db.getGraphTypes();
        ArrayList<String> graphList = new ArrayList<>();
        graphList.add(setting);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        for(String type : graphList) {
            Calendar thisWeek = currCal;
            thisWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            for (int i = 0; i < 7; i++) {
                thisWeek.set(Calendar.DAY_OF_WEEK, i + 1);
                int day = thisWeek.get(Calendar.DAY_OF_MONTH);
                int month = thisWeek.get(Calendar.MONTH);
                int year = thisWeek.get(Calendar.YEAR);
                ArrayList temp = new ArrayList();
                temp.add(type);
                ArrayList<String> out = db.getItemByDay(temp, day, month, year);
                float x = i;
                float y = 0;
                try {
                    y = Float.parseFloat(out.get(0));
                } catch( Exception e){ }
                values.add(new BarEntry(x, y));
            }
            BarDataSet set = new BarDataSet(values, "Daily " + type);
            set.setColor(getGraphColor(type));
            set.setDrawValues(false);
            dataSets.add(set);
        }

        BarData data = new BarData(dataSets);
        data.setValueTextSize(12f);
        dayBarChart.setData(data);
        dayBarChart.invalidate();
    }

    private void loadWeekBarChartData(String setting){
        String Days[] = {"Wk1", "Wk2", "Wk3", "Wk4"};
        XAxis xaxis = weekBarChart.getXAxis();
        xaxis.setValueFormatter(new IndexAxisValueFormatter(Days));
        xaxis.setCenterAxisLabels(true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularity(1);
        xaxis.setGranularityEnabled(true);
        xaxis.setDrawGridLines(false);

        String[] moodList = db.getItemsByMonth(setting,currCal.get(Calendar.MONTH),currCal.get(Calendar.YEAR));
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i=0; i<4; i++){
            float sum = 0;
            int count = 0;
            for(int j=0;j<7;j++){
                if(moodList[i * 7 + j] != null) {
                    sum += Integer.parseInt(moodList[i * 7 + j]);
                    count++;
                }
            }
            float avg = 0;
            if (count >0){
                avg = sum/count;
            }
            values.add(new BarEntry(i, avg));
        }
        BarDataSet set1 = new BarDataSet(values, "Average Weekly " + setting);
        set1.setColor(getGraphColor(setting));
        set1.setDrawValues(false);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(12f);
        weekBarChart.setData(data);
        weekBarChart.invalidate();
    }

    private void loadMonthBarCharData(String setting){
        String Days[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        XAxis xaxis = monthBarChart.getXAxis();
        xaxis.setValueFormatter(new IndexAxisValueFormatter(Days));
        xaxis.setCenterAxisLabels(true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularity(1);
        xaxis.setGranularityEnabled(true);
        xaxis.setDrawGridLines(false);

        String[] moodList = db.getAvgMonthInYear(setting,currCal.get(Calendar.YEAR));
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i=0; i<12; i++){
            float val = 0;
            if(moodList[i] != null && !moodList[i].equals("")){
                val = Float.parseFloat(moodList[i]);
            }
            values.add(new BarEntry(i, val));
        }
        BarDataSet set1 = new BarDataSet(values, "Average Monthly " + setting);
        set1.setColor(getGraphColor(setting));
        set1.setDrawValues(false);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(12f);
        monthBarChart.setData(data);
        monthBarChart.invalidate();
    }

    private void loadYearBarChartData(String setting){
        int year = currCal.get(Calendar.YEAR);
        String Days[] = new String[7];
        for (int i=6; i>=0; i--){
            Days[i] = (year - i) + "";
        }

        XAxis xaxis = yearBarChart.getXAxis();
        xaxis.setValueFormatter(new IndexAxisValueFormatter(Days));
        xaxis.setCenterAxisLabels(true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularity(1);
        xaxis.setGranularityEnabled(true);
        xaxis.setDrawGridLines(false);

        String[] moodList = db.getAvgYearList(setting,currCal.get(Calendar.YEAR), 7);
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i=0; i < 7; i++){
            float val = 0;
            if(moodList[i] != null && !moodList[i].equals("")){
                val = Float.parseFloat(moodList[i]);
            }
            values.add(new BarEntry(i, val));
        }
        BarDataSet set1 = new BarDataSet(values, "Average Yearly " + setting);
        set1.setColor(getGraphColor(setting));
        set1.setDrawValues(false);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(12f);
        yearBarChart.setData(data);
        yearBarChart.invalidate();
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
        String text = "";

        int day = currCal.get(Calendar.DAY_OF_MONTH);
        int month = currCal.get(Calendar.MONTH);
        int year = currCal.get(Calendar.YEAR);
        ArrayList<String> output = db.getItemByDay(datasetTypes, day, month, year);
        for(int i =0; i<output.size(); i++){
            if(output.get(i) != null && !output.get(i).equals("")) {
                text += datasetTypes.get(i) + ": " + output.get(i) + "\n";
            }
        }
        if(text == ""){
            text = "No Data Available";
        }

        TextView databaseOut = (TextView) graphsMenuView.findViewById(R.id.logview);
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


    // updates the week label to match the current date or the date selected
    public void updateCalendarView(){
        final int numSpace = 4;
        TextView tvdays = (TextView) findViewById(R.id.calendarMiniView);
        TextView tvMonths = (TextView) findViewById(R.id.monthTextView);

        //days of the week header
        String weekNames = "S&nbsp;&nbsp;M&nbsp;&nbsp;T&nbsp;&nbsp;W&nbsp;&nbsp;T&nbsp;&nbsp;F&nbsp;&nbsp;S";
        //holds the string of the days of the week
        String dayNums = "";

        int day = currCal.get(Calendar.DAY_OF_MONTH);

        //formatting for the days of the week
        String month = currCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        Calendar thisWeek = Calendar.getInstance();
        thisWeek.setTime(currCal.getTime());
        thisWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
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
                dayNums += html;
                if (thisWeek.get(Calendar.DAY_OF_MONTH) >= 10) {
                    weekNames += "&nbsp;";
                }
            }
        }

        //prints the days and month for that week
        tvdays.setText(Html.fromHtml( weekNames + "<br>" + dayNums));
        tvMonths.setText(month);

        //adds in the indicators for the week
        int tempMonth = thisWeek.get(Calendar.MONTH);
        int tempYear = thisWeek.get(Calendar.YEAR);
        for (int i =0; i<7 ; i++){
            thisWeek.set(Calendar.DAY_OF_WEEK, i+1);
            ArrayList<String> variables =  db.getDataTypes();
            int d = thisWeek.get(Calendar.DAY_OF_MONTH);
            int m = thisWeek.get(Calendar.MONTH);
            int y = thisWeek.get(Calendar.YEAR);
            ArrayList<String> output = db.getItemByDay(variables, d, m, y);
            if (output.size() > 0) {
                dotsIndicators[i].setVisibility(ImageView.VISIBLE);
            }
            else {
                dotsIndicators[i].setVisibility(ImageView.INVISIBLE);
            }
        }
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

        //calls the notification creation function
        createNotificationChannel();

        //set the intent of the notification to be used when building it (what happens when you click it)
        Intent notifIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

        //notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "testChannel")
                .setSmallIcon(R.drawable.input_dot)
                .setContentTitle("Test Notification")
                .setContentText("This is a test notification.")
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //notification manager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //calls the notification
        notificationManager.notify(100, builder.build());

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
                openGraphsMenu(true);
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

        int monthCheck = currCal.get(Calendar.MONTH);

        ImageView bg1 = (ImageView) findViewById(R.id.testBG1);
        ImageView bg2 = (ImageView) findViewById(R.id.testBG2);
        ImageView bg3 = (ImageView) findViewById(R.id.testBG3);
        ImageView bg4 = (ImageView) findViewById(R.id.testBG4);
        ImageView bg5 = (ImageView) findViewById(R.id.testBG5);
        ImageView bg6 = (ImageView) findViewById(R.id.testBG6);
        bg1.setAlpha(0f);
        bg2.setAlpha(0f);
        bg3.setAlpha(0f);
        bg4.setAlpha(0f);
        bg5.setAlpha(0f);
        bg6.setAlpha(0f);

        if (monthCheck >= 0 && monthCheck <= 1) {
            bg1.setAlpha(1f);
            prevDay.setTextColor(Color.BLACK);
            nextDay.setTextColor(Color.BLACK);
            monthView.setTextColor(Color.BLACK);
            CalendarView.setTextColor(Color.BLACK);
        }
        else if (monthCheck >= 2 && monthCheck <= 3) {
            bg2.setAlpha(1f);
            prevDay.setTextColor(Color.WHITE);
            nextDay.setTextColor(Color.WHITE);
            monthView.setTextColor(Color.WHITE);
            CalendarView.setTextColor(Color.WHITE);
        }
        else if (monthCheck >= 4 && monthCheck <= 5) {
            bg3.setAlpha(1f);
            prevDay.setTextColor(Color.BLACK);
            nextDay.setTextColor(Color.BLACK);
            monthView.setTextColor(Color.BLACK);
            CalendarView.setTextColor(Color.BLACK);
        }
        else if (monthCheck >= 6 && monthCheck <= 7) {
            bg4.setAlpha(1f);
            prevDay.setTextColor(Color.BLACK);
            nextDay.setTextColor(Color.BLACK);
            monthView.setTextColor(Color.BLACK);
            CalendarView.setTextColor(Color.BLACK);
        }
        else if (monthCheck >= 8 && monthCheck <= 9) {
            bg5.setAlpha(1f);
            prevDay.setTextColor(Color.BLACK);
            nextDay.setTextColor(Color.BLACK);
            monthView.setTextColor(Color.BLACK);
            CalendarView.setTextColor(Color.BLACK);
        }
        else if (monthCheck >= 10 && monthCheck <= 11) {
            bg6.setAlpha(1f);
            prevDay.setTextColor(Color.WHITE);
            nextDay.setTextColor(Color.WHITE);
            monthView.setTextColor(Color.WHITE);
            CalendarView.setTextColor(Color.WHITE);
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "testNotification";
            String description = "A test notification.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("testChannel", name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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

    private Integer getGraphColor(String type){
        switch (type){
            case "Mood":
                return Color.BLUE;
            case "Exercise":
                return Color.RED;
        }
        return Color.BLACK;
    }
}