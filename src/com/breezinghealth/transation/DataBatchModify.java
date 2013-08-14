package com.breezinghealth.transation;import java.util.HashMap;public class DataBatchModify {    private static final String TAG = DataBatchModify.class.getSimpleName();    private final HashMap<String, DataRowModify> mDataRowModify;    public DataBatchModify() {        mDataRowModify = new HashMap<String, DataRowModify>();    }    public DataRowModify getDataRowModify(String mimeType) {        return mDataRowModify.get(mimeType);    }    public void addDataRow(String mimeType, DataRowModify dataRow) {        if ( (mimeType == null) || (dataRow == null) ) {            return;        }        mDataRowModify.put(mimeType, dataRow);    }}