package com.example.mainactivity.Data;
import com.example.mainactivity.R;

public class Constants {

    public static final String DATABASE_NAME = "MentalHealthDB";
    public static final int DATABASE_VERSION = 1;
    //public static final String KEY_ID = "id";
    public static final String TABLE_NAME_FOOD = "averagefood";
    public static final String TABLE_NAME_DAY = "daytable";
    public static final String TABLE_NAME_MONTH = "monthtable";
    public static final String TABLE_NAME_YEAR = "yeartable";
    public static final String TABLE_NAME_SETTINGS = "settingstable";
    //public static final String DATABASE_EDITTEXT = "edittext_value";

    public int getImageByID(int ID) {
        switch (ID) {
            case 0:
                return R.raw.default_character_2;
        }
        return -1;
    }
}