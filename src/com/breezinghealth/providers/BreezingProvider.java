package com.breezinghealth.providers;


import com.breezinghealth.providers.Breezing.Account;
import com.breezinghealth.providers.Breezing.EnergyCost;
import com.breezinghealth.providers.Breezing.HeatConsumption;
import com.breezinghealth.providers.Breezing.HeatIngestion;
import com.breezinghealth.providers.Breezing.Information;
import com.breezinghealth.providers.Breezing.Ingestion;
import com.breezinghealth.providers.Breezing.WeightChange;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BreezingProvider extends  SQLiteContentProvider {
    private final static String TAG = "BreezingProvider";
    public  static String TABLE_ACCOUNT = "account";
    public  static String TABLE_INFORMATION = "information";
    public  static String TABLE_COST = "cost";
    public  static String TABLE_INGESTION = "ingestion";
    public  static String TABLE_WEIGHT = "weight";
    public  static String TABLE_HEAT_CONSUMPTION  = "heat_consumption";
    public  static String TABLE_HEAT_INGESTION = "heat_ingestion";

    private SQLiteOpenHelper mOpenHelper;

    @Override
    public String getType(Uri url) {
        Log.d(TAG, " getType uri =   " + url);
        // Generate the body of the query.
        int match = sURLMatcher.match(url);
        switch (match) {
            case BREEZING_ACCOUNT:
                return Account.CONTENT_TYPE;
            case BREEZING_ACCOUNT_ID:
                return Account.CONTENT_ITEM_TYPE;
            case BREEZING_INFORMATION:
                return Information.CONTENT_TYPE;
            case BREEZING_INFORMATION_ID:
                return Information.CONTENT_ITEM_TYPE;
            case BREEZING_COST:
                return EnergyCost.CONTENT_TYPE;
            case BREEZING_COST_ID:
                return EnergyCost.CONTENT_ITEM_TYPE;
            case BREEZING_INGESTION:
                return Ingestion.CONTENT_TYPE;
            case BREEZING_INGESTION_ID:
                return Ingestion.CONTENT_ITEM_TYPE;
            case BREEZING_WEIGHT:
                return WeightChange.CONTENT_TYPE;
            case BREEZING_WEIGHT_ID:
                return WeightChange.CONTENT_ITEM_TYPE;
            case BREEZING_HEAT_CONSUMPTION:
                return HeatConsumption.CONTENT_TYPE;
            case BREEZING_HEAT_CONSUMPTION_ID:
                return HeatConsumption.CONTENT_ITEM_TYPE;
            case BREEZING_HEAT_INGESTION:
                return HeatIngestion.CONTENT_TYPE;
            case BREEZING_HEAT_INGESTION_ID:
                return HeatIngestion.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    private void notifyChange(Uri uri) {
        ContentResolver cr = getContext().getContentResolver();
        cr.notifyChange(uri, null);
    }

    @Override
    public boolean onCreate() {
        super.onCreate();
        try {
            return initialize();
        } catch (RuntimeException e) {
            Log.e(TAG, "Cannot start provider", e);
            return false;
        }
    }

    private boolean initialize() {
        final Context context = getContext();
        mOpenHelper = getDatabaseHelper();
        mDb = mOpenHelper.getWritableDatabase();
        return (mDb != null);
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        // Generate the body of the query.
        int match = sURLMatcher.match(url);
        switch (match) {
            case BREEZING_ACCOUNT:
                qb.setTables(TABLE_ACCOUNT);
                break;
            case BREEZING_ACCOUNT_ID:
                qb.setTables(TABLE_ACCOUNT);
                qb.appendWhere("(_id = " + url.getPathSegments().get(1) + ")");
                break;
            case BREEZING_INFORMATION:
                qb.setTables(TABLE_INFORMATION);
                break;
            case BREEZING_INFORMATION_ID:
                qb.setTables(TABLE_INFORMATION);
                qb.appendWhere("(_id = " + url.getPathSegments().get(1) + ")");
                break;
            case BREEZING_COST:
                qb.setTables(TABLE_COST);
                break;
            case BREEZING_COST_ID:
                qb.setTables(TABLE_COST);
                qb.appendWhere("(_id = " + url.getPathSegments().get(1) + ")");
                break;
            case BREEZING_INGESTION:
                qb.setTables(TABLE_INGESTION);
                break;
            case BREEZING_INGESTION_ID:
                qb.setTables(TABLE_INGESTION);
                qb.appendWhere("(_id = " + url.getPathSegments().get(1) + ")");
                break;
            case BREEZING_WEIGHT:
                qb.setTables(TABLE_WEIGHT);
                break;
            case BREEZING_WEIGHT_ID:
                qb.setTables(TABLE_WEIGHT);
                qb.appendWhere("(_id = " + url.getPathSegments().get(1) + ")");
                break;
            case BREEZING_HEAT_CONSUMPTION:
                qb.setTables(TABLE_HEAT_CONSUMPTION);
                break;
            case BREEZING_HEAT_CONSUMPTION_ID:
                qb.setTables(TABLE_HEAT_CONSUMPTION);
                qb.appendWhere("(_id = " + url.getPathSegments().get(1) + ")");
                break;
            case BREEZING_HEAT_INGESTION:
                qb.setTables(TABLE_HEAT_INGESTION);
                break;
            case BREEZING_HEAT_INGESTION_ID:
                qb.setTables(TABLE_HEAT_INGESTION);
                qb.appendWhere("(_id = " + url.getPathSegments().get(1) + ")");
                break;
            default:
                Log.e(TAG, "query: invalid request: " + url);
                return null;
        }

        String finalSortOrder = null;
        if (TextUtils.isEmpty(sort)) {
            if (qb.getTables().equals(TABLE_COST)) {
                finalSortOrder = Breezing.EnergyCost.DEFAULT_SORT_ORDER;
            } else if (qb.getTables().equals(TABLE_INGESTION)) {
                finalSortOrder = Breezing.Ingestion.DEFAULT_SORT_ORDER;
            } else if (qb.getTables().equals(TABLE_WEIGHT)) {
                finalSortOrder = Breezing.WeightChange.DEFAULT_SORT_ORDER;
            } else if (qb.getTables().equals(TABLE_HEAT_CONSUMPTION)) {
                finalSortOrder = Breezing.HeatConsumption.DEFAULT_SORT_ORDER;
            }
        } else {
            finalSortOrder = sort;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection,
                selectionArgs, null, null, finalSortOrder);

        // TODO: Does this need to be a URI for this provider.
        ret.setNotificationUri(getContext().getContentResolver(), url);
        return ret;
    }

    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        return super.delete(url, where, whereArgs);
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        return super.insert(url, initialValues);
    }

    @Override
    public int update(Uri url, ContentValues values, String where,
            String[] whereArgs) {
        return super.update(url, values, where, whereArgs);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        Log.d(TAG, " applyBatch ");
        return super.applyBatch(operations);
    }

    /***
     * 添加  EnergyCost Ingestion WeightChange 这几个表基本的日期格式
     * @param initialValues
     * @param values
     * @param addDate
     */
    private ContentValues addDateFormat(ContentValues initialValues, ContentValues values, boolean addDate) {
        int date = simpleDateFormat("yyyyMMdd");

        if (addDate) {
            values.put(Breezing.BaseDateColumns.DATE, date);
        }

        int year = simpleDateFormat("yyyy");
        values.put(Breezing.BaseDateColumns.YEAR, year);

        int month = simpleDateFormat("yyyyMM");
        values.put(Breezing.BaseDateColumns.YEAR_MONTH, month);

        int week = simpleDateFormat("yyyyMMww");
        values.put(Breezing.BaseDateColumns.YEAR_WEEK, week);

        return values;
    }

    /***
     * 获得现在日期格式并转化为int类型
     * @param format
     * @return
     */
    private int simpleDateFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String strDate = sdf.format(new Date());
        Log.d(TAG, "format = " + format + " sdf.format(new Date())  = " +  sdf.format(new Date()));
        Date date = null;
        try {
            date =  sdf.parse(strDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d(TAG, " simpleDateFormat date = " + date);
        int intDate = Integer.parseInt(strDate);
        Log.d(TAG, "simpleDateFormat longDate = " + intDate);
        return intDate;
    }



    @Override
    protected SQLiteOpenHelper getDatabaseHelper(Context context) {
        return BreezingDatabaseHelper.getInstance(getContext());
    }

    @Override
    protected Uri insertInTransaction(Uri url, ContentValues initialValues) {
        long rowID = 0;
        int match = sURLMatcher.match(url);

        Log.d(TAG, "Insert uri=" + url + ", match=" + match);


        switch (match) {
            case BREEZING_ACCOUNT:
                rowID = mDb.insert(TABLE_ACCOUNT, Account.ACCOUNT_ID, initialValues);
                break;
            case BREEZING_INFORMATION:
                rowID = mDb.insert(TABLE_INFORMATION, Information.ACCOUNT_ID, initialValues);
                break;
            case BREEZING_COST:
                rowID = insertCostTable(initialValues);
                break;
            case BREEZING_INGESTION:
                rowID = insertIngestionTable(initialValues);
                break;
            case BREEZING_WEIGHT:
                rowID = insertWeightTable(initialValues);
                break;
            case BREEZING_HEAT_CONSUMPTION:
                rowID = insertHeatConsumptionTable(initialValues);
                break;
            case BREEZING_HEAT_INGESTION:
                rowID = mDb.insert(TABLE_HEAT_INGESTION, HeatIngestion.FOOD_TYPE, initialValues);
                break;
            default:
                Log.e(TAG, "insert: invalid request: " + url);
                return null;
        }

        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(url, rowID);

            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.d(TAG, "insert " + uri + " succeeded");
            }
            notifyChange(uri);
            return uri;
        } else {
            Log.e(TAG,"insert: failed! " + initialValues.toString());
        }

        return null;
    }

    private long insertCostTable(ContentValues initialValues) {
        int totalEnergy = 0;
        boolean addDate = false;
        ContentValues values = new ContentValues(initialValues);

        if (!initialValues.containsKey(EnergyCost.DATE)) {
            addDate = true;
        }

        if (initialValues.containsKey(EnergyCost.METABOLISM)) {
            totalEnergy += initialValues.getAsInteger(EnergyCost.METABOLISM);
        }

        if (initialValues.containsKey(EnergyCost.SPORT)) {
            totalEnergy += initialValues.getAsInteger(EnergyCost.SPORT);
        }

        if (initialValues.containsKey(EnergyCost.DIGEST)) {
            totalEnergy += initialValues.getAsInteger(EnergyCost.DIGEST);
        }

        if (initialValues.containsKey(EnergyCost.TRAIN)) {
            totalEnergy += initialValues.getAsInteger(EnergyCost.TRAIN);
        }

        values.put(EnergyCost.TOTAL_ENERGY, totalEnergy);
        values = addDateFormat(initialValues, values, addDate);
        return mDb.insert(TABLE_COST, EnergyCost.ACCOUNT_ID, values);
    }

    private long insertIngestionTable(ContentValues initialValues) {
        int totalIngestion = 0;
        boolean addDate = false;
        ContentValues values = new ContentValues(initialValues);
        if (!initialValues.containsKey(Ingestion.DATE)) {
            addDate = true;
        }

        if (initialValues.containsKey(Ingestion.BREAKFAST)) {
            totalIngestion += initialValues.getAsInteger(Ingestion.BREAKFAST);
        }

        if (initialValues.containsKey(Ingestion.LUNCH)) {
            totalIngestion += initialValues.getAsInteger(Ingestion.LUNCH);
        }

        if (initialValues.containsKey(Ingestion.DINNER)) {
            totalIngestion += initialValues.getAsInteger(Ingestion.DINNER);
        }

        if (initialValues.containsKey(Ingestion.ETC)) {
            totalIngestion += initialValues.getAsInteger(Ingestion.ETC);
        }

        values.put(Breezing.Ingestion.TOTAL_INGESTION, totalIngestion);
        values = addDateFormat(initialValues, values, addDate);

        return mDb.insert(TABLE_INGESTION, Ingestion.ACCOUNT_ID, values);
    }

    private long insertWeightTable(ContentValues initialValues) {
        boolean addDate = false;
        ContentValues values = new ContentValues(initialValues);
        if (!initialValues.containsKey(WeightChange.DATE)) {
            addDate = true;
        }
        values = addDateFormat(initialValues, values, addDate);
        return mDb.insert(TABLE_WEIGHT, WeightChange.ACCOUNT_ID, values);
    }

    private long insertHeatConsumptionTable(ContentValues initialValues) {
        boolean addDate = false;
        ContentValues values = new ContentValues(initialValues);

        if (!initialValues.containsKey(HeatConsumption.DATE)) {
            addDate = true;
        }

        if (addDate) {
            int date = simpleDateFormat("yyyyMMdd");
            values.put(Breezing.BaseDateColumns.DATE, date);
        }

        return mDb.insert(TABLE_HEAT_CONSUMPTION, HeatConsumption.SPORT_TYPE, values);
    }

    @Override
    protected int updateInTransaction(Uri url, ContentValues values, String where,
                String[] whereArgs) {
        int count = 0;
        String table = null;
        String extraWhere = null;

        int match = sURLMatcher.match(url);
        switch (match) {
            case BREEZING_ACCOUNT:
                table = TABLE_ACCOUNT;
                break;
            case BREEZING_ACCOUNT_ID:
                table = TABLE_ACCOUNT;
                extraWhere = "_id=" + url.getPathSegments().get(1);
                break;
            case BREEZING_INFORMATION:
                table = TABLE_INFORMATION;
                break;
            case BREEZING_INFORMATION_ID:
                table = TABLE_INFORMATION;
                extraWhere = "_id=" + url.getPathSegments().get(1);
                break;
            case BREEZING_COST:
                table = TABLE_COST;
                break;
            case BREEZING_COST_ID:
                table = TABLE_COST;
                extraWhere = "_id=" + url.getPathSegments().get(1);
                break;
            case BREEZING_INGESTION:
                table = TABLE_INGESTION;
                break;
            case BREEZING_INGESTION_ID:
                table = TABLE_INGESTION;
                extraWhere = "_id=" + url.getPathSegments().get(1);
                break;
            case BREEZING_WEIGHT:
                table = TABLE_WEIGHT;
                break;
            case BREEZING_WEIGHT_ID:
                table = TABLE_WEIGHT;
                extraWhere = "_id=" + url.getPathSegments().get(1);
                break;
            case BREEZING_HEAT_CONSUMPTION:
                table = TABLE_HEAT_CONSUMPTION;
                break;
            case BREEZING_HEAT_CONSUMPTION_ID:
                table = TABLE_HEAT_CONSUMPTION;
                extraWhere = "_id=" + url.getPathSegments().get(1);
                break;
            case BREEZING_HEAT_INGESTION:
                table = TABLE_HEAT_INGESTION;
                break;
            case BREEZING_HEAT_INGESTION_ID:
                table = TABLE_HEAT_INGESTION;
                extraWhere = "_id=" + url.getPathSegments().get(1);
                break;
            default:
                throw new UnsupportedOperationException(
                        "URI " + url + " not supported");
        }

        if (extraWhere != null) {
            where = DatabaseUtils.concatenateWhere(where, extraWhere);
        }

        Log.d(TAG, "update where = " + where);
        count = mDb.update(table, values, where, whereArgs);

        if (count > 0) {
            Log.d(TAG, "update " + url + " succeeded");
            notifyChange(url);
        }

        return count;
    }

    @Override
    protected int deleteInTransaction(Uri url, String where,
            String[] whereArgs) {
        int count = 0;
        int match = sURLMatcher.match(url);


        switch (match) {
            case BREEZING_ACCOUNT:
                count = mDb.delete(TABLE_ACCOUNT, where, whereArgs);
                break;
            case BREEZING_ACCOUNT_ID:
                int accountId;

                try {
                    accountId = Integer.parseInt(url.getPathSegments().get(1));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Bad message id: " + url.getPathSegments().get(1));
                }

                where = DatabaseUtils.concatenateWhere("_id = " + accountId, where);
                count = mDb.delete(TABLE_ACCOUNT, where, whereArgs);
                break;
            case BREEZING_INFORMATION:
                count = mDb.delete(TABLE_INFORMATION, where, whereArgs);
                break;
            case BREEZING_INFORMATION_ID:
                int informationId = 0;

                try {
                    informationId = Integer.parseInt(url.getPathSegments().get(1));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Bad message id: " + url.getPathSegments().get(1));
                }

                where = DatabaseUtils.concatenateWhere("_id = " + informationId, where);
                count = mDb.delete(TABLE_INFORMATION, where, whereArgs);
                break;
            case BREEZING_COST:
                count = mDb.delete(TABLE_COST, where, whereArgs);
                break;
            case BREEZING_COST_ID:
                int costId;

                try {
                    costId = Integer.parseInt(url.getPathSegments().get(1));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Bad message id: " + url.getPathSegments().get(1));
                }

                where = DatabaseUtils.concatenateWhere("_id = " + costId, where);
                count = mDb.delete(TABLE_COST, where, whereArgs);
                break;
            case BREEZING_INGESTION:
                count = mDb.delete(TABLE_INGESTION, where, whereArgs);
                break;
            case BREEZING_INGESTION_ID:
                int ingestionId;

                try {
                    ingestionId = Integer.parseInt(url.getPathSegments().get(1));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Bad message id: " + url.getPathSegments().get(1));
                }

                where = DatabaseUtils.concatenateWhere("_id = " + ingestionId, where);
                count = mDb.delete(TABLE_INGESTION, where, whereArgs);
                break;
            case BREEZING_WEIGHT:
                count = mDb.delete(TABLE_WEIGHT, where, whereArgs);
                break;
            case BREEZING_WEIGHT_ID:
                int weightId;

                try {
                    weightId = Integer.parseInt(url.getPathSegments().get(1));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Bad message id: " + url.getPathSegments().get(1));
                }

                where = DatabaseUtils.concatenateWhere("_id = " + weightId, where);
                count = mDb.delete(TABLE_WEIGHT, where, whereArgs);
                break;
            case BREEZING_HEAT_CONSUMPTION:
                count = mDb.delete(TABLE_HEAT_CONSUMPTION, where, whereArgs);
                break;
            case BREEZING_HEAT_CONSUMPTION_ID:
                int consumptionId;
                try {
                    consumptionId = Integer.parseInt(url.getPathSegments().get(1));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Bad message id: " + url.getPathSegments().get(1));
                }

                where = DatabaseUtils.concatenateWhere("_id = " + consumptionId, where);
                count = mDb.delete(TABLE_HEAT_CONSUMPTION, where, whereArgs);
                break;
            case BREEZING_HEAT_INGESTION:
                count = mDb.delete(TABLE_HEAT_INGESTION, where, whereArgs);
                break;
            case BREEZING_HEAT_INGESTION_ID:
                int heatIngestion;
                try {
                    heatIngestion = Integer.parseInt(url.getPathSegments().get(1));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Bad message id: " + url.getPathSegments().get(1));
                }

                where = DatabaseUtils.concatenateWhere("_id = " + heatIngestion, where);
                count = mDb.delete(TABLE_HEAT_INGESTION, where, whereArgs);
                break;
            default:
                Log.e(TAG, "query: invalid request: " + url);
        }

        return count;
    }

    @Override
    protected void notifyChange() {
        ContentResolver cr = getContext().getContentResolver();
        //cr.notifyChange(uri, null);
    }

    private static final int BREEZING_ACCOUNT = 1;
    private static final int BREEZING_ACCOUNT_ID = 2;
    private static final int BREEZING_INFORMATION = 3;
    private static final int BREEZING_INFORMATION_ID = 4;
    private static final int BREEZING_COST = 5;
    private static final int BREEZING_COST_ID = 6;
    private static final int BREEZING_INGESTION = 7;
    private static final int BREEZING_INGESTION_ID = 8;
    private static final int BREEZING_WEIGHT = 9;
    private static final int BREEZING_WEIGHT_ID = 10;
    private static final int BREEZING_HEAT_CONSUMPTION = 11;
    private static final int BREEZING_HEAT_CONSUMPTION_ID = 12;
    private static final int BREEZING_HEAT_INGESTION = 13;
    private static final int BREEZING_HEAT_INGESTION_ID = 14;

    private static final UriMatcher sURLMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI(Breezing.AUTHORITY, "account", BREEZING_ACCOUNT);
        sURLMatcher.addURI(Breezing.AUTHORITY, "account/#", BREEZING_ACCOUNT_ID);
        sURLMatcher.addURI(Breezing.AUTHORITY, "information", BREEZING_INFORMATION);
        sURLMatcher.addURI(Breezing.AUTHORITY, "information/#", BREEZING_INFORMATION_ID);
        sURLMatcher.addURI(Breezing.AUTHORITY, "cost", BREEZING_COST);
        sURLMatcher.addURI(Breezing.AUTHORITY, "cost/#", BREEZING_COST_ID);
        sURLMatcher.addURI(Breezing.AUTHORITY, "ingestion", BREEZING_INGESTION);
        sURLMatcher.addURI(Breezing.AUTHORITY, "ingestion/#", BREEZING_INGESTION_ID);
        sURLMatcher.addURI(Breezing.AUTHORITY, "weight", BREEZING_WEIGHT);
        sURLMatcher.addURI(Breezing.AUTHORITY, "weight/#", BREEZING_WEIGHT_ID);
        sURLMatcher.addURI(Breezing.AUTHORITY, "heat_consumption", BREEZING_HEAT_CONSUMPTION);
        sURLMatcher.addURI(Breezing.AUTHORITY, "heat_consumption/#", BREEZING_HEAT_CONSUMPTION_ID);
        sURLMatcher.addURI(Breezing.AUTHORITY, "heat_ingestion", BREEZING_HEAT_INGESTION);
        sURLMatcher.addURI(Breezing.AUTHORITY, "heat_ingestion/#", BREEZING_HEAT_INGESTION_ID);
    }


}
