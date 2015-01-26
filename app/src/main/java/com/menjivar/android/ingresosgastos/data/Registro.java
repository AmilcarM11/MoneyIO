package com.menjivar.android.ingresosgastos.data;

import android.database.Cursor;

import java.text.DecimalFormat;

/**
* Created by Amilcar Menjivar on 30/12/2014.
*/
public class Registro implements Data {
    public int id;
    public double money;
    public String description;
    public String date;
    public int cuentaID;
    public String cuenta;

    private Registro(int id, double money, String description, int cuentaID, String cuenta, String date){
        this.id = id;
        this.money = money;
        this.description = description;
        this.cuentaID = cuentaID;
        this.cuenta = cuenta;
        this.date = date;
    }

    public int getID() {
        return id;
    }

    public String getText() {
        return "$ " + new DecimalFormat("###,###,##0.00").format(money);
    }

    public static Registro decode(Cursor cursor) {

        int index_id = cursor.getColumnIndex(DBHelper.REGISTROS_COLUMN_ID);
        int index_money = cursor.getColumnIndex(DBHelper.REGISTROS_COLUMN_AMOUNT);
        int index_description = cursor.getColumnIndex(DBHelper.REGISTROS_COLUMN_DESCRICTION);
        int index_time = cursor.getColumnIndex(DBHelper.REGISTROS_COLUMN_TIME);
        if (index_id == -1 || index_money == -1 || index_description == -1 || index_time == -1) {
            throw new IllegalArgumentException("Attempting to decode a registro entry without the minimum data.");
        }
        int index_cuentaId = cursor.getColumnIndex(DBHelper.CUENTAS_COLUMN_ID);
        int index_cuentaName = cursor.getColumnIndex(DBHelper.CUENTAS_COLUMN_NAME);

        int id = cursor.getInt(index_id);
        double money = cursor.getDouble(index_money);
        String description = cursor.getString(index_description);
        String time = cursor.getString(index_time);

        int cuentaID = index_cuentaId != -1 ? cursor.getInt(index_cuentaId) : 0;
        String cuenta = index_cuentaName != -1 ? cursor.getString(index_cuentaName) : null;
        return new Registro(id, money, description, cuentaID, cuenta, time);
    }

}
