package com.morgenworks.alchemistutil;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

/**
 * This is Created by wizard on 7/21/16.
 */
public class SnappyDBHelper {
    private static String TAG = SnappyDB.class.getName();

    public interface SnappyDBUpdateOperation {
        void update(DB snappydb) throws SnappydbException;
    }

    public interface SnappyDBGetOperation {
        <T> T getFromDB(DB snappydb) throws SnappydbException;
    }

    /**
     * @param context Context
     * @param dbName String
     * @param getOperation SnappyDBGetOption
     * @param <T> ObjectType
     * @return ObjectType, Array, List also are ok
     */
    public static <T> T snappydbExecute(Context context, String dbName, SnappyDBGetOperation getOperation) {
        DB snappydb = null;

        try {
            snappydb = DBFactory.open(context, dbName);

            T result = getOperation.getFromDB(snappydb);

            snappydb.close();

            return result;

        } catch (SnappydbException e) {

            Log.e(TAG, e.getLocalizedMessage());

            try {
                if (snappydb != null && snappydb.isOpen() ) {
                    snappydb.close();
                }
            } catch (SnappydbException e1) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        return null;
    }

    /**
     * @param context Context
     * @param dbName String
     * @param updateOperation SnappyDBUpdateOperation
     */
    public static void snappydbExecute(Context context, String dbName, SnappyDBUpdateOperation updateOperation) {
        DB snappydb = null;

        try {
            snappydb = DBFactory.open(context, dbName);

            updateOperation.update(snappydb);

            snappydb.close();

        } catch (SnappydbException e) {
            Log.e(TAG, e.getLocalizedMessage());

            try {
                if (snappydb != null && snappydb.isOpen() ) {
                    snappydb.close();
                }
            } catch (SnappydbException e1) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    public static String getKeyFromId(String prefix, String id) {
        return prefix + id;
    }


}
