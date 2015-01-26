package com.menjivar.android.ingresosgastos.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Iterator;


/*
* TODO: create a table for natures. Structure: IdNature, conversion
* Every single registro_money should be multiplied by conversion before summing it for cuentas/rubros totals.
* TODO: that means insertNature, updateNature, deleteNature
* */

/**
 *
 * Created by Amilcar Menjivar on 30/12/2014.
 */
public class DBHelper extends SQLiteOpenHelper {
    /*
    * Rubros:
    *   IdRubro  NombreRubro    DescripcionRubro    Total    Cantidad
    *
    * Cuentas:
    *   IdCuenta  NombreCuenta    DescripcionCuenta    Naturaleza   RubroId     NombreRubro   Total
    *
    * Registros:
    *   IdRegistro  Monto   Descripcion    TimeDate     CuentaId    NombreCuenta
    *
    * Naturalezas:
    *   IdNaturaleza    conversion
    * */
    public static final String DATABASE_NAME = "IngresosEgresos.db";

    private static final int DATABASE_VERSION = 10;

    private static DBHelper instance;

    public static DBHelper getInstance() {
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        instance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE IF NOT EXISTS " + RUBROS_TABLE_NAME + " ("+ RUBROS_TABLE_DESCRIPTION +");" );
        db.execSQL( "CREATE TABLE IF NOT EXISTS " + CUENTAS_TABLE_NAME + " ("+ CUENTAS_TABLE_DESCRIPTION +");" );
        db.execSQL( "CREATE TABLE IF NOT EXISTS " + REGISTROS_TABLE_NAME + " ("+ REGISTROS_TABLE_DESCRIPTION +");" );
        // TODO: create natures table.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + RUBROS_TABLE_NAME);
        db.execSQL( "DROP TABLE IF EXISTS " + CUENTAS_TABLE_NAME);
        db.execSQL( "DROP TABLE IF EXISTS " + REGISTROS_TABLE_NAME);
        // TODO: drop natures table.
        onCreate(db);
    }

    public Cursor query(Query q, Object... itemID) {
        String raw;
        if( itemID != null && itemID.length > 0 ) {
            String whereClause = String.format("WHERE %s = %s", q.columnID, itemID[0]);
            raw = String.format(q.query, whereClause);
        } else {
            raw = String.format(q.query, "");
        }
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(raw, q.data);
    }

    public int getRubrosCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, RUBROS_TABLE_NAME);
    }

    public int getCuentasCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, CUENTAS_TABLE_NAME);
    }

    public int getRegistrosCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, REGISTROS_TABLE_NAME);
    }

    public Rubro getRubro(int id) {
        Cursor cursor = query(Query.Rubro, id);
        cursor.moveToFirst();
        return Rubro.decode(cursor);
    }

    public Cuenta getCuenta(int id) {
        Cursor cursor = query(Query.Cuenta, id);
        cursor.moveToFirst();
        return Cuenta.decode(cursor);
    }

    public Registro getRegistro(int id) {
        Cursor cursor = query(Query.Registro, id);
        cursor.moveToFirst();
        return Registro.decode(cursor);
    }

    public boolean insertRubro(String nameRubro, String descriptionRubro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(RUBROS_COLUMN_NAME, nameRubro);
        contentValues.put(RUBROS_COLUMN_DESCRIPTION, descriptionRubro);

        return db.insert(RUBROS_TABLE_NAME, null, contentValues) != -1;
    }

    public boolean insertCuenta(String nameCuenta, String descriptionCuenta, int nature, int idRubro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CUENTAS_COLUMN_NAME, nameCuenta);
        contentValues.put(CUENTAS_COLUMN_DESCRIPTION, descriptionCuenta);
        contentValues.put(CUENTAS_COLUMN_NATURE, nature);
        contentValues.put(CUENTAS_COLUMN_RUBRO, idRubro);

        return db.insert(CUENTAS_TABLE_NAME, null, contentValues) != -1;
    }

    public long insertRegistro(double amount, String description, int idCuenta, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(REGISTROS_COLUMN_AMOUNT, amount);
        contentValues.put(REGISTROS_COLUMN_DESCRICTION, description);
        contentValues.put(REGISTROS_COLUMN_TIME, date);
        contentValues.put(REGISTROS_COLUMN_CUENTA, idCuenta);

        try{
            return db.insert(REGISTROS_TABLE_NAME, null, contentValues);
        }finally {
            db.close();
        }
    }

    public boolean updateRubro(int id, String nameRubro, String descriptionRubro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(RUBROS_COLUMN_NAME, nameRubro);
        contentValues.put(RUBROS_COLUMN_DESCRIPTION, descriptionRubro);

        return db.update(RUBROS_TABLE_NAME, contentValues, RUBROS_COLUMN_ID+"="+id, null) > 0;
    }

    public boolean updateCuenta(int id, String nameCuenta, String descriptionCuenta, int nature, int idRubro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CUENTAS_COLUMN_NAME, nameCuenta);
        contentValues.put(CUENTAS_COLUMN_DESCRIPTION, descriptionCuenta);
        contentValues.put(CUENTAS_COLUMN_NATURE, nature);
        contentValues.put(CUENTAS_COLUMN_RUBRO, idRubro);

        return db.update(CUENTAS_TABLE_NAME, contentValues, CUENTAS_COLUMN_ID+"="+id, null) > 0;
    }

    public boolean updateRegistro(int id, double amount, String description, int idCuenta, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(REGISTROS_COLUMN_AMOUNT, amount);
        contentValues.put(REGISTROS_COLUMN_DESCRICTION, description);
        contentValues.put(REGISTROS_COLUMN_TIME, date);
        contentValues.put(REGISTROS_COLUMN_CUENTA, idCuenta);

        return db.update(REGISTROS_TABLE_NAME, contentValues, REGISTROS_COLUMN_ID+"="+id, null) > 0;
    }

    public int deleteRubro(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete( RUBROS_TABLE_NAME, RUBROS_COLUMN_ID+" = "+ id, null);
    }

    public int deleteCuenta(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete( CUENTAS_TABLE_NAME, CUENTAS_COLUMN_ID+ " = "+id, null);
    }

    public int deleteRegistro(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete( REGISTROS_TABLE_NAME, REGISTROS_COLUMN_ID+" = "+id, null);
    }

    public ArrayList<Rubro> getAllRubros() {
        ArrayList<Rubro> list = new ArrayList<Rubro>();
        for(Rubro r : iterableRubros()) {
            list.add( r );
        }
        return list;
    }

    public ArrayList<Cuenta> getAllCuentas() {
        ArrayList<Cuenta> list = new ArrayList<Cuenta>();
        for(Cuenta c : iterableCuenta()) {
            list.add( c );
        }
        return list;
    }

    public ArrayList<Registro> getAllRegistros() {
        ArrayList<Registro> list = new ArrayList<Registro>();
        for(Registro r : iterableRegistro()) {
            list.add( r );
        }
        return list;
    }

    public Iterable<Rubro> iterableRubros() {
        final Cursor res = query(Query.Rubro);
        return iterableCursor(res, Rubro.class);
    }

    public Iterable<Cuenta> iterableCuenta() {
        final Cursor res = query(Query.Cuenta);
        return iterableCursor(res, Cuenta.class);
    }

    public Iterable<Registro> iterableRegistro() {
        final Cursor res = query(Query.Registro);
        return iterableCursor(res, Registro.class);
    }

    @SuppressWarnings("unchecked")
    public static <D extends Data> Iterable<D> iterableCursor(final Cursor c, final Class<D> clazz) {
        c.moveToFirst();
        return new Iterable<D>() {
            @Override
            public Iterator<D> iterator() {
                return new Iterator<D>() {
                    public D next() {
                        D foo = null;
                        if( clazz == Rubro.class ) {
                            foo = (D) Rubro.decode(c);
                        } else if( clazz == Cuenta.class ) {
                            foo = (D) Cuenta.decode(c);
                        } else if( clazz == Registro.class ) {
                            foo = (D) Registro.decode(c);
                        }
                        c.moveToNext();
                        return foo;
                    }
                    public boolean hasNext() { return !c.isAfterLast(); }
                    public void remove() { throw new UnsupportedOperationException();}
                };
            }
        };
    }

    // Rubros
    public static final String RUBROS_TABLE_NAME = "Rubros";
    public static final String RUBROS_COLUMN_ID = "IdRubro";
    public static final String RUBROS_COLUMN_NAME = "NombreRubro";
    public static final String RUBROS_COLUMN_DESCRIPTION = "DescripcionRubro";
    public static final String RUBROS_TABLE_DESCRIPTION =
            RUBROS_COLUMN_ID + 				" INTEGER PRIMARY KEY, " +
            RUBROS_COLUMN_NAME + 			" TEXT NOT NULL, "+
            RUBROS_COLUMN_DESCRIPTION +		" TEXT";

    // Cuentas
    public static final String CUENTAS_TABLE_NAME = "Cuentas";
    public static final String CUENTAS_COLUMN_ID = "IdCuenta";
    public static final String CUENTAS_COLUMN_NAME = "NombreCuenta";
    public static final String CUENTAS_COLUMN_DESCRIPTION = "DescripcionCuenta";
    public static final String CUENTAS_COLUMN_NATURE = "Naturaleza";
    public static final String CUENTAS_COLUMN_RUBRO = "RubroId";
    public static final String CUENTAS_TABLE_DESCRIPTION =
            CUENTAS_COLUMN_ID + 			" INTEGER PRIMARY KEY, " +
            CUENTAS_COLUMN_NAME + 			" TEXT NOT NULL, " +
            CUENTAS_COLUMN_DESCRIPTION +	" TEXT, " +
            CUENTAS_COLUMN_NATURE + 		" INTEGER NOT NULL, " +
            CUENTAS_COLUMN_RUBRO + 			" INTEGER NOT NULL";

    // Registros
    public static final String REGISTROS_TABLE_NAME = "Registros";
    public static final String REGISTROS_COLUMN_ID = "IdRegistro";
    public static final String REGISTROS_COLUMN_AMOUNT = "Monto";
    public static final String REGISTROS_COLUMN_DESCRICTION = "Descripcion";
    public static final String REGISTROS_COLUMN_CUENTA = "CuentaId";
    public static final String REGISTROS_COLUMN_TIME = "TimeDate";
    public static final String REGISTROS_TABLE_DESCRIPTION =
            REGISTROS_COLUMN_ID + 			" INTEGER PRIMARY KEY, "+
            REGISTROS_COLUMN_AMOUNT + 		" REAL NOT NULL, "+
            REGISTROS_COLUMN_DESCRICTION + 	" TEXT, "+
            REGISTROS_COLUMN_TIME +         " TEXT, "+
            REGISTROS_COLUMN_CUENTA + 		" INTEGER NOT NULL";

    public static final String JOINED_COLUMN_TOTAL_MONEY = "Total";
    public static final String JOINED_COLUMN_ITEMS_COUNT = "Cantidad";

    /*
    SELECT RUBROS_COLUMN_ID, RUBROS_COLUMN_NAME, RUBROS_COLUMN_DESCRIPTION,
        SUM(REGISTROS_COLUMN_AMOUNT) AS JOINED_COLUMN_TOTAL_MONEY,
        COUNT(distinct CUENTAS_COLUMN_ID) AS JOINED_COLUMN_ITEMS_COUNT
    FROM RUBROS_TABLE_NAME
        LEFT JOIN CUENTAS_TABLE_NAME ON CUENTAS_COLUMN_RUBRO = RUBROS_COLUMN_ID
        LEFT JOIN REGISTROS_TABLE_NAME ON REGISTROS_COLUMN_CUENTA = CUENTAS_COLUMN_ID
    WHERE RUBROS_COLUMN_ID = %id%
    GROUP BY RUBROS_COLUMN_ID, RUBROS_COLUMN_NAME, RUBROS_COLUMN_DESCRIPTION
    */
    private static final String QUERY_RUBRO =
        "SELECT " + RUBROS_COLUMN_ID+", " +RUBROS_COLUMN_NAME+", "+RUBROS_COLUMN_DESCRIPTION +", " +
            "SUM("+REGISTROS_COLUMN_AMOUNT+") AS "+JOINED_COLUMN_TOTAL_MONEY+", "  +
            "COUNT(distinct "+CUENTAS_COLUMN_ID+") AS "+JOINED_COLUMN_ITEMS_COUNT +
        " FROM "+RUBROS_TABLE_NAME +
            " LEFT JOIN "+CUENTAS_TABLE_NAME+" ON "+CUENTAS_COLUMN_RUBRO +" = "+ RUBROS_COLUMN_ID +
            " LEFT JOIN "+REGISTROS_TABLE_NAME+" ON "+REGISTROS_COLUMN_CUENTA +" = "+ CUENTAS_COLUMN_ID +
        " %s"+ // Where clause: WHERE RUBROS_COLUMN_ID = %id%
        " GROUP BY "+RUBROS_COLUMN_ID+", "+RUBROS_COLUMN_NAME+", "+RUBROS_COLUMN_DESCRIPTION;

    /*
    SELECT CUENTAS_COLUMN_ID, CUENTAS_COLUMN_NAME, CUENTAS_COLUMN_DESCRIPTION, CUENTAS_COLUMN_NATURE,
        CUENTAS_COLUMN_RUBRO, RUBROS_COLUMN_NAME, SUM(REGISTROS_COLUMN_AMOUNT) AS JOINED_COLUMN_TOTAL_MONEY
    FROM CUENTAS_TABLE_NAME
        LEFT JOIN RUBROS_TABLE_NAME ON CUENTAS_COLUMN_RUBRO = RUBROS_COLUMN_ID
        LEFT JOIN REGISTROS_TABLE_NAME ON REGISTROS_COLUMN_CUENTA = CUENTAS_COLUMN_ID
    WHERE CUENTAS_COLUMN_ID = %ID%
    GROUP BY CUENTAS_COLUMN_ID, CUENTAS_COLUMN_NAME, CUENTAS_COLUMN_DESCRIPTION, CUENTAS_COLUMN_NATURE, CUENTAS_COLUMN_RUBRO
    */
    private static final String QUERY_CUENTA =
        "SELECT "+CUENTAS_COLUMN_ID+", "+CUENTAS_COLUMN_NAME+", "+CUENTAS_COLUMN_DESCRIPTION+", "+CUENTAS_COLUMN_NATURE+", " +
            CUENTAS_COLUMN_RUBRO+", "+RUBROS_COLUMN_NAME+", SUM("+REGISTROS_COLUMN_AMOUNT+") AS "+JOINED_COLUMN_TOTAL_MONEY +
        " FROM "+CUENTAS_TABLE_NAME+
            " LEFT JOIN "+RUBROS_TABLE_NAME+" ON "+CUENTAS_COLUMN_RUBRO+" = "+RUBROS_COLUMN_ID+
            " LEFT JOIN "+REGISTROS_TABLE_NAME+" ON "+REGISTROS_COLUMN_CUENTA+" = "+CUENTAS_COLUMN_ID +
        " %s" + // Where clause: WHERE CUENTAS_COLUMN_ID = %id%
        " GROUP BY "+CUENTAS_COLUMN_ID+", "+CUENTAS_COLUMN_NAME+", "+CUENTAS_COLUMN_DESCRIPTION+", "+CUENTAS_COLUMN_NATURE+", "+CUENTAS_COLUMN_RUBRO;

    /*
    SELECT REGISTROS_COLUMN_ID, REGISTROS_COLUMN_AMOUNT, REGISTROS_COLUMN_DESCRICTION,
        REGISTROS_COLUMN_TIME, REGISTROS_COLUMN_CUENTA, CUENTAS_COLUMN_NAME
    FROM REGISTROS_TABLE_NAME
        LEFT JOIN CUENTAS_TABLE_NAME ON REGISTROS_COLUMN_CUENTA = CUENTAS_COLUMN_ID
    WHERE REGISTROS_COLUMN_ID = %ID%
    */
    public static final String QUERY_REGISTRO =
        "SELECT "+REGISTROS_COLUMN_ID+", "+REGISTROS_COLUMN_AMOUNT+", "+REGISTROS_COLUMN_DESCRICTION+", " +
            REGISTROS_COLUMN_TIME+", "+REGISTROS_COLUMN_CUENTA+", "+CUENTAS_COLUMN_NAME +
        " FROM "+REGISTROS_TABLE_NAME +
        " LEFT JOIN "+CUENTAS_TABLE_NAME+" ON "+REGISTROS_COLUMN_CUENTA+" = "+CUENTAS_COLUMN_ID+
        " %s"; // Where clause: WHERE REGISTROS_COLUMN_ID = %id%

    /*
    SELECT RUBROS_COLUMN_ID, RUBROS_COLUMN_NAME, RUBROS_COLUMN_DESCRIPTION
    FROM RUBROS_TABLE_NAME
    ORDER BY RUBROS_COLUMN_NAME
    */
    public static final String QUERY_RUBRO_LIST =
        "SELECT "+RUBROS_COLUMN_ID+", "+RUBROS_COLUMN_NAME+", "+RUBROS_COLUMN_DESCRIPTION +
        " FROM "+RUBROS_TABLE_NAME +
        " ORDER BY "+RUBROS_COLUMN_NAME;

    /*
    SELECT CUENTAS_COLUMN_ID, CUENTAS_COLUMN_NAME, CUENTAS_COLUMN_DESCRIPTION, CUENTAS_COLUMN_NATURE, RUBROS_COLUMN_NAME
    FROM CUENTAS_TABLE_NAME LEFT JOIN RUBROS_COLUMN_NAME ON CUENTAS_COLUMN_RUBRO = RUBROS_COLUMN_ID
    ORDER BY RUBROS_COLUMN_NAME, CUENTAS_COLUMN_NAME
     */
    public static final String QUERY_CUENTA_LIST =
        "SELECT "+CUENTAS_COLUMN_ID+", "+CUENTAS_COLUMN_NAME+", "+CUENTAS_COLUMN_DESCRIPTION+", "+CUENTAS_COLUMN_NATURE+", "+RUBROS_COLUMN_NAME +
        " FROM "+CUENTAS_TABLE_NAME+" LEFT JOIN "+RUBROS_TABLE_NAME+" ON "+CUENTAS_COLUMN_RUBRO+" = "+RUBROS_COLUMN_ID +
        " ORDER BY "+RUBROS_COLUMN_NAME+", "+CUENTAS_COLUMN_NAME;

    public enum Query {
        Rubro(null, QUERY_RUBRO, RUBROS_COLUMN_ID),
        Cuenta(null, QUERY_CUENTA, CUENTAS_COLUMN_ID),
        Registro(null, QUERY_REGISTRO, REGISTROS_COLUMN_ID),
        RubroList(null, QUERY_RUBRO_LIST, null),
        CuentaList(null, QUERY_CUENTA_LIST, null);

        String query;
        String[] data;
        String columnID;

        Query(String[] data, String query, String columnID) {
            this.query = query;
            this.data = data;
            this.columnID = columnID;
        }
    }


}
