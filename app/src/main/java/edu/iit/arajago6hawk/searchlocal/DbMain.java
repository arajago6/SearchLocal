package edu.iit.arajago6hawk.searchlocal;

/**
 * Created by rasuishere on 2/27/16.
 */

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spanned;

public class DbMain extends SQLiteOpenHelper {
		
    /**
     * Local database that stores test data.
     * TODO Migrate the app's data source from local to cloud so
     * that we can make our app get data from API with minimal changes.
     */

    public static final String DB_NAME = "sl.db";

    public DbMain(Context context)
    {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates tables for businesses and ads when app is installed.
        try {
            db.execSQL(
                    "create table business " +
                            "(id integer primary key autoincrement, " +
                            "name text, category text, desc text, " +
                            "address text, city text, phone text, " +
                            "web text, fb text, lat double, " +
                            "lng double, imgname text, pub text)"
            );
            db.execSQL(
                    "create table ads " +
                            "(id integer primary key autoincrement, ad blob, busid integer, validity integer)"
            );
            /*
            db.execSQL("insert into business values ('Bob\'s Bikes','Automotive / Recreational','The best bikes in the city!',"+
                            "'W 47th street','Austin','3129374808','www.ownlocal.com','www.facebook.com/OwnLocal'," +
                            "30.291859,-97.735252,'shop3','HCNews')"
            );
            */
        }
        catch(Exception e) {

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Updates database schema on device when needed. 
        try {
            db.execSQL("DROP TABLE IF EXISTS business");
            db.execSQL("DROP TABLE IF EXISTS ads");
            onCreate(db);
        }
        catch(Exception e){}
    }

    public boolean insertBusiness (String name, String category, String desc,
                                   String address, String city, String phone,
                                   String web, String fb, double lat,
                                   double lng, String imgName, String pub)
    {
        try {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("category", category);
        contentValues.put("desc", desc);
        contentValues.put("address", address);
        contentValues.put("city", city);
        contentValues.put("phone", phone);
        contentValues.put("web", web);
        contentValues.put("fb", fb);
        contentValues.put("lat", lat);
        contentValues.put("lng", lng);
        contentValues.put("imgname", imgName);
        contentValues.put("pub", pub);
        db.insert("business", null, contentValues);
        db.close();
        return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public int getBusinessCount(){
        int c = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from business", null);
            c = res.getCount();
            res.close();
            db.close();
        }
        catch(Exception e){

        }
        return c;
    }


    public Integer deleteAllBusinesses ()
    {try {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("business", null, null);
        db.close();
        return 0;
    }
    catch(Exception e){
        return 1;
    }
    }

    public ArrayList<BusinessData> getAllBusinesses() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from business", null);
        int count = res.getCount();
        res.moveToFirst();

        ArrayList<BusinessData> businessDataList = new ArrayList<BusinessData>(count);
        while(res.isAfterLast() == false){

            BusinessData iBD = new BusinessData();
            iBD.setName(res.getString(res.getColumnIndex("name")));
            iBD.setCategory(res.getString(res.getColumnIndex("category")));
            iBD.setDesc(res.getString(res.getColumnIndex("desc")));
            iBD.setAddress(res.getString(res.getColumnIndex("address")));
            iBD.setCity(res.getString(res.getColumnIndex("city")));
            iBD.setPhone(res.getString(res.getColumnIndex("phone")));
            iBD.setWeb(res.getString(res.getColumnIndex("web")));
            iBD.setFb(res.getString(res.getColumnIndex("fb")));
            iBD.setImgName(res.getString(res.getColumnIndex("imgname")));
            iBD.setPub(res.getString(res.getColumnIndex("pub")));
            iBD.setLat(Double.parseDouble(res.getString(res.getColumnIndex("lat"))));
            iBD.setLng(Double.parseDouble(res.getString(res.getColumnIndex("lng"))));
            businessDataList.add(iBD);
            res.moveToNext();
        }
        res.close();
        db.close();
        return businessDataList;
    }
}
