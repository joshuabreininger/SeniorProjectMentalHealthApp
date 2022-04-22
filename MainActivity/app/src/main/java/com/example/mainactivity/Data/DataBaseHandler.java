package com.example.mainactivity.Data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//item class was red so i imported this idk if its right
import com.example.mainactivity.Model.Item;
import com.example.mainactivity.campaign.Player;
import com.example.mainactivity.campaign.Enemy;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {

    private ArrayList<String> types;
    private ArrayList<String> longTermTypes;
    private ArrayList<String> graphTypes;


    public DataBaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        types = new ArrayList<>();
        longTermTypes = new ArrayList<>();
        graphTypes = new ArrayList<>();
        setDataTypes();
    }

    public void clearDataBase(Context context){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_DAY);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_MONTH);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_YEAR);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SCENARIO);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_ENEMY_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_PLAYER);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_SETTINGS);
        onCreate(db);
    }

    private void setDataTypes(){
        types.add("Mood");
        types.add("Exercise");
        types.add("Fruit");
        types.add("Vegetable");
        types.add("Sugar");
        longTermTypes.add("Mood");
        longTermTypes.add("Exercise");
        longTermTypes.add("Fruit");
        longTermTypes.add("Vegetable");
        longTermTypes.add("Sugar");
        graphTypes.add("Mood");
        graphTypes.add("Exercise");
        graphTypes.add("Food");
    }

    public ArrayList<String> getGraphTypes() { return graphTypes; }
    public ArrayList<String> getFoodTypes() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Fruit");
        list.add("Vegetable");
        list.add("Sugar");
        return list;
    }
    public ArrayList<String> getDataTypes(){
        return types;
    }
    public ArrayList<String> getDatasetTypes(){
        return types;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Constants.DAYTABLE);
        db.execSQL(Constants.MONTHTABLE);
        db.execSQL(Constants.YEARTABLE);
        db.execSQL(Constants.SETTINGSTABLE);
        db.execSQL(Constants.SCENARIOTABLE);
        db.execSQL(Constants.PLAYERTABLE);
        db.execSQL(Constants.ENEMYTABLELIST);

        ArrayList<String> sceneList = Constants.getScenarioDataBase();
        /*
        scene list
         */
        for( String line: sceneList){
            db.execSQL("INSERT OR REPLACE INTO " + Constants.TABLE_SCENARIO +
                    " (Object_ID, Scenario_ID, Scenario) VALUES " + line + ";");
        }

        /*
        player stats
         */
        String input = "INSERT OR REPLACE INTO  " + Constants.TABLE_PLAYER +
                " (Damage, Exp, Mana, Max_Mana, Magic_Damage, Mana_Recharge,Player_ID, Damage_Mod) " +
                "VALUES (100, 0, 100, 100, 200, 30, 0, 0);";

        db.execSQL(input);

        /*
        null first character
         */
        input = "INSERT OR REPLACE INTO  " + Constants.TABLE_ENEMY_LIST +
                " (Enemy_ID, Enemy_Image_ID, Name, Health, Max_Health, Enemy_Physical_Resistance, Enemy_Magic_Resistance) " +
                "VALUES (0, 0, \"No\", 0, 1, 0, 0);";

        db.execSQL(input);

        /*
        enemy list
         */
        ArrayList<String> enemyList = Constants.getEnemyDataBase();
        for( String stats: enemyList){
            db.execSQL("INSERT OR REPLACE INTO  " + Constants.TABLE_ENEMY_LIST +
                    " (Enemy_ID, Enemy_Image_ID, Name, Health, Max_Health, Enemy_Physical_Resistance, Enemy_Magic_Resistance) " +
                    "VALUES " + stats + ";");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_DAY);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_MONTH);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_YEAR);

        onCreate(db);
    }

    public boolean SaveList(ArrayList<String> items, ArrayList<String> types, int day, int month, int year){
        if (items == null || items.size() == 0){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String columns =  "Day_Num, Month_Num, Year_Num, ";
        String values = day +", " + month +", " + year + ", ";

        boolean firstInputToday = true;
        String test = "SELECT * FROM " + Constants.TABLE_NAME_DAY +
                " WHERE Day_Num = " + day +
                " AND Month_Num = " + month +
                " AND Year_Num = " + year + ";";
        Cursor cursor = db.rawQuery(test, null);
        if (cursor != null && cursor.getCount() > 0 ){
            firstInputToday = false;
        }
        else{
            updateNumTurns(1,false);
        }
        for(int i =0; i<items.size(); i++){
            columns += types.get(i);

            String item = items.get(i);
            String textType;
            if (isNumeric(item)){
                textType = item;
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
                cursor = db.rawQuery(input, null);
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
        return firstInputToday;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
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
        ArrayList<String> foodList = getFoodTypes();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] month = new String[12];
        for (int i=0;i < 12;i++){
            month[i] = "";
        }

        if(foodList.contains(var)) {
            String query = "SELECT Month_Num, Count_" + var + " FROM " + Constants.TABLE_NAME_MONTH +
                    " WHERE Year_ID=" + year + ";";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int monthIndex = cursor.getInt(cursor.getColumnIndexOrThrow("Month_Num"));
                    String input = cursor.getString(cursor.getColumnIndexOrThrow("Count_" + var));
                    month[monthIndex] = input;
                } while (cursor.moveToNext());
            }
        }
        else{
            String query = "SELECT Month_Num, Av_" + var + " FROM " + Constants.TABLE_NAME_MONTH +
                    " WHERE Year_ID=" + year + ";";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int monthIndex = cursor.getInt(cursor.getColumnIndexOrThrow("Month_Num"));
                    String input = cursor.getString(cursor.getColumnIndexOrThrow("Av_" + var));
                    month[monthIndex] = input;
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return month;
    }

    public String[] getAvgYearList(String var, int year, int setBack){
        ArrayList<String> foodList = getFoodTypes();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] yearVal = new String[setBack];
        for (int i=0;i<setBack;i++){
            yearVal[i] = "";
        }

        if(foodList.contains(var)) {
            String query = "SELECT Year_Num, Count_" + var + " FROM " + Constants.TABLE_NAME_YEAR +
                    " WHERE Year_Num >" + (year - setBack) + ";";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int yearIndex = cursor.getInt(cursor.getColumnIndexOrThrow("Year_Num"));
                    String input = cursor.getString(cursor.getColumnIndexOrThrow("Count_" + var));
                    yearVal[setBack - 1 - (year - yearIndex)] = input;
                } while (cursor.moveToNext());
            }
        }
        else {
            String query = "SELECT Year_Num, Av_" + var + " FROM " + Constants.TABLE_NAME_YEAR +
                    " WHERE Year_Num >" + (year - setBack) + ";";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int yearIndex = cursor.getInt(cursor.getColumnIndexOrThrow("Year_Num"));
                    String input = cursor.getString(cursor.getColumnIndexOrThrow("Av_" + var));
                    yearVal[setBack - 1 - (year - yearIndex)] = input;
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return yearVal;
    }

    private void updateAvgMonthYearTable(String type, String val, int month, int year, String prev){
        ArrayList <String> foodList = getFoodTypes();
        int monthCnt = 0;
        double monthAvg = 0;
        int yearCnt = 0;
        double yearAvg = 0;
        String avgType = "Av_" + type;
        String cntType = "Count_" + type;

        SQLiteDatabase db = this.getReadableDatabase();

        boolean noMonth = true;
        boolean noYear = true;


        if (foodList.contains(type)){
            String queryMonth = "SELECT " + cntType +" FROM " + Constants.TABLE_NAME_MONTH +
                    " WHERE Month_Num=" + month +
                    " AND Year_ID=" + year + ";";

            String queryYear = "SELECT " + cntType +" FROM " + Constants.TABLE_NAME_YEAR +
                    " WHERE Year_Num=" + year + ";";

            Cursor cursor = db.rawQuery(queryMonth, null);
            if (cursor != null && cursor.getCount() > 0) {
                noMonth = false;
                cursor.moveToFirst();
                monthCnt = cursor.getInt(cursor.getColumnIndexOrThrow(cntType));
            }

            cursor = db.rawQuery(queryYear, null);
            if (cursor != null && cursor.getCount() > 0) {
                noYear = false;
                cursor.moveToFirst();
                yearCnt = cursor.getInt(cursor.getColumnIndexOrThrow(cntType));
            }
            if (prev.equals("NULL")){
                prev = "false";
            }
            if (!val.equals(prev)){
                if(val.equals("true")){
                    if (noMonth){
                        monthCnt = 1;
                    }
                    else{
                        monthCnt += 1;
                    }
                    if (noYear){
                        yearCnt = 1;
                    }
                    else{
                        yearCnt += 1;
                    }
                }
                else{
                    monthCnt -=1;
                    yearCnt -=1;
                }
                String monthCol = "Month_Num, Year_ID, " + cntType;
                String monthValues = month + ", " + year + ", " + monthCnt;

                String yearCol = "Year_Num, " + cntType;
                String yearValues = year + ", " + yearCnt;

                String monthInput;
                if (noMonth) {
                    monthInput = "INSERT INTO " + Constants.TABLE_NAME_MONTH +
                            " (" + monthCol + ")" + " VALUES (" + monthValues + ");";
                } else {
                    monthInput = "UPDATE " + Constants.TABLE_NAME_MONTH +
                            " SET " +
                            cntType + " = " + monthCnt +
                            " WHERE Month_Num = " + month +
                            " AND Year_ID = " + year + ";";
                }
                db.execSQL(monthInput);

                String yearInput;
                if (noYear) {
                    yearInput = "INSERT INTO " + Constants.TABLE_NAME_YEAR +
                            " (" + yearCol + ")" + " VALUES (" + yearValues + ");";
                } else {
                    yearInput = "UPDATE " + Constants.TABLE_NAME_YEAR +
                            " SET " +
                            cntType + " = " + yearCnt +
                            " WHERE Year_Num = " + year + ";";
                }
                db.execSQL(yearInput);
            }
        }
        else {
            String queryMonth = "SELECT " + avgType + ", " + cntType +" FROM " + Constants.TABLE_NAME_MONTH +
                    " WHERE Month_Num=" + month +
                    " AND Year_ID=" + year + ";";

            String queryYear = "SELECT " + avgType + ", " + cntType +" FROM " + Constants.TABLE_NAME_YEAR +
                    " WHERE Year_Num=" + year + ";";

            Cursor cursor = db.rawQuery(queryMonth, null);
            if (cursor != null && cursor.getCount() > 0) {
                noMonth = false;
                cursor.moveToFirst();
                monthCnt = cursor.getInt(cursor.getColumnIndexOrThrow(cntType));
                monthAvg = cursor.getDouble(cursor.getColumnIndexOrThrow(avgType));
            }
            cursor = db.rawQuery(queryYear, null);

            if (cursor != null && cursor.getCount() > 0) {
                noYear = false;
                cursor.moveToFirst();
                yearCnt = cursor.getInt(cursor.getColumnIndexOrThrow(cntType));
                yearAvg = cursor.getDouble(cursor.getColumnIndexOrThrow(avgType));
            }
            if (prev == "NULL") {
                monthAvg = (monthAvg * monthCnt + Float.parseFloat(val)) / (monthCnt + 1);
                yearAvg = (yearAvg * yearCnt + Float.parseFloat(val)) / (yearCnt + 1);
            } else {
                float newVal = Float.parseFloat(val);
                float prevVal = Float.parseFloat(prev);
                monthAvg = monthAvg + (newVal - prevVal) / (monthCnt);
                yearAvg = yearAvg + (newVal - prevVal) / (yearCnt);
            }

            String monthCol = "Month_Num, Year_ID, " + avgType + ", " + cntType;
            String monthValues = month + ", " + year + ", " + monthAvg + ", " + (monthCnt + 1);

            String yearCol = "Year_Num, " + avgType + ", " + cntType;
            String yearValues = year + ", " + yearAvg + ", " + (yearCnt + 1);

            String monthInput;
            if (noMonth) {
                monthInput = "INSERT INTO " + Constants.TABLE_NAME_MONTH +
                        " (" + monthCol + ")" + " VALUES (" + monthValues + ");";
            } else {
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
            } else {
                yearInput = "UPDATE " + Constants.TABLE_NAME_YEAR +
                        " SET " +
                        avgType + " = " + yearAvg + ", " +
                        cntType + " = " + (yearCnt + 1) +
                        " WHERE Year_Num = " + year + ";";
            }
            db.execSQL(yearInput);
        }
    }

    public String[] getDailyScenario() {
        String[] input  = new String[2];
        SQLiteDatabase db = this.getReadableDatabase();
        String removeScene = "DELETE FROM " + Constants.TABLE_SCENARIO + " WHERE Scenario_ID = -1";
        db.execSQL(removeScene);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_SCENARIO + " ORDER BY RANDOM() LIMIT 1", null);
        if (cursor.moveToFirst()) {
            do {
                input[0] = cursor.getString(cursor.getColumnIndexOrThrow("Object_ID"));
                input[1] = cursor.getString(cursor.getColumnIndexOrThrow("Scenario"));
            } while (cursor.moveToNext());
        }
        String Input = "INSERT OR REPLACE INTO " + Constants.TABLE_SCENARIO +
                " (Scenario_ID, Object_ID, Scenario) VALUES " +
                " (-1, " + input[0] + ", \"" + input[1] + "\" );";
        db.execSQL(Input);
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

    public String[] retrievePrevScenario(){
        String[] input  = new String[2];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Object_ID, Scenario FROM " + Constants.TABLE_SCENARIO + " WHERE Scenario_ID = -1", null);
        if (cursor.moveToFirst()) {
            do {
                input[0] = cursor.getString(cursor.getColumnIndexOrThrow("Object_ID"));
                input[1] = cursor.getString(cursor.getColumnIndexOrThrow("Scenario"));
            } while (cursor.moveToNext());
        }
        return input;
    }

    public Player retrievePlayer(){
        SQLiteDatabase db = this.getReadableDatabase();
        Player player = new Player();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_PLAYER, null);
        //if (cursor != null && cursor.getCount()>0) {
        cursor.moveToFirst();
        player.attackDamage = cursor.getInt(cursor.getColumnIndexOrThrow("Damage"));
        player.mana = cursor.getInt(cursor.getColumnIndexOrThrow("Mana"));
        player.maximumMana = cursor.getInt(cursor.getColumnIndexOrThrow("Max_Mana"));
        player.exp = cursor.getInt(cursor.getColumnIndexOrThrow("Exp"));
        player.magicDamage = cursor.getInt(cursor.getColumnIndexOrThrow("Magic_Damage"));
        player.manaRechargeRate= cursor.getInt(cursor.getColumnIndexOrThrow("Mana_Recharge"));
        player.numberOfTurns = cursor.getInt(cursor.getColumnIndexOrThrow("Num_Turns"));
        player.damgeMod = cursor.getInt(cursor.getColumnIndexOrThrow("Damage_Mod"));
        //}
        return player;
    }

    public Enemy retrieveEnemy(){
        SQLiteDatabase db = this.getReadableDatabase();
        Enemy enemy = new Enemy();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_ENEMY_LIST +
                " WHERE Enemy_ID = 0", null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            enemy.name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            enemy.enemyHealth = cursor.getInt(cursor.getColumnIndexOrThrow("Health"));
            enemy.enemyMaxHealth = cursor.getInt(cursor.getColumnIndexOrThrow("Max_Health"));
            enemy.magicResistance = cursor.getInt(cursor.getColumnIndexOrThrow("Enemy_Magic_Resistance"));
            enemy.physicalResistance = cursor.getInt(cursor.getColumnIndexOrThrow("Enemy_Physical_Resistance"));
            enemy.imageID = cursor.getInt(cursor.getColumnIndexOrThrow("Enemy_Image_ID"));
            return enemy;
        }
        else{
            return null;
        }
    }

    public void updatePlayer(Player player){
        SQLiteDatabase db = this.getReadableDatabase();
        String Input = "UPDATE " + Constants.TABLE_PLAYER +
                " SET " +
                "Damage = " + player.attackDamage + ", " +
                "Mana = " + player.mana + ", " +
                "Max_Mana = " + player.maximumMana + ", " +
                "Exp = " + player.exp + ", " +
                "Magic_Damage = " + player.magicDamage + ", " +
                "Mana_Recharge = " + player.manaRechargeRate + ", " +
                "Num_Turns = " + player.numberOfTurns + ", " +
                "Damage_Mod = " + player.damgeMod + " " +
                "WHERE  Player_ID = 0;";
        db.execSQL(Input);
    }

    public void updateEnemy(Enemy enemy){
        SQLiteDatabase db = this.getReadableDatabase();
        String Input = "INSERT OR REPLACE INTO " + Constants.TABLE_ENEMY_LIST +
                " (Enemy_ID, Enemy_Image_ID, Name, Health, Max_Health, Enemy_Physical_Resistance, Enemy_Magic_Resistance) " +
                "Values (0, " +
                enemy.imageID + ", " +
                "\""+ enemy.name + "\", " +
                enemy.enemyHealth + ", " +
                enemy.enemyMaxHealth + ", " +
                enemy.physicalResistance + ", " +
                enemy.magicResistance + ");";
        db.execSQL(Input);
    }

    public Enemy getNewEnemy(){
        SQLiteDatabase db = this.getReadableDatabase();

        String removeEnemy = "DELETE FROM " + Constants.TABLE_ENEMY_LIST + " WHERE Enemy_ID = 0";
        db.execSQL(removeEnemy);

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_ENEMY_LIST +
                " ORDER BY RANDOM() LIMIT 1", null);
        cursor.moveToFirst();
        Enemy enemy = new Enemy();
        enemy.name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
        enemy.enemyHealth = cursor.getInt(cursor.getColumnIndexOrThrow("Health"));
        enemy.enemyMaxHealth = cursor.getInt(cursor.getColumnIndexOrThrow("Max_Health"));
        enemy.magicResistance = cursor.getInt(cursor.getColumnIndexOrThrow("Enemy_Magic_Resistance"));
        enemy.physicalResistance = cursor.getInt(cursor.getColumnIndexOrThrow("Enemy_Physical_Resistance"));
        enemy.imageID = cursor.getInt(cursor.getColumnIndexOrThrow("Enemy_Image_ID"));
        updateEnemy(enemy);

        return enemy;
    }

    public void updateNumTurns(int turns, boolean add){
        SQLiteDatabase db = this.getReadableDatabase();
        if(add){
            String input = "SELECT Num_Turns FROM " + Constants.TABLE_PLAYER +
                    " WHERE Player_ID = 0;";
            Cursor cursor = db.rawQuery(input, null);
            cursor.moveToFirst();
            int num = cursor.getInt(cursor.getColumnIndexOrThrow("Num_Turns"));
            String Input = "UPDATE " + Constants.TABLE_PLAYER +
                    " SET " +
                    "Num_Turns = " + (turns + num) +
                    " WHERE Player_ID = 0";
            db.execSQL(Input);
        }
        else {
            String Input = "UPDATE " + Constants.TABLE_PLAYER +
                    " SET " +
                    "Num_Turns = " + turns +
                    " WHERE Player_ID = 0";
            db.execSQL(Input);
        }
    }

    public void setDamageMod(int attack){
        SQLiteDatabase db = this.getReadableDatabase();
        String Input = "UPDATE " + Constants.TABLE_PLAYER +
                " SET " +
                "Damage_Mod = " + attack +
                " WHERE Player_ID = 0";
        db.execSQL(Input);
    }

    public void updateManaRegen(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_PLAYER, null);
        cursor.moveToFirst();
        int mana = cursor.getInt(cursor.getColumnIndexOrThrow("Mana"));
        int manaRegen = cursor.getInt(cursor.getColumnIndexOrThrow("Mana_Recharge"));
        int max = cursor.getInt(cursor.getColumnIndexOrThrow("Max_Mana"));
        mana += manaRegen;
        if(mana>max){
            mana = max;
        }
        String Input = "UPDATE " + Constants.TABLE_PLAYER +
                " SET " +
                "Mana = " + mana +
                " WHERE Player_ID = 0";
        db.execSQL(Input);
    }
    public void updateManaRegen(int regen){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_PLAYER, null);
        cursor.moveToFirst();
        int mana = cursor.getInt(cursor.getColumnIndexOrThrow("Mana"));
        int max = cursor.getInt(cursor.getColumnIndexOrThrow("Max_Mana"));
        mana += regen;
        if(mana>max){
            mana = max;
        }
        String Input = "UPDATE " + Constants.TABLE_PLAYER +
                " SET " +
                "Mana = " + mana +
                " WHERE Player_ID = 0";
        db.execSQL(Input);
    }
}
