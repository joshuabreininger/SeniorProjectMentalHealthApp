package com.example.mainactivity.Data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

//item was red so i imported this idk if its right

public class DataBaseHandler extends SQLiteOpenHelper {

    private ArrayList<String> types;

    public DataBaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        types = new ArrayList<>();
        setDataTypes();
    }
    private void setDataTypes(){
        types.add("Mood");
        types.add("Food");
        types.add("Exercise");
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
                "Year_ID INTEGER, " +
                "FOREIGN KEY (Year_ID) REFERENCES YEARTABLE (Year_Num), " +
                "PRIMARY KEY (Month_Num, Year_ID));";


        String YEARTABLE = "CREATE TABLE " +
                Constants.TABLE_NAME_YEAR + " (" +
                "Year_Num INTEGER PRIMARY KEY, " +
                "Av_Exercise REAL, " +
                "Av_Mood REAL);";

        //db.execSQL(OBJECTTABLE);
        db.execSQL(SCENARIOTABLE);
        db.execSQL(AVERAGEFOOD);
        db.execSQL(DAYTABLE);
        db.execSQL(MONTHTABLE);
        db.execSQL(YEARTABLE);

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

            if (i+1 < types.size()){
                columns += ", ";
                values += ", ";
            }
        }

        String input = "INSERT OR REPLACE INTO " + Constants.TABLE_NAME_DAY +
                " (" + columns + ") " + " VALUES(" + values + ");";
        db.execSQL(input);
        //db.close();
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

       //cursor.close();
       //db.close();

        return db_list;

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
/*
    //Never implemented, didnt get to part 5 of funny british man video
    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Constants.TABLE_NAME_DAY, Constants.KEY_ID + "=?", new String[] {String.valueOf(id)});

    }
*/


}
