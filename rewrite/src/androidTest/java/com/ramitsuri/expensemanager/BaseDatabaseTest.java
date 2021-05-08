package com.ramitsuri.expensemanager;

import android.content.Context;

import com.ramitsuri.expensemanager.data.ExpenseManagerDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith(AndroidJUnit4ClassRunner.class)
public abstract class BaseDatabaseTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    static final String TAG = BaseDatabaseTest.class.getName();

    ExpenseManagerDatabase mDb;

    @Before
    public void createDb() {
        Context appContext = getContext();
        mDb = Room.inMemoryDatabaseBuilder(appContext, ExpenseManagerDatabase.class).build();
    }

    private Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }
}
