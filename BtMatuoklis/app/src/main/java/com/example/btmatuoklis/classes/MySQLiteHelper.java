package com.example.btmatuoklis.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CalibrationDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        // SQL statement to create table
        String CREATE_ROOMS_TABLE =
                "CREATE TABLE rooms (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)";

        // create table
        database.execSQL(CREATE_ROOMS_TABLE);

        // SQL statement to create table
        String CREATE_BEACONS_TABLE =
                "CREATE TABLE beacons (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, mac TEXT)";

        // create table
        database.execSQL(CREATE_BEACONS_TABLE);

        String CREATE_CALIBRATIONS_TABLE =
                "CREATE TABLE calibrations (id INTEGER PRIMARY KEY AUTOINCREMENT, roomid INTEGER, beaconid INTEGER, rssi STRING)";

        database.execSQL(CREATE_CALIBRATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // Drop older table if existed
        database.execSQL("DROP TABLE IF EXISTS rooms");
        database.execSQL("DROP TABLE IF EXISTS beacons");
        database.execSQL("DROP TABLE IF EXISTS calibrations");
        // create fresh table
        this.onCreate(database);
    }
    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete)  + get all  + delete all
     */

    // Table name
    private static final String TABLE_ROOMS = "rooms";
    private static final String TABLE_BEACONS = "beacons";
    private static final String TABLE_CALIBRATIONS = "calibrations";

    // Common Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    // Beacons Table columns names
    private static final String KEY_MAC = "mac";

    // Calibrations Table columns names
    private static final String KEY_ROOMID = "roomid";
    private static final String KEY_BEACONID = "beaconid";
    private static final String KEY_RSSI = "rssi";


    private static final String[] ROOMSCOLUMNS = {KEY_ID,KEY_NAME};
    private static final String[] BEACONSCOLUMNS = {KEY_ID,KEY_NAME,KEY_MAC};
    private static final String[] CALIBRATIONSCOLUMNS = {KEY_ID,KEY_ROOMID,KEY_BEACONID,KEY_RSSI};



    public void addRoom(Room room){
        Log.d("addRoom", room.toString());
        // 1. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, room.getName()); // get title

        // 3. insert
        database.insert(TABLE_ROOMS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        database.close();
    }

    public void addBeacon(Beacon beacon){
        Log.d("addBeacon", beacon.toString());
        // 1. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, beacon.getName()); // get title
        values.put(KEY_MAC, beacon.getMAC());

        // 3. insert
        database.insert(TABLE_BEACONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        database.close();
    }

    public void addCalibration(Calibration calibration){
        Log.d("addCalibration", calibration.toString());
        // 1. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ROOMID, calibration.getRoomID()); // get title
        values.put(KEY_BEACONID, calibration.getBeaconID());
        values.put(KEY_RSSI, calibration.getRSSI());

        // 3. insert
        database.insert(TABLE_CALIBRATIONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        database.close();
    }



    public Room getRoom(int id){

        // 1. get reference to readable DB
        SQLiteDatabase database = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                database.query(TABLE_ROOMS, // a. table
                        ROOMSCOLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build object
        Room room = new Room();
        room.setID(Integer.parseInt(cursor.getString(0)));
        room.setName(cursor.getString(1));

        Log.d("getRoom(" + id + ")", room.toString());

        // 5. return
        return room;
    }

    public Beacon getBeacon(int id){

        // 1. get reference to readable DB
        SQLiteDatabase database = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                database.query(TABLE_BEACONS, // a. table
                        BEACONSCOLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build object
        Beacon beacon = new Beacon(cursor.getString(1), cursor.getString(2));
        beacon.setID(Integer.parseInt(cursor.getString(0)));

        Log.d("getBeacon(" + id + ")", beacon.toString());

        // 5. return
        return beacon;
    }

    public Calibration getCalibration(int id){

        // 1. get reference to readable DB
        SQLiteDatabase database = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                database.query(TABLE_CALIBRATIONS, // a. table
                        CALIBRATIONSCOLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build object
        Calibration calibration = new Calibration();
        calibration.setID(Integer.parseInt(cursor.getString(0)));
        calibration.setRoomID(Integer.parseInt(cursor.getString(1)));
        calibration.setBeaconID(Integer.parseInt(cursor.getString(2)));
        calibration.setRSSI(cursor.getString(3));

        Log.d("getCalibration(" + id + ")", calibration.toString());

        // 5. return
        return calibration;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new LinkedList<Room>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ROOMS;

        // 2. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        // 3. go over each row, build room and add it to list
        Room room = null;
        if (cursor.moveToFirst()) {
            do {
                room = new Room();
                room.setID(Integer.parseInt(cursor.getString(0)));
                room.setName(cursor.getString(1));

                // Add room to rooms
                rooms.add(room);
            } while (cursor.moveToNext());
        }
        return rooms;
    }

    public List<Beacon> getAllBeacons() {
        List<Beacon> beacons = new LinkedList<Beacon>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BEACONS;

        // 2. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        // 3. go over each row, build room and add it to list
        Beacon beacon = null;
        if (cursor.moveToFirst()) {
            do {
                beacon = new Beacon(cursor.getString(1), cursor.getString(2));
                beacon.setID(Integer.parseInt(cursor.getString(0)));

                beacons.add(beacon);
            } while (cursor.moveToNext());
        }
        return beacons;
    }

    public List<Calibration> getAllCalibrations() {
        List<Calibration> calibrations = new LinkedList<Calibration>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_CALIBRATIONS;

        // 2. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        // 3. go over each row, build room and add it to list
        Calibration calibration = null;
        if (cursor.moveToFirst()) {
            do {
                calibration = new Calibration();
                calibration.setID(Integer.parseInt(cursor.getString(0)));
                calibration.setRoomID(Integer.parseInt(cursor.getString(1)));
                calibration.setBeaconID(Integer.parseInt(cursor.getString(2)));
                calibration.setRSSI(cursor.getString(3));

                calibrations.add(calibration);
            } while (cursor.moveToNext());
        }
        return calibrations;
    }

    // Updating single room
    public int updateRoom(Room room) {

        // 1. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("name", room.getName());

        // 3. updating row
        int i = database.update(TABLE_ROOMS, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(room.getID())}); //selection args

        // 4. close
        database.close();

        return i;
    }

    public int getCalibrationID(Calibration calibration){
        SQLiteDatabase database = this.getReadableDatabase();
        /*Cursor cursor =
                database.query(TABLE_CALIBRATIONS, // a. table
                        CALIBRATIONSCOLUMNS, // b. column names
                        KEY_ROOMID + " = ? AND " + KEY_BEACONID + " = ?", // c. selections
                        new String[]{String.valueOf(calibration.getRoomID()), String.valueOf(calibration.getBeaconID())}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit*/
        String query = "SELECT "+KEY_ID+" FROM "+TABLE_CALIBRATIONS+" WHERE "+KEY_ROOMID+" = "+
                String.valueOf(calibration.getRoomID())+" AND "+KEY_BEACONID+" = "+String.valueOf(calibration.getBeaconID());
        Cursor cursor = database.rawQuery(query, null);
        int id = -1;
        if (cursor.moveToNext()) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }
        cursor.close();
        database.close();
        return id;
    }

    public int getBeaconID(String mac){
        SQLiteDatabase database = this.getReadableDatabase();
        /*Cursor cursor =
                database.query(TABLE_CALIBRATIONS, // a. table
                        CALIBRATIONSCOLUMNS, // b. column names
                        KEY_ROOMID + " = ? AND " + KEY_BEACONID + " = ?", // c. selections
                        new String[]{String.valueOf(calibration.getRoomID()), String.valueOf(calibration.getBeaconID())}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit*/
        String query = "SELECT "+KEY_ID+" FROM "+TABLE_BEACONS+" WHERE "+KEY_MAC+" = "+mac;
        Cursor cursor = database.rawQuery(query, null);
        int id = -1;
        if (cursor.moveToNext()) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }
        cursor.close();
        database.close();
        return id;
    }

    public int updateCalibration(Calibration calibration){
        int id = getCalibrationID(calibration);
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rssi", calibration.getRSSI());
        int i = database.update(TABLE_CALIBRATIONS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(id)}); //selection args
        database.close();
        return i;
    }

    // Deleting single room
    public void deleteRoom(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();

        // 2. delete
        database.delete(TABLE_ROOMS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        // 3. close
        database.close();
    }

    public void deleteBeacon(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();

        // 2. delete
        database.delete(TABLE_BEACONS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        // 3. close
        database.close();
    }

    public void deleteCalibration(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase database = this.getWritableDatabase();

        // 2. delete
        database.delete(TABLE_CALIBRATIONS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        // 3. close
        database.close();
    }

    public int getCount(String table) {

        SQLiteDatabase database = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + table;
        Cursor cursor = database.rawQuery(countQuery, null);
        Log.i("Number of Records", " :: " + cursor.getCount());

        cursor.close();
        // return count
        return cursor.getCount();
    }

    public void deleteAll(String table){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table, null, null);
    }

    public void closeDB() {
        SQLiteDatabase database = this.getReadableDatabase();
        if (database != null && database.isOpen())
            database.close();
    }
}