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

    public String[] getAllColumns(){
        return new String[]{
                ADAPTER_ROWID,
                PaymentMethodDBConstants.COLUMN_ID,
                PaymentMethodDBConstants.COLUMN_NAME
        };
    }

    public ContentValues getPaymentMethodContentValues(PaymentMethod paymentMethod){
        int id = paymentMethod.getId();
        String name = paymentMethod.getName();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PaymentMethodDBConstants.COLUMN_ID, id);
        contentValues.put(PaymentMethodDBConstants.COLUMN_NAME, name);

        return contentValues;
    }

    public PaymentMethod getPaymentMethodFromCursor(Cursor cursor){
        PaymentMethod paymentMethod = new PaymentMethod();
        for(String column: cursor.getColumnNames()){
            if(column.equals(PaymentMethodDBConstants.COLUMN_ID)){
                int value = cursor.getInt(cursor.getColumnIndex(PaymentMethodDBConstants.COLUMN_ID));
                paymentMethod.setId(value);
            } else if(column.equals(CategoryDBConstants.COLUMN_NAME)){
                String value = cursor.getString(
                        cursor.getColumnIndex(PaymentMethodDBConstants.COLUMN_NAME));
                paymentMethod.setName(value);
            }
        }
        return paymentMethod;
    }

    public synchronized boolean setPaymentMethod(PaymentMethod paymentMethod){
        open();

        boolean insertSuccess = true;
        ContentValues contentValues = getPaymentMethodContentValues(paymentMethod);
        long result = mDatabase.insertOrThrow(PaymentMethodDBConstants.TABLE_PAYMENT_METHOD, null,
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

        Cursor cursor = getCursor(PaymentMethodDBConstants.TABLE_PAYMENT_METHOD, columns,
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

    //TODO don't delete if payment method has corresponding rows in expense table
    public synchronized boolean deletePaymentMethod(int paymentMethodId){
        if(paymentMethodId <= 0){
            throw new IllegalArgumentException();
        }
        open();
        String selection1 = PaymentMethodDBConstants.COLUMN_ID + " = ?";
        String[] selectionArgs1 = new String[]{
                String.valueOf(paymentMethodId)
        };

        int result1 = mDatabase.delete(
                PaymentMethodDBConstants.TABLE_PAYMENT_METHOD,
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

        String selection = PaymentMethodDBConstants.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{
                String.valueOf(paymentMethodId)
        };

        ContentValues contentValues = new ContentValues();
        contentValues.put(PaymentMethodDBConstants.COLUMN_NAME, newName);

        int result = mDatabase.update(
                PaymentMethodDBConstants.TABLE_PAYMENT_METHOD,
                contentValues,
                selection,
                selectionArgs
        );
        close();

        return result > 0;
    }
}
