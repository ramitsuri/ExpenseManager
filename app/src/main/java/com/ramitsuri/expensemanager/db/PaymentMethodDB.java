package com.ramitsuri.expensemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ramitsuri.expensemanager.entities.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDB extends BaseDB {


    public PaymentMethodDB(Context context){
        super(context);
    }

    private String[] getAllColumns(){
        return new String[]{
                DBConstants.COLUMN_PAYMENT_METHOD_ID,
                DBConstants.COLUMN_PAYMENT_METHOD_NAME
        };
    }

    private ContentValues getPaymentMethodContentValues(PaymentMethod paymentMethod){
        int id = paymentMethod.getId();
        String name = paymentMethod.getName();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_PAYMENT_METHOD_ID, id);
        contentValues.put(DBConstants.COLUMN_PAYMENT_METHOD_NAME, name);

        return contentValues;
    }

    private PaymentMethod getPaymentMethodFromCursor(Cursor cursor){
        PaymentMethod paymentMethod = new PaymentMethod();
        for(String column: cursor.getColumnNames()){
            if(column.equals(DBConstants.COLUMN_PAYMENT_METHOD_ID)){
                int value =
                        cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN_PAYMENT_METHOD_ID));
                paymentMethod.setId(value);
            } else if(column.equals(DBConstants.COLUMN_PAYMENT_METHOD_NAME)){
                String value = cursor.getString(
                        cursor.getColumnIndex(DBConstants.COLUMN_PAYMENT_METHOD_NAME));
                paymentMethod.setName(value);
            }
        }
        return paymentMethod;
    }

    public synchronized boolean setPaymentMethod(String name){
        open();

        boolean insertSuccess = true;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_PAYMENT_METHOD_NAME, name);
        long result = mDatabase.insertOrThrow(DBConstants.TABLE_PAYMENT_METHOD, null,
                contentValues);
        if(result <= 0){
            insertSuccess = false;
        }
        close();
        return insertSuccess;
    }

    public List<PaymentMethod> getAllPaymentMethods() {
        open();

        String[] columns = getAllColumns();

        Cursor cursor = getCursor(DBConstants.TABLE_PAYMENT_METHOD, columns,
                null, null, null, null, null, null);

        List<PaymentMethod> paymentMethods = new ArrayList<>();
        try {
            if(cursor.moveToFirst()){
                do {
                    PaymentMethod paymentMethod = getPaymentMethodFromCursor(cursor);
                    paymentMethods.add(paymentMethod);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){

        }

        cursor.close();
        close();

        return paymentMethods;
    }

    public PaymentMethod getFirstPaymentMethod(){
        open();

        String[] columns = getAllColumns();

        Cursor cursor = getCursor(DBConstants.TABLE_PAYMENT_METHOD, columns, null, null,
                null, null, null, null);

        PaymentMethod paymentMethod = null;

        if(cursor.moveToFirst()){
            paymentMethod = getPaymentMethodFromCursor(cursor);
        }

        return paymentMethod;
    }

    //TODO don't delete if payment method has corresponding rows in expense table
    public synchronized boolean deletePaymentMethod(int paymentMethodId){
        if(paymentMethodId <= 0){
            throw new IllegalArgumentException();
        }
        open();
        String selection1 = DBConstants.COLUMN_PAYMENT_METHOD_ID + " = ?";
        String[] selectionArgs1 = new String[]{
                String.valueOf(paymentMethodId)
        };

        int result1 = mDatabase.delete(
                DBConstants.TABLE_PAYMENT_METHOD,
                selection1,
                selectionArgs1
        );

        close();

        return result1 > 0;
    }

    public synchronized boolean setPaymentMethodName(int paymentMethodId, String newName){
        if(paymentMethodId <= 0){
            throw new IllegalArgumentException();
        }
        open();

        String selection = DBConstants.COLUMN_PAYMENT_METHOD_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(paymentMethodId)
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN_PAYMENT_METHOD_NAME, newName);

        int result = mDatabase.update(
                DBConstants.TABLE_PAYMENT_METHOD,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }
}
