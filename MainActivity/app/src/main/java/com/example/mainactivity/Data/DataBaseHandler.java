package com.example.mainactivity.Data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//item class was red so i imported this idk if its right
import com.example.mainactivity.Model.Item;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {

    private ArrayList<String> types;
    private ArrayList<String> longTermTypes;

    public DataBaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        types = new ArrayList<>();
        longTermTypes = new ArrayList<>();
        setDataTypes();
    }
    private void setDataTypes(){
        types.add("Mood");
        types.add("Fruit");
        types.add("Vegetable");
        types.add("Sugar");
        types.add("Exercise");
        longTermTypes.add("Mood");
        longTermTypes.add("Exercise");
    }

    public ArrayList<String> getGraphTypes() {return longTermTypes;}

    public ArrayList<String> getDataTypes(){
        return types;
    }
    public ArrayList<String> getDatasetTypes(){
        return types;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
        String OBJECTTABLE = "CREATE TABLE objecttable(" +
                "Object_ID INTEGER, " +
                "Object_PATH varchar(20), " +
                "PRIMARY KEY (Object_ID));";



        */
        String SCENARIOTABLE = "CREATE TABLE scenariotable(" +
                "Object_ID INTEGER, " +
                "Scenario_ID INTEGER, " +
                "Scenario varchar(40), " +
                //"FOREIGN KEY (Object_ID) REFERENCES OBJECTTABLE (Object_ID), " +
                "PRIMARY KEY (Object_ID, Scenario_ID));";

        String AVERAGEFOOD = "CREATE TABLE " +
                Constants.TABLE_NAME_FOOD + " (" +
                "Average_Fruits REAL, " +
                "Average_Vegetables REAL, " +
                "Average_Sugar REAL, " +
                "Year_Num INTEGER, " +
                "Month_Num INTEGER DEFAULT - 1);";

        String DAYTABLE = "CREATE TABLE " +
                Constants.TABLE_NAME_DAY + " (" +
                "Day_Num INTEGER, " +
                "Year_Num INTEGER, " +
                "Month_Num INTEGER, " +
                "Mood INTEGER, " +
                "Food varchar(255)," +
                "Fruit INTEGER, " +
                "Vegetable INTEGER, " +
                "Sugar INTEGER, " +
                "Exercise INTEGER, " +
                "IndexID INTEGER," +
                "PRIMARY KEY(Day_Num, Month_Num, Year_Num));";
        // will use these 3 when we fully implement the database - Josh
        //"FOREIGN KEY (Year_Num) REFERENCES YEARTABLE (Year_Num), " +
        //"FOREIGN KEY (Month_Num) REFERENCES MONTHTABLE (Month_Num), " +
        //"PRIMARY KEY(Day_Num, Month_Num, Year_Num));";


        String MONTHTABLE = "CREATE TABLE " +
                Constants.TABLE_NAME_MONTH + " (" +
                "Month_Num INTEGER, " +
                "Av_Exercise REAL, " +
                "Av_Mood REAL, " +
                "Count_Exercise INTEGER, " +
                "Count_Mood INTEGER, " +
                "Year_ID INTEGER, " +
                "FOREIGN KEY (Year_ID) REFERENCES YEARTABLE (Year_Num), " +
                "PRIMARY KEY (Month_Num, Year_ID));";


        String YEARTABLE = "CREATE TABLE " +
                Constants.TABLE_NAME_YEAR + " (" +
                "Year_Num INTEGER PRIMARY KEY, " +
                "Count_Exercise INTEGER, " +
                "Count_Mood INTEGER, " +
                "Av_Exercise REAL, " +
                "Av_Mood REAL);";

        String SETTINGSTABLE = "CREATE TABLE " +
                Constants.TABLE_NAME_SETTINGS + " (" +
                "Settings_ID INTEGER, " +
                "Fruit_Notify INTEGER DEFAULT 5, " +
                "Vegetable_Notify INTEGER DEFAULT 5, " +
                "Sugar_Notify INTEGER DEFAULT 5, " +
                "Exercise_Notify INTEGER DEFAULT 5, " +
                "PRIMARY KEY(Settings_ID));";


        //db.execSQL(OBJECTTABLE);
        db.execSQL(SCENARIOTABLE);
        db.execSQL(AVERAGEFOOD);
        db.execSQL(DAYTABLE);
        db.execSQL(MONTHTABLE);
        db.execSQL(YEARTABLE);
        db.execSQL(SETTINGSTABLE);

        /*
        String temp = "INSERT INTO objecttable" +
                "(Object_ID, Object_Path)" +
                "VALUES" +
                "(2, 'bg1.png');";

        db.execSQL(temp);*/

        String input = "INSERT OR REPLACE INTO SCENARIOTABLE ( Object_ID, Scenario_ID, Scenario) VALUES (0, 0, \"Player has found Player\");";
        db.execSQL(input);

    }//MAKE SURE TO DELETE THE DATABASE AGAIN BEFORE I DO THIS SINCE I HAVE THE APP OPEN WITH THE OLDER BUILD

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_DAY);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_MONTH);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_YEAR);

        onCreate(db);
    }

    public Boolean[] getDaysOfWeekFilled(int startDay, int endDay, int month, int year){
        SQLiteDatabase db = this.getReadableDatabase();
        Boolean[] exist = new Boolean[] {false, false, false, false, false, false, false};
        try {
            if (startDay < endDay) {
                Cursor cursor = db.rawQuery("SELECT Day_Num FROM " + Constants.TABLE_NAME_DAY +
                        " WHERE Month_Num = " + month + " AND Year_Num = " + year +
                        " AND Day_Num >=" + startDay + " AND Day_Num <= " + endDay, null);
                if (cursor.moveToFirst()) {
                    do {
                        int input = cursor.getInt(cursor.getColumnIndexOrThrow("Day_Num"));
                        exist[input - startDay] = true;
                    } while (cursor.moveToNext());
                }
            } else {
                Cursor cursor = db.rawQuery("SELECT Day_Num FROM " + Constants.TABLE_NAME_DAY +
                        " WHERE Month_Num = " + month + " AND Year_Num = " + year +
                        " AND Day_Num >=" + startDay, null);
                if (cursor.moveToFirst()) {
                    do {
                        int input = cursor.getInt(cursor.getColumnIndexOrThrow("Day_Num"));
                        exist[input - startDay] = true;
                    } while (cursor.moveToNext());
                }
                month = (month % 12) + 1;
                if (month == 1) {
                    year++;
                }
                cursor = db.rawQuery("SELECT Day_Num FROM " + Constants.TABLE_NAME_DAY +
                        " WHERE Month_Num = " + month + " AND Year_Num = " + year +
                        " AND Day_Num <=" + endDay, null);
                if (cursor.moveToFirst()) {
                    do {
                        int input = cursor.getInt(cursor.getColumnIndexOrThrow("Day_Num"));
                        exist[6 - (endDay - input)] = true;
                    } while (cursor.moveToNext());
                }
            }
        }catch (Exception e){
            Log.i("TESTDB",e.toString());
        }
        //db.close();
        return exist;
    }

    public ArrayList<Integer> getDaysOfMonthFilled(int month, int year){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> dates = new ArrayList<Integer>();
        Cursor cursor = db.rawQuery("SELECT Day_Num FROM " + Constants.TABLE_NAME_DAY +
                "WHERE Month_Num = " + month + " AND Year_Num = " + year, null);
        if (cursor.moveToFirst()) {
            do {
                int input = cursor.getInt(cursor.getColumnIndexOrThrow("Day_Num"));
                dates.add(input);
            } while (cursor.moveToNext());
        }
        return dates;
    }

    public void SaveList(ArrayList<String> items, ArrayList<String> types, int day, int month, int year){
        SQLiteDatabase db = this.getWritableDatabase();
        String columns =  "Day_Num, Month_Num, Year_Num, ";
        String values = day +", " + month +", " + year + ", ";
        ArrayList<String> alreadyReplaced = new ArrayList<String>();

        for(int i =0; i<items.size(); i++){
            columns += types.get(i);

            String item = items.get(i);
            String textType;
            if (isNumeric(item)){
                textType = ""+item;
            }
            else {
                textType = "\"" + item + "\"";
            }
            values += textType;

            if (i+1 < types.size()) {
                columns += ", ";
                values += ", ";
            }
            if (longTermTypes.contains(types.get(i))){
                String input = "SELECT " + types.get(i) + " FROM " +
                        Constants.TABLE_NAME_DAY + " WHERE " +
                        " Day_Num = " + day +
                        " AND Month_Num = " + month +
                        " AND Year_Num = " + year + ";";
                Cursor cursor = db.rawQuery(input, null);
                String output = "NULL";
                if (cursor != null && cursor.getCount() > 0){
                    cursor.moveToFirst();
                    output = cursor.getString(cursor.getColumnIndexOrThrow(types.get(i)));
                }
                updateAvgMonthYearTable(types.get(i), items.get(i), month, year, output);
            }
        }

        String input = "INSERT OR REPLACE INTO " + Constants.TABLE_NAME_DAY +
                " (" + columns + ") " + " VALUES (" + values + ");";
        db.execSQL(input);
        db.close();
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public ArrayList<String> getallItems(ArrayList<String> types){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> db_list = new ArrayList<>();
        //Cursor cursor = db.query(Constants.TABLE_NAME_DAY, new String[]{Constants.KEY_ID, Constants.DATABASE_EDITTEXT}, null, null, null, null, null);
        //Cursor cursor = db.query(Constants.TABLE_NAME_DAY, new String[]{"Day_Num"}, null, null, null, null, null);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME_DAY, null);
        if (cursor.moveToFirst()){
            do {
                for(String i : types) {
                    String input  = cursor.getString(cursor.getColumnIndexOrThrow(i));

                    db_list.add(input);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return db_list;

    }

    public ArrayList<String> getItemByDay(ArrayList<String> variables,int day, int month, int year){
        if(variables.size() <=0){
            return null;
        }

        String var = variables.get(0);
        for(int i = 1; i< variables.size(); i++){
            var += ", " + variables.get(i);
        }
        String query = "SELECT "+ var +" FROM " + Constants.TABLE_NAME_DAY +
                " WHERE Day_Num=" + day +
                " AND Month_Num=" + month +
                " AND Year_Num=" + year + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> db_list = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                for (String type : variables) {
                    String input = "";
                    try {
                        input = cursor.getString(cursor.getColumnIndexOrThrow(type));
                    }
                    catch(Exception e){
                    }
                    db_list.add(input);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return db_list;
    }

    public String[] getItemsByMonth(String var, int month, int year){
        String[] info = new String[32];
        String query = "SELECT Day_Num, "+ var +" FROM " + Constants.TABLE_NAME_DAY +
                " WHERE Month_Num=" + month +
                " AND Year_Num=" + year + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                int day = cursor.getInt(cursor.getColumnIndexOrThrow("Day_Num"));
                String input = cursor.getString(cursor.getColumnIndexOrThrow(var));
                info[day] = input;
            } while (cursor.moveToNext());
        }
        db.close();
        return info;
    }

    public String[] getAvgMonthInYear(String var, int year){
        String[] month = new String[12];
        for (int i=0;i < 12;i++){
            month[i] = "";
        }
        String query = "SELECT Month_Num, Av_"+ var +" FROM " + Constants.TABLE_NAME_MONTH +
                " WHERE Year_ID=" + year + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                int monthIndex = cursor.getInt(cursor.getColumnIndexOrThrow("Month_Num"));
                String input = cursor.getString(cursor.getColumnIndexOrThrow("Av_" + var));
                month[monthIndex] = input;
            } while (cursor.moveToNext());
        }
        db.close();
        return month;
    }

    public String[] getAvgYearList(String var, int year, int setBack){
        String[] yearVal = new String[setBack];
        for (int i=0;i<setBack;i++){
            yearVal[i] = "";
        }

        String query = "SELECT Year_Num, Av_"+ var +" FROM " + Constants.TABLE_NAME_YEAR +
                " WHERE Year_Num >" + (year - setBack) + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do {
                int yearIndex = cursor.getInt(cursor.getColumnIndexOrThrow("Year_Num"));
                String input = cursor.getString(cursor.getColumnIndexOrThrow("Av_" + var));
                yearVal[ setBack- 1 -(year-yearIndex) ] = input;
            } while (cursor.moveToNext());
        }

        db.close();
        return yearVal;
    }

    private void updateAvgMonthYearTable(String type, String val, int month, int year, String prev){
        int monthCnt = 0;
        double monthAvg = 0;
        int yearCnt = 0;
        double yearAvg = 0;
        String avgType = "Av_" + type;
        String cntType = "Count_" + type;

        String queryMonth = "SELECT " + avgType + ", " + cntType +" FROM " + Constants.TABLE_NAME_MONTH +
                " WHERE Month_Num=" + month +
                " AND Year_ID=" + year + ";";

        String queryYear = "SELECT " + avgType + ", " + cntType +" FROM " + Constants.TABLE_NAME_YEAR +
                " WHERE Year_Num=" + year + ";";


        SQLiteDatabase db = this.getReadableDatabase();
        boolean noMonth = true;
        boolean noYear = true;
        Cursor cursor = db.rawQuery(queryMonth, null);
        if(cursor != null && cursor.getCount() > 0) {
            noMonth = false;
            cursor.moveToFirst();
            monthCnt = cursor.getInt(cursor.getColumnIndexOrThrow(cntType));
            monthAvg = cursor.getDouble(cursor.getColumnIndexOrThrow(avgType));
        }
        cursor = db.rawQuery(queryYear, null);

        if(cursor != null && cursor.getCount() > 0) {
            noYear = false;
            cursor.moveToFirst();
            yearCnt = cursor.getInt(cursor.getColumnIndexOrThrow(cntType));
            yearAvg = cursor.getDouble(cursor.getColumnIndexOrThrow(avgType));
        }
        if(prev == "NULL") {
            monthAvg = (monthAvg * monthCnt + Float.parseFloat(val)) / (monthCnt + 1);
            yearAvg = (yearAvg * yearCnt + Float.parseFloat(val)) / (yearCnt + 1);
        }
        else{
            float newVal = Float.parseFloat(val);
            float prevVal = Float.parseFloat(prev);
            monthAvg = monthAvg + (newVal - prevVal) / (monthCnt);
            yearAvg = yearAvg + (newVal - prevVal) / (yearCnt);
        }

        String monthCol =  "Month_Num, Year_ID, " + avgType + ", " + cntType;
        String monthValues = month +", " + year + ", " + monthAvg + ", " +(monthCnt + 1);

        String yearCol =  "Year_Num, " + avgType + ", " + cntType;
        String yearValues = year + ", " + yearAvg + ", " +(yearCnt + 1);

        String monthInput;
        if (noMonth) {
            monthInput = "INSERT INTO " + Constants.TABLE_NAME_MONTH +
                    " (" + monthCol + ")" + " VALUES (" + monthValues + ");";
        }
        else{
            monthInput = "UPDATE " + Constants.TABLE_NAME_MONTH +
                    " SET " +
                    avgType + " = " + monthAvg + ", " +
                    cntType + " = " + (monthCnt + 1) +
                    " WHERE Month_Num = " + month +
                    " AND Year_ID = " + year + ";";
        }
        db.execSQL(monthInput);

        String yearInput;
        if (noYear) {
            yearInput = "INSERT INTO " + Constants.TABLE_NAME_YEAR +
                    " (" + yearCol + ")" + " VALUES (" + yearValues + ");";
        }
        else{
            yearInput = "UPDATE " + Constants.TABLE_NAME_YEAR +
                    " SET " +
                    avgType + " = " + yearAvg + ", " +
                    cntType + " = " + (yearCnt + 1) +
                    " WHERE Year_Num = " + year + ";";
        }
        db.execSQL(yearInput);
    }

    public String[] getDailyScenario() {
        String[] input  = new String[2];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM scenariotable ORDER BY RANDOM() LIMIT 1", null);

        if (cursor.moveToFirst()) {
            do {
                input[0] = cursor.getString(cursor.getColumnIndexOrThrow("Object_ID"));
                input[1] = cursor.getString(cursor.getColumnIndexOrThrow("Scenario"));
            } while (cursor.moveToNext());
        }
        return input;
    }

    public void saveNotificationSettings(String fruit, String vegetable, String sugar, String exercise) {
        //blank means 5
        //0 means disable
        //any other number is that number of days
        SQLiteDatabase db = this.getReadableDatabase();

        String insertSettings = "INSERT OR IGNORE INTO settingstable (settings_id) VALUES (1 );";
        db.execSQL(insertSettings);

        String updateSettings = "UPDATE settingstable SET settings_id=1, fruit_notify=" + fruit + ", vegetable_notify=" + vegetable + ", sugar_notify=" + sugar + ", exercise_notify=" + exercise + ";";
        db.execSQL(updateSettings);
    }

    public Integer checkNotificationTriggerFruit(int monthNum) {
        Integer[] input  = new Integer[9];
        SQLiteDatabase db = this.getReadableDatabase();
        Integer counter = 0;
        Integer compare = 0;

        Cursor cursor = db.rawQuery("SELECT (Fruit_Notify) FROM settingstable;", null);

        if (cursor.moveToFirst()) {
            do {
                compare = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("Fruit_Notify")));
            } while (cursor.moveToNext());
        }

        Cursor cursor2 = db.rawQuery("SELECT (Fruit) FROM daytable WHERE Month_Num = "+Integer.toString(monthNum)+" ORDER BY Day_Num DESC LIMIT " + Integer.toString(compare) + ";", null);

        if (cursor2.moveToFirst()) {
            do {
                if(cursor2.getString(cursor2.getColumnIndexOrThrow("Fruit")).equals("false")) {
                    counter++;
                }
            } while (cursor2.moveToNext());
        }
        if (counter >= compare && compare != 0) {
            return 1;
        }
        return 0;
    }

    public Integer checkNotificationTriggerVegetable(int monthNum) {
        Integer[] input  = new Integer[9];
        SQLiteDatabase db = this.getReadableDatabase();
        Integer counter = 0;
        Integer compare = 0;

        Cursor cursor = db.rawQuery("SELECT (Vegetable_Notify) FROM settingstable;", null);

        if (cursor.moveToFirst()) {
            do {
                compare = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("Vegetable_Notify")));
            } while (cursor.moveToNext());
        }

        Cursor cursor2 = db.rawQuery("SELECT (Vegetable) FROM daytable WHERE Month_Num = "+Integer.toString(monthNum)+" ORDER BY Day_Num DESC LIMIT " + Integer.toString(compare) + ";", null);

        if (cursor2.moveToFirst()) {
            do {
                if(cursor2.getString(cursor2.getColumnIndexOrThrow("Vegetable")).equals("false")) {
                    counter++;
                }
            } while (cursor2.moveToNext());
        }
        if (counter >= compare && compare != 0) {
            return 1;
        }
        return 0;
    }

    public Integer checkNotificationTriggerSugar(int monthNum) {
        Integer[] input  = new Integer[9];
        SQLiteDatabase db = this.getReadableDatabase();
        Integer counter = 0;
        Integer compare = 0;

        Cursor cursor = db.rawQuery("SELECT (Sugar_Notify) FROM settingstable;", null);

        if (cursor.moveToFirst()) {
            do {
                compare = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("Sugar_Notify")));
            } while (cursor.moveToNext());
        }

        Cursor cursor2 = db.rawQuery("SELECT (Sugar) FROM daytable WHERE Month_Num = "+Integer.toString(monthNum)+" ORDER BY Day_Num DESC LIMIT " + Integer.toString(compare) + ";", null);

        if (cursor2.moveToFirst()) {
            do {
                if(cursor2.getString(cursor2.getColumnIndexOrThrow("Sugar")).equals("true")) {
                    counter++;
                }
            } while (cursor2.moveToNext());
        }
        if (counter >= compare && compare != 0) {
            return 1;
        }
        return 0;
    }

    public Integer checkNotificationTriggerExercise(int monthNum) {
        Integer[] input  = new Integer[9];
        SQLiteDatabase db = this.getReadableDatabase();
        Integer counter = 0;
        Integer compare = 0;

        Cursor cursor = db.rawQuery("SELECT (Exercise_Notify) FROM settingstable;", null);

        if (cursor.moveToFirst()) {
            do {
                compare = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("Exercise_Notify")));
            } while (cursor.moveToNext());
        }

        Cursor cursor2 = db.rawQuery("SELECT (Exercise) FROM daytable WHERE Month_Num = "+Integer.toString(monthNum)+" ORDER BY Day_Num DESC LIMIT " + Integer.toString(compare) + ";", null);

        if (cursor2.moveToFirst()) {
            do {
                if(Integer.parseInt(cursor2.getString(cursor2.getColumnIndexOrThrow("Exercise"))) == 0) {
                    counter++;
                }
            } while (cursor2.moveToNext());
        }
        if (counter >= compare && compare != 0) {
            return 1;
        }
        return 0;
    }
/*
    //Never implemented, didnt get to part 5 of funny british man video
    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Constants.TABLE_NAME_DAY, Constants.KEY_ID + "=?", new String[] {String.valueOf(id)});

    }
*/


}
