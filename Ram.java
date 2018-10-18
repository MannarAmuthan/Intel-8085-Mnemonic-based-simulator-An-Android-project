package com.amuthan.bt.a8085;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;




/**
 * Created by AMUTHAN on 9/8/2018.
 * Optimized ram for 8085 with sqlite
 */

public class Ram {
     SQLiteDatabase db;


    Ram(Context ctx){
        db=ctx.openOrCreateDatabase("RamDB", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Ram(address VARCHAR,data VARCHAR);");

    }



    boolean insert(String adrs,String datum){
        //specified entry for stackpointer

        if(adrs.equalsIgnoreCase("SP")){
            if (checkExists(adrs)) {
                update(adrs, datum);
            } else {
                db.execSQL("INSERT INTO Ram VALUES('" + adrs + "','" + datum + "');");
            }
                return true;
        }

        else{

            if(datum.length()>2){
             return false;
            }
            else {
            if (checkExists(adrs)) {
                update(adrs, datum);
            } else {
                db.execSQL("INSERT INTO Ram VALUES('" + adrs + "','" + datum + "');");
            }
            return true;
         }

    }  }

    boolean checkExists(String adrs){
        Cursor c=db.rawQuery("SELECT * FROM Ram WHERE address='"+adrs+"'", null);
        if(c.moveToFirst()){
            return true;
        }
        else{
            return false;
        }
    }
    String get(String adrs) {
        String resultData;
        Cursor c=db.rawQuery("SELECT * FROM Ram WHERE address='"+adrs+"'", null);
        if(c.moveToFirst()){
           resultData= c.getString(1);
            return  resultData;
        }
        else{
            insert(adrs,"00");
            return "00";
        }

     }
    void update(String adrs,String data){
        db.execSQL("UPDATE Ram SET data='"+data+"' WHERE address='"+adrs+"'");
    }


}
