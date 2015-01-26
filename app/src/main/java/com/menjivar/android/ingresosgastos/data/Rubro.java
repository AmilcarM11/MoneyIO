package com.menjivar.android.ingresosgastos.data;

import android.database.Cursor;

/**
* Created by Amilcar Menjivar on 30/12/2014.
*/
public class Rubro implements Data {
    public int id;
    public String name;
    public String description;
    public double money;
    public int count;

    private Rubro(int id, String name, double money, String description, int count){
        this.id = id;
        this.name = name;
        this.money = money;
        this.description = description;
        this.count = count;
    }

    public int getID() {
        return id;
    }

    public String getText() {
        return name;
    }

    public static Rubro decode(Cursor cursor) {
        int index_id = cursor.getColumnIndex(DBHelper.RUBROS_COLUMN_ID);
        int index_name = cursor.getColumnIndex(DBHelper.RUBROS_COLUMN_NAME);
        int index_description = cursor.getColumnIndex(DBHelper.RUBROS_COLUMN_DESCRIPTION);
        if (index_id == -1 || index_name == -1 || index_description == -1) {
            throw new IllegalArgumentException("Attempting to decode a rubro entry without the minimum data.");
        }
        int index_money = cursor.getColumnIndex(DBHelper.JOINED_COLUMN_TOTAL_MONEY);
        int index_count = cursor.getColumnIndex(DBHelper.JOINED_COLUMN_ITEMS_COUNT);


        int id = cursor.getInt(index_id);
        String name = cursor.getString(index_name);
        String description = cursor.getString(index_description);
        double money = index_money != -1 ? cursor.getDouble(index_money) : 0.0;
        int count = index_count != -1 ? cursor.getInt(index_count) : 0;
        return new Rubro(id, name, money, description, count);
    }

}
