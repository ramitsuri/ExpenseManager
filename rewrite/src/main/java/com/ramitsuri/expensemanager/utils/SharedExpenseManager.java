package com.ramitsuri.expensemanager.utils;

import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ramitsuri.expensemanager.entities.Expense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class SharedExpenseManager {
    private static final String SOURCE = "source";

    private Attributes mAttributes;
    private Callbacks mCallbacks;
    private FirebaseFirestore mDb;

    public SharedExpenseManager(Attributes attributes, @Nonnull Callbacks callbacks) {
        mAttributes = attributes;
        mCallbacks = callbacks;
        mDb = FirebaseFirestore.getInstance();
    }

    public void add(@Nonnull Expense expense) {
        if (!isValid(mAttributes)) {
            Timber.i("Attributes not valid");
            mCallbacks.failure("add", new Exception("Attributes not valid"));
            return;
        }
        Map<String, Object> expenseMap = toMap(expense, mAttributes.mThisSource);
        getCollection(mAttributes)
                .add(expenseMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mCallbacks.addSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mCallbacks.failure("add", e);
                    }
                });
    }

    public void getForOtherSource(@Nonnull final String source) {
        if (!isValid(mAttributes, source)) {
            Timber.i("Attributes not valid");
            mCallbacks.failure("getForSource", new Exception("Attributes not valid"));
            return;
        }
        final List<Expense> expenses = new ArrayList<>();
        getCollection(mAttributes)
                .whereEqualTo(SOURCE, source)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    Expense expense = fromMap(document.getData());
                                    expenses.add(expense);
                                }
                            }
                            mCallbacks.getForOtherSuccess(source, expenses);
                        } else {
                            Exception e;
                            if (task.getException() == null) {
                                e = new Exception("Unknown reason");
                            } else {
                                e = task.getException();
                            }
                            mCallbacks.failure("getForSource", e);
                        }
                    }
                });
    }

    public void deleteForOtherSource(@Nonnull final String source) {
        if (!isValid(mAttributes, source)) {
            Timber.i("Attributes not valid");
            mCallbacks.failure("deleteForSource", new Exception("Attributes not valid"));
            return;
        }
        getCollection(mAttributes)
                .whereEqualTo(SOURCE, source)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            WriteBatch batch = mDb.batch();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    batch.delete(document.getReference());
                                }
                            }
                            batch.commit();
                            mCallbacks.deleteForOtherSuccess(source);
                        } else {
                            Exception e;
                            if (task.getException() == null) {
                                e = new Exception("Unknown reason");
                            } else {
                                e = task.getException();
                            }
                            mCallbacks.failure("deleteForSource", e);
                        }
                    }
                });
    }

    public boolean isEnabled() {
        return isValid(mAttributes);
    }

    private Map<String, Object> toMap(@Nonnull Expense expense, @Nonnull String source) {
        Map<String, Object> expenseMap = new HashMap<>();
        expenseMap.put(Expense.COL_DATE_TIME, expense.getDateTime());
        expenseMap.put(Expense.COL_AMOUNT, String.valueOf(expense.getAmount()));
        expenseMap.put(Expense.COL_PAYMENT, expense.getPaymentMethod());
        expenseMap.put(Expense.COL_CATEGORY, expense.getCategory());
        expenseMap.put(Expense.COL_DESCRIPTION, expense.getDescription());
        expenseMap.put(Expense.COL_STORE, expense.getStore());
        expenseMap.put(Expense.COL_STARRED, booleanToString(expense.isStarred()));
        expenseMap.put(Expense.COL_RECORD_TYPE, expense.getRecordType());
        expenseMap.put(Expense.COL_IDENTIFIER, expense.getIdentifier());
        expenseMap.put(SOURCE, source);
        return expenseMap;
    }

    private Expense fromMap(@Nonnull Map<String, Object> map) {
        Expense expense = new Expense();
        expense.setDateTime((long)map.get(Expense.COL_DATE_TIME));
        expense.setAmount(new BigDecimal((String)map.get(Expense.COL_AMOUNT)));
        expense.setPaymentMethod((String)map.get(Expense.COL_PAYMENT));
        expense.setCategory((String)map.get(Expense.COL_CATEGORY));
        expense.setDescription((String)map.get(Expense.COL_DESCRIPTION));
        expense.setStore((String)map.get(Expense.COL_STORE));
        expense.setIsStarred(booleanFromString((String)map.get(Expense.COL_STARRED)));
        expense.setRecordType((String)map.get(Expense.COL_RECORD_TYPE));
        Object identifier = map.get(Expense.COL_IDENTIFIER);
        if (identifier != null) {
            expense.setIdentifier((String)identifier);
        }
        return expense;
    }

    private boolean isValid(Attributes attributes) {
        return attributes != null &&
                !TextUtils.isEmpty(attributes.mCollectionName) &&
                !TextUtils.isEmpty(attributes.mThisSource);
    }

    private boolean isValid(Attributes attributes, String source) {
        return isValid(attributes) && !TextUtils.isEmpty(source);
    }

    private String booleanToString(boolean value) {
        return value ? "true" : "false";
    }

    private boolean booleanFromString(String value) {
        return "true".equals(value);
    }

    @Nonnull
    private CollectionReference getCollection(@Nonnull Attributes attributes) {
        return mDb.collection(attributes.mCollectionName);
    }

    static class Attributes {
        private String mCollectionName;
        private String mThisSource;

        Attributes(String collectionName, String thisSource) {
            mCollectionName = collectionName;
            mThisSource = thisSource;
        }
    }

    public interface Callbacks {
        void addSuccess();

        void deleteForOtherSuccess(@Nonnull String other);

        void getForOtherSuccess(@Nonnull String other, @Nonnull List<Expense> expenses);

        void failure(@Nonnull String message, @Nonnull Exception e);
    }
}
