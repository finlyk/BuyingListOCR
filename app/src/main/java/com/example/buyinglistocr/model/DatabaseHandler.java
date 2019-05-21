package com.example.buyinglistocr.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Allow the management of the database
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    /***** List *****/

    // "List" table
    public static final String LIST_TABLE_NAME = "List";

    // Attributes of "List" table
    public static final String LIST_KEY = "id";
    public static final String LIST_NAME = "name";
    public static final String LIST_SPENT = "spent";

    // SQL request for the creation of the "List" table
    public static final String LIST_TABLE_CREATE = "CREATE TABLE " + LIST_TABLE_NAME + " (" +
                                                    LIST_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                    LIST_NAME + " TEXT, " +
                                                    LIST_SPENT + " REAL);";

    // SQL request for the update of the "List" table
    public static final String LIST_TABLE_DROP = "DROP TABLE IF EXISTS " + LIST_TABLE_NAME + ";";

    /***** Item *****/

    // "Item" table
    public static final String ITEM_TABLE_NAME = "Item";

    // Attributes of "Item" table
    public static final String ITEM_KEY = "id";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_QUANTITY_DESIRED = "quantityDesired";
    public static final String ITEM_QUANTITY_GOT = "quantityGot";
    public static final String ITEM_NOTE = "note";
    public static final String ITEM_STATUS = "status";
    public static final String ITEM_KEY_LIST = "idList";

    // SQL request for the creation of the "Item" table
    public static final String ITEM_TABLE_CREATE = "CREATE TABLE " + ITEM_TABLE_NAME + " (" +
                                                    ITEM_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                    ITEM_NAME + " TEXT, " +
                                                    ITEM_QUANTITY_DESIRED + " INTEGER, " +
                                                    ITEM_QUANTITY_GOT + " INTEGER, " +
                                                    ITEM_NOTE + " TEXT, " +
                                                    ITEM_STATUS + " INTEGER, " +
                                                    ITEM_KEY_LIST + " INTEGER);";

    // SQL request for the update of the "Item" table
    public static final String ITEM_TABLE_DROP = "DROP TABLE IF EXISTS " + ITEM_TABLE_NAME + ";";

    /***** Shop *****/

    // "Shop" table
    public static final String SHOP_TABLE_NAME = "Shop";

    // Attributes of "Shop" table
    public static final String SHOP_KEY = "id";
    public static final String SHOP_NAME = "name";

    // SQL request for the creation of the "Shop" table
    public static final String SHOP_TABLE_CREATE = "CREATE TABLE " + SHOP_TABLE_NAME + " (" +
                                                    SHOP_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                    SHOP_NAME + " TEXT);";

    // SQL request for the update of the "Shop" table
    public static final String SHOP_TABLE_DROP = "DROP TABLE IF EXISTS " + SHOP_TABLE_NAME + ";";

    /***** Product *****/

    // "Product" table
    public static final String PRODUCT_TABLE_NAME = "Product";

    // Attributes of "Product" table
    public static final String PRODUCT_KEY = "id";
    public static final String PRODUCT_NAME = "name";
    public static final String PRODUCT_TYPE = "type";
    public static final String PRODUCT_PRICE = "price";
    public static final String PRODUCT_KEY_SHOP = "idShop";

    // SQL request for the creation of the "Product" table
    public static final String PRODUCT_TABLE_CREATE = "CREATE TABLE " + PRODUCT_TABLE_NAME + " (" +
                                                   PRODUCT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                   PRODUCT_NAME + " TEXT, " +
                                                   PRODUCT_TYPE + " INTEGER, " +
                                                   PRODUCT_PRICE + " INTEGER, " +
                                                   PRODUCT_KEY_SHOP + " INTEGER);";

    // SQL request for the update of the "Product" table
    public static final String PRODUCT_TABLE_DROP = "DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME+ ";";

    /**
     * The constructor of the class
     * @param context - The context
     * @param name - The name of the database
     * @param factory - The factory
     * @param version - The version of the database
     */
    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);

    }

    /**
     * Allow the creation of the "List" table
     * @param db - The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(LIST_TABLE_CREATE);
        db.execSQL(ITEM_TABLE_CREATE);
        db.execSQL(SHOP_TABLE_CREATE);
        db.execSQL(PRODUCT_TABLE_CREATE);

    }

    /**
     * Allow the update of the "List" table
     * @param db - The databse
     * @param oldVersion - The number of the old version
     * @param newVersion - The number of the new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(LIST_TABLE_DROP);
        db.execSQL(ITEM_TABLE_DROP);
        db.execSQL(SHOP_TABLE_DROP);
        db.execSQL(PRODUCT_TABLE_DROP);
        onCreate(db);

    }

}