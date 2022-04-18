package com.example.mainactivity.Data;
import android.graphics.Color;

import com.example.mainactivity.R;

import java.util.ArrayList;

public class Constants {

    public static final String DATABASE_NAME = "MentalHealthDB";
    public static final int DATABASE_VERSION = 1;
    //public static final String KEY_ID = "id";
    public static final String TABLE_NAME_FOOD = "averagefood";
    public static final String TABLE_NAME_DAY = "daytable";
    public static final String TABLE_NAME_MONTH = "monthtable";
    public static final String TABLE_NAME_YEAR = "yeartable";

    public static final String TABLE_SCENARIO =  "scenariotable";
    public static final String TABLE_ENEMY_LIST = "enemylisttable";
    public static final String TABLE_PLAYER = "player";
    //public static final String DATABASE_EDITTEXT = "edittext_value";

    public static final String PLAYERTABLE = "CREATE TABLE " +
            Constants.TABLE_PLAYER + " (" +
            "Damage INTEGER, " +
            "Exp INTEGER, " +
            "Mana INTEGER, " +
            "Max_Mana INTEGER, " +
            "Player_ID INTEGER, " +
            "Magic_Damage INTEGER, " +
            "Day_Num INTEGER, " +
            "Mana_Recharge INTEGER, " +
            "Num_Turns INTEGER, " +
            // this can be used for later if we want to add m
                /*"Stamina_Dmg INTEGER, " +
                "Stamina_Restore INTEGER, " +
                "Mana_Dmg INTEGER, " +
                "Mana_Restore INTEGER, " +
                "Has_Attack BOOLEAN, " +*/
            "PRIMARY KEY (Player_ID));";

    public static final String ENEMYTABLELIST = "CREATE TABLE " +
            Constants.TABLE_ENEMY_LIST + " (" +
            "Health INTEGER, " +
            "Enemy_ID INTEGER, " +
            "Max_Health INTEGER, " +
            "Name varchar(40), " +
            "Enemy_Magic_Resistance INTGER, " +
            "Enemy_Physical_Resistance INTEGER, " +
            "Enemy_Image_ID INTEGER, " +
            "PRIMARY KEY (Enemy_ID));";

    public static final String SCENARIOTABLE = "CREATE TABLE " +
            Constants.TABLE_SCENARIO + " (" +
            "Object_ID INTEGER, " +
            "Scenario_ID INTEGER, " +
            "Scenario varchar(40), " +
            //"FOREIGN KEY (Object_ID) REFERENCES OBJECTTABLE (Object_ID), " +
            "PRIMARY KEY (Scenario_ID));";

    public static final String AVERAGEFOOD = "CREATE TABLE " +
            Constants.TABLE_NAME_FOOD + " (" +
            "Average_Fruits REAL, " +
            "Average_Vegetables REAL, " +
            "Average_Sugar REAL, " +
            "Year_Num INTEGER, " +
            "Month_Num INTEGER DEFAULT - 1);";

    public static final String DAYTABLE = "CREATE TABLE " +
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

    public static final String MONTHTABLE = "CREATE TABLE " +
            Constants.TABLE_NAME_MONTH + " (" +
            "Month_Num INTEGER, " +
            "Av_Exercise REAL, " +
            "Av_Mood REAL, " +
            "Count_Exercise INTEGER, " +
            "Count_Mood INTEGER, " +
            "Year_ID INTEGER, " +
            "FOREIGN KEY (Year_ID) REFERENCES " + Constants.TABLE_NAME_YEAR + " (Year_Num), " +
            "PRIMARY KEY (Month_Num, Year_ID));";

    public static final String YEARTABLE = "CREATE TABLE " +
            Constants.TABLE_NAME_YEAR + " (" +
            "Year_Num INTEGER PRIMARY KEY, " +
            "Count_Exercise INTEGER, " +
            "Count_Mood INTEGER, " +
            "Av_Exercise REAL, " +
            "Av_Mood REAL);";

    public static ArrayList<String> getScenarioDataBase(){
        ArrayList<String> sceneList = new ArrayList<>();

        sceneList.add("(0, 0, \"The Hero finds a goblin, who appears to be lost. The goblin asks for directions to the army of the demon lord, and seems generally unaware of why this would be a bad thing to ask to a human, and gets pointed in the very wrong direction for its efforts.\")");
        sceneList.add("(0, 1, \"The Hero is challenged to a staring contest by a goblin. The goblin doesn't appear to know what a staring contest is, and begins hammering some sticks in the general shape of a staircase, and get increasingly frustrated as it fails to construct something it can stand on without breaking. Perhaps its best to leave it to its own devices.\")");
        sceneList.add("(0, 2, \"The Hero walks up behind a goblin completely covered in mud, taking it by extreme surprise. It screeches something about ascending into its final form, and scampers away in a rush.\")");
        sceneList.add("(0, 3, \"The Hero is challenged to a footrace by a goblin for the fate of the lands. The goblin doesn't wait for a response, and sprints off with great effort. Its speed is rather commendable, which is a pity it didn't see the tree it whacks into moments later.\")");
        sceneList.add("(0, 4, \"The Hero encounters a strange goblin with a most likely stolen noble bedsheet as a cloak, and a crown that looks suspiciously like painted wood. It haughtily names itself the king of the lands, and with excessive flair gifts a coin as a reward for hard work, before wandering off with its chest puffed out. The coin is pretty clearly a token from a board game.\")");
        return sceneList;
    }

    public static ArrayList<String> getEnemyDataBase(){
        ArrayList<String> enemy = new ArrayList<>();

        enemy.add("(1, 0, \"Goblin\", 600, 600, 0, 0)");
        enemy.add("(2, 1, \"Slime\", 800, 800, 20, -80)");
        enemy.add("(3, 2, \"Sneak Crab\", 700, 700, 10, -20)");
        enemy.add("(4, 3, \"Drake\", 1000, 1000, 20, -120)");
        enemy.add("(5, 4, \"Yeti\", 800, 800, -20, -40)");
        enemy.add("(6, 5, \"Electric Angler\", 700, 700, -40, 40)");
        enemy.add("(7, 6, \"Treant\", 800, 800, 0, -30)");
        enemy.add("(8, 7, \"Cockatrice\", 900, 900, -30, 40)");
        enemy.add("(9, 8, \"Ron\", 1200, 1200, -30, -60)");

        return enemy;
    }

    public static int getImageByID(int ID) {
        switch (ID) {
            case 0:
                return R.raw.goblin;
            case 1:
                return R.raw.slime;
            case 2:
                return R.raw.chestcrab;
            case 3:
                return R.raw.dragon;
            case 4:
                return R.raw.yeti;
            case 5:
                return R.raw.electricangler;
            case 6:
                return R.raw.treant;
            case 7:
                return R.raw.cockatrice;
            case 8:
                return R.raw.ron;
        }
        return -1;
    }

    public static Integer getGraphColor(String type){
        switch (type){
            case "Mood":
                return Color.BLUE;
            case "Exercise":
                return Color.RED;
        }
        return Color.BLACK;
    }
}