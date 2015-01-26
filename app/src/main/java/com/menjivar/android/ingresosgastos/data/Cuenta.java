package com.menjivar.android.ingresosgastos.data;

import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

/**
* Created by Amilcar Menjivar on 30/12/2014.
*/
public class Cuenta implements Data {
    public int id;
    public String name;
    public String description;
    public double money;
    public int nature;
    public int rubroID;
    public String rubro;

    private Cuenta(int id, String name, String description, int nature, int rubroID, String rubro, double money){
        this.id = id;
        this.name = name;
        this.description = description;
        this.money = money;
        this.nature = nature;
        this.rubroID = rubroID;
        this.rubro = rubro;
    }

    public int getID() {
        return id;
    }

    public String getText() {
        return name;
    }

    public static Cuenta decode(Cursor cursor) {
        int index_id = cursor.getColumnIndex(DBHelper.CUENTAS_COLUMN_ID);
        int index_name = cursor.getColumnIndex(DBHelper.CUENTAS_COLUMN_NAME);
        int index_description = cursor.getColumnIndex(DBHelper.CUENTAS_COLUMN_DESCRIPTION);
        int index_nature = cursor.getColumnIndex(DBHelper.CUENTAS_COLUMN_NATURE);
        if (index_id == -1 || index_name == -1 || index_description == -1 || index_nature == -1 ) {
            //throw new IllegalArgumentException("Attempting to decode a cuenta entry without the minimum data.");
            Log.e("Decoding", "Null cuenta");
            return null;
        }
        int index_rubroId = cursor.getColumnIndex(DBHelper.RUBROS_COLUMN_ID);
        int index_rubroName = cursor.getColumnIndex(DBHelper.RUBROS_COLUMN_NAME);
        int index_money = cursor.getColumnIndex(DBHelper.JOINED_COLUMN_TOTAL_MONEY);


        int id = cursor.getInt(index_id);
        String name = cursor.getString(index_name);
        String description = cursor.getString(index_description);
        int nature = cursor.getInt(index_nature);

        int rubroID = index_rubroId != -1 ? cursor.getInt(index_rubroId) : 0;
        String rubro = index_rubroName != -1 ? cursor.getString(index_rubroName) : null;
        double money = index_money != -1 ? cursor.getDouble(index_money) : 0;
        return new Cuenta(id, name, description, nature, rubroID, rubro, money);
    }

    public Nature nature() {
        return Nature.values()[nature];
    }

    public enum Nature {
        UNKNOWN, INCOME, OUTCOME;

        public String toString() {
            switch(this){
                case INCOME:
                    return "Ingresos";
                case OUTCOME:
                    return "Egresos";
                case UNKNOWN:
                default:
                    return "Unknown";
            }
        }

        public int conversion() {
            switch(this) {
                case INCOME:
                    return 1;
                case OUTCOME:
                    return -1;
                case UNKNOWN:
                default:
                    return 0;
            }
        }
    }
}
