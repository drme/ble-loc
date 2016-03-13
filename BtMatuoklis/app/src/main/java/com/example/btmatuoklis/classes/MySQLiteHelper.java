package com.example.btmatuoklis.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CalibrationDB";

    private static final String TABLE_ROOMS = "rooms";
    private static final String TABLE_BEACONS = "beacons";
    private static final String TABLE_CALIBRATIONS = "calibrations";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static final String KEY_MAC = "mac";

    private static final String KEY_ROOMID = "roomid";
    private static final String KEY_BEACONID = "beaconid";
    private static final String KEY_RSSI = "rssi";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREATE_ROOMS_TABLE = "CREATE TABLE "+TABLE_ROOMS+" ("+KEY_ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_NAME+" TEXT)";
        String CREATE_BEACONS_TABLE = "CREATE TABLE "+TABLE_BEACONS+" ("+KEY_ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_NAME+" TEXT, "+KEY_MAC+" TEXT)";
        String CREATE_CALIBRATIONS_TABLE = "CREATE TABLE "+TABLE_CALIBRATIONS+" ("+KEY_ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_ROOMID+" INTEGER, "+KEY_BEACONID+
                " INTEGER, "+KEY_RSSI+" TEXT)";
        database.execSQL(CREATE_BEACONS_TABLE);
        database.execSQL(CREATE_ROOMS_TABLE);
        database.execSQL(CREATE_CALIBRATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS "+TABLE_ROOMS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACONS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CALIBRATIONS);
        this.onCreate(database);
    }

    public void addRoom(Room room){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, room.getName());
        database.insert(TABLE_ROOMS, null, values);
        database.close();
    }

    public int getLastRoomID() {
        int id = -1;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT ROWID FROM "+TABLE_ROOMS+" ORDER BY ROWID DESC LIMIT 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
            cursor.close();
        }
        return id;
    }

    public void addBeacon(Beacon beacon){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, beacon.getName());
        values.put(KEY_MAC, beacon.getMAC());
        database.insert(TABLE_BEACONS, null, values);
        database.close();
    }

    public int getLastBeaconID(){
        int id = -1;
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT ROWID FROM "+TABLE_BEACONS+" ORDER BY ROWID DESC LIMIT 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
            cursor.close();
        }
        return id;
    }

    public void addCalibration(Calibration calibration){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROOMID, calibration.getRoomID());
        values.put(KEY_BEACONID, calibration.getBeaconID());
        values.put(KEY_RSSI, calibration.getRSSI());
        database.insert(TABLE_CALIBRATIONS, null, values);
        database.close();
    }

    public ArrayList<Room> getAllRooms() {
        ArrayList<Room> rooms = new ArrayList<Room>();
        String query = "SELECT * FROM "+TABLE_ROOMS;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                rooms.add(new Room(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return rooms;
    }

    public void loadAllBeacons(ArrayList<Room> rooms){
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT "+TABLE_BEACONS+"."+KEY_ID+", "+TABLE_BEACONS+"."+KEY_NAME+
                ", "+TABLE_BEACONS+"."+KEY_MAC+", "+TABLE_CALIBRATIONS+"."+KEY_RSSI+
                " FROM "+TABLE_BEACONS+" INNER JOIN "+TABLE_CALIBRATIONS+
                " ON ("+TABLE_BEACONS+"."+KEY_ID+" = "+TABLE_CALIBRATIONS+"."+KEY_BEACONID+
                ") WHERE "+TABLE_CALIBRATIONS+"."+KEY_ROOMID+" = ?";
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            Cursor cursor = database.rawQuery(query, new String[]{Integer.toString(room.getID())});
            if (cursor.moveToFirst()){
                do{
                    room.getBeacons().add(new Beacon(cursor.getInt(0), cursor.getString(1),
                            cursor.getString(2), loadRSSIS(cursor.getString(3))));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
    }

    private ArrayList<Byte> loadRSSIS(String rssiArray){
        if (rssiArray == null){
            return new ArrayList<Byte>();
        }
        String onlyRSSI = rssiArray.replaceAll("[\\[\\]\\^]", "");
        String[] RSSIS = onlyRSSI.split(", ");
        ArrayList<Byte> arrays = new ArrayList<Byte>();
        for (String rssi : RSSIS) {
            byte lastrssi = Byte.parseByte(rssi);
            arrays.add(lastrssi);
        }
        return arrays;
    }

    public int getCalibrationID(Calibration calibration){
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT "+KEY_ID+" FROM "+TABLE_CALIBRATIONS+
                " WHERE "+KEY_ROOMID+" = ? AND "+KEY_BEACONID+" = ?";
        Cursor cursor = database.rawQuery(query,
                new String[]{String.valueOf(calibration.getRoomID()),
                        String.valueOf(calibration.getBeaconID())});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
            cursor.close();
        }
        database.close();
        return id;
    }

    public int updateCalibration(Calibration calibration){
        int id = getCalibrationID(calibration);
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RSSI, calibration.getRSSI());
        int i = database.update(TABLE_CALIBRATIONS, values, KEY_ID+" = ?",
                new String[] { String.valueOf(id)});
        database.close();
        return i;
    }

    public void deleteRoom(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_ROOMS, KEY_ID+" = ?", new String[]{String.valueOf(id)});
        database.close();
    }

    public void deleteBeacon(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_BEACONS, KEY_ID+" = ?", new String[]{String.valueOf(id)});
        database.close();
    }

    public void deleteBeacons(ArrayList<Integer> ids) {
        SQLiteDatabase database = this.getWritableDatabase();
        if (!ids.isEmpty()){
            for (int i = 0; i < ids.size(); i++){
                database.delete(TABLE_BEACONS, KEY_ID + " = ?", new String[]{String.valueOf(ids.get(i))});
            }
            database.close();
        }
        /*String query = "DELETE FROM "+TABLE_BEACONS+" WHERE "+KEY_ID+" IN ";
        String list = "";
        if (!ids.isEmpty()){
            for (int i = 0; i < ids.size(); i++){
                if (i != 0){ list += ", "+ids.get(i);}
                else { list += ids.get(i); }
            }
            list = "("+list+")";
            //database.delete(TABLE_BEACONS, KEY_ID+" IN ?", new String[]{list});
            database.rawQuery(query+list, null);
            database.close();
        }*/
    }

    public void deleteCalibration(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CALIBRATIONS, KEY_ID+" = ?", new String[]{String.valueOf(id)});
        database.close();
    }

    public void deleteCalibrations(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CALIBRATIONS, KEY_ROOMID+" = ?", new String[]{String.valueOf(id)});
        database.close();
    }

    public void clearDB(){
        deleteTable(TABLE_ROOMS);
        deleteTable(TABLE_BEACONS);
        deleteTable(TABLE_CALIBRATIONS);
    }

    public void deleteTable(String table){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table, null, null);
    }
}