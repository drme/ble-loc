package com.example.btmatuoklis.classes;

/**
 * Created by sauli_000 on 2016-02-08.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "CalibrationDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL statement to create table
        String CREATE_ROOMS_TABLE = "CREATE TABLE rooms ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT )";

        // create table
        db.execSQL(CREATE_ROOMS_TABLE);

        // SQL statement to create table
        String CREATE_BEACONS_TABLE = "CREATE TABLE beacons ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "mac TEXT )";

        // create table
        db.execSQL(CREATE_BEACONS_TABLE);

        String CREATE_CALIBRATIONS_TABLE = "CREATE TABLE calibrations ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "roomid INTEGER, "+
                "beaconid INTEGER, "+
                "rssi STRING )";

        db.execSQL(CREATE_CALIBRATIONS_TABLE);
    }

    String uzklausaSurinkimui = "SELECT rooms.name AS RoomName, beacons.name AS BeaconName," +
            "beacons.mac AS BeaconMac, calibrations.rssi AS RSSI " +
            "FROM calibrations " +
            "JOIN rooms ON (calibrations.roomid = rooms.id)" +
            "JOIN beacons ON (calibrations.beaconid = beacons.id)" +
            "WHERE roomid=1";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS rooms");
        db.execSQL("DROP TABLE IF EXISTS beacons");
        db.execSQL("DROP TABLE IF EXISTS calibrations");
        // create fresh table
        this.onCreate(db);
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
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, room.getName()); // get title

        // 3. insert
        db.insert(TABLE_ROOMS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void addBeacon(Beacon beacon){
        Log.d("addBeacon", beacon.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, beacon.getName()); // get title
        values.put(KEY_MAC, beacon.getMAC());

        // 3. insert
        db.insert(TABLE_BEACONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void addCalibration(Calibration calibration){
        Log.d("addCalibration", calibration.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ROOMID, calibration.getRoomId()); // get title
        values.put(KEY_BEACONID, calibration.getBeaconId());
        values.put(KEY_RSSI, calibration.getRSSI());

        // 3. insert
        db.insert(TABLE_CALIBRATIONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }



    public Room getRoom(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_ROOMS, // a. table
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
        room.setId(Integer.parseInt(cursor.getString(0)));
        room.setName(cursor.getString(1));

        Log.d("getRoom(" + id + ")", room.toString());

        // 5. return
        return room;
    }

    public Beacon getBeacon(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_BEACONS, // a. table
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
        beacon.setId(Integer.parseInt(cursor.getString(0)));

        Log.d("getBeacon(" + id + ")", beacon.toString());

        // 5. return
        return beacon;
    }

    public Calibration getCalibration(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_CALIBRATIONS, // a. table
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
        calibration.setId(Integer.parseInt(cursor.getString(0)));
        calibration.setRoomId(Integer.parseInt(cursor.getString(1)));
        calibration.setBeaconId(Integer.parseInt(cursor.getString(2)));
        calibration.setRSSI(cursor.getString(3));

        Log.d("getCalibration(" + id + ")", calibration.toString());

        // 5. return
        return calibration;
    }

    // Get All
    public void atrinkti(int id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(uzklausaSurinkimui, null);
        cursor.moveToFirst();

    }

    public List<Selected> getAll() {
        List<Selected> selecteds = new LinkedList<Selected>();

        // 1. build the query
        String query = "SELECT rooms.name AS RoomName, beacons.name AS BeaconName," +
                "beacons.mac AS BeaconMac, calibrations.rssi AS RSSI " +
                "FROM calibrations " +
                "JOIN rooms ON (calibrations.roomid = rooms.id)" +
                "JOIN beacons ON (calibrations.beaconid = beacons.id)" +
                "WHERE roomid=4";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build room and add it to list
        Selected selected = null;
        if (cursor.moveToFirst()) {
            do {
                selected = new Selected();
                selected.setRoomName(cursor.getString(0));
                selected.setBeaconName(cursor.getString(1));
                selected.setBeaconName(cursor.getString(2));
                selected.setRSSI(cursor.getString(3));
                // Add room to rooms
                selecteds.add(selected);
            } while (cursor.moveToNext());
        }

        Log.d("getAll()", selecteds.toString());

        // return rooms
        return selecteds;
    }


    public List<Room> getAllRooms() {
        List<Room> rooms = new LinkedList<Room>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ROOMS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build room and add it to list
        Room room = null;
        if (cursor.moveToFirst()) {
            do {
                room = new Room();
                room.setId(Integer.parseInt(cursor.getString(0)));
                room.setName(cursor.getString(1));

                // Add room to rooms
                rooms.add(room);
            } while (cursor.moveToNext());
        }

        Log.d("getAllRooms()", rooms.toString());

        // return rooms
        return rooms;
    }

    public List<Beacon> getAllBeacons() {
        List<Beacon> beacons = new LinkedList<Beacon>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BEACONS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build room and add it to list
        Beacon beacon = null;
        if (cursor.moveToFirst()) {
            do {
                beacon = new Beacon(cursor.getString(1), cursor.getString(2));
                beacon.setId(Integer.parseInt(cursor.getString(0)));

                beacons.add(beacon);
            } while (cursor.moveToNext());
        }
        Log.d("getAllBeacons()", beacons.toString());
        return beacons;
    }

    public List<Calibration> getAllCalibrations() {
        List<Calibration> calibrations = new LinkedList<Calibration>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_CALIBRATIONS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build room and add it to list
        Calibration calibration = null;
        if (cursor.moveToFirst()) {
            do {
                calibration = new Calibration();
                calibration.setId(Integer.parseInt(cursor.getString(0)));
                calibration.setRoomId(Integer.parseInt(cursor.getString(1)));
                calibration.setBeaconId(Integer.parseInt(cursor.getString(2)));
                calibration.setRSSI(cursor.getString(3));

                calibrations.add(calibration);
            } while (cursor.moveToNext());
        }

        Log.d("getAllCalibrations()", calibrations.toString());

        return calibrations;
    }




    // Updating single room
    public int updateRoom(Room room) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("name", room.getName());

        // 3. updating row
        int i = db.update(TABLE_ROOMS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(room.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single room
    public void deleteRoom(Room room) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_ROOMS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(room.getId())});

        // 3. close
        db.close();

        Log.d("deleteRoom", room.toString());

    }


    public int getCount(String table) {

        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + table;
        Cursor cursor = db.rawQuery(countQuery, null);
        Log.i("Number of Records", " :: " + cursor.getCount());

        cursor.close();
        // return count
        return cursor.getCount();
    }



    public void deleteAll(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table,null,null);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}

