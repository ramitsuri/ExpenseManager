package com.ramitsuri.expensemanager.data;

import android.database.sqlite.SQLiteException;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class DatabaseMigrationTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public DatabaseMigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                ExpenseManagerDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate1To2() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);

        // db has schema version 1. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("SELECT * FROM Expense");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, DatabaseMigration.MIGRATION_1_2);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        db.execSQL(
                "INSERT INTO Log(time,type,result,message,acknowledged) VALUES(4673483849, 'periodic_backup', 'success',null,'0')");
    }

    @Test
    public void migrate2To3() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);

        // db has schema version 2. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("SELECT * FROM Expense");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 3 and provide
        // MIGRATION_2_3 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, DatabaseMigration.MIGRATION_2_3);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        db.execSQL(
                "INSERT INTO Expense(amount,payment_method,category,description,store,sheet_id,date_time,is_synced,is_starred) VALUES('20.00','chase','fun','movie','sunray',1544454,7843,1,1)");
    }

    @Test
    public void migrate3To4() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 3);

        // db has schema version 3. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("SELECT * FROM Expense");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 4 and provide
        // MIGRATION_3_4 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, DatabaseMigration.MIGRATION_3_4);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        db.execSQL("INSERT INTO SheetInfo(sheet_name,sheet_id) VALUES('DEC', '154646')");
    }

    @Test
    public void migrate4To5() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 4);

        // db has schema version 4. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("SELECT * FROM Expense");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 5 and provide
        // MIGRATION_4_5 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 5, true, DatabaseMigration.MIGRATION_4_5);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        db.execSQL(
                "INSERT INTO Budget(name,amount,categories) VALUES('Budget1', '32.93','[\"Utilities\",\"Rent\",\"Home\"]')");
    }

    @Test
    public void migrate5To6() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 5);

        // db has schema version 5. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("SELECT * FROM Expense");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 6 and provide
        // MIGRATION_5_6 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 6, true, DatabaseMigration.MIGRATION_5_6);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        db.execSQL("INSERT INTO EditedSheet(sheet_id) VALUES(32211)");
    }

    @Test
    public void migrate6To7() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 6);

        // db has schema version 6. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("SELECT * FROM Expense");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 7 and provide
        // MIGRATION_6_7 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 7, true, DatabaseMigration.MIGRATION_6_7);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        db.execSQL(
                "INSERT INTO Expense(amount,payment_method,category,description,store,sheet_id,date_time,is_synced,is_starred,is_income) VALUES('20.00','chase','fun','movie','sunray',1544454,7843,1,1,1)");
    }

    @Test
    public void migrate7To8() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 7);

        // db has schema version 7. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("SELECT * FROM Expense");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 8 and provide
        // MIGRATION_7_8 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 8, true, DatabaseMigration.MIGRATION_7_8);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        db.execSQL(
                "INSERT INTO Expense(amount,payment_method,category,description,store,sheet_id,date_time,is_synced,is_starred,is_income,record_type) VALUES('20.00','chase','fun','movie','sunray',1544454,7843,1,1,1,'MONTHLY')");
    }

    @Test
    public void migrate8To9() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 8);

        // db has schema version 8. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("SELECT * FROM Expense");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 9 and provide
        // MIGRATION_8_9 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 9, true, DatabaseMigration.MIGRATION_8_9);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.

        // Will pass
        db.execSQL(
                "INSERT INTO " +
                        "Expense(amount,payment_method,category,description,store,sheet_id," +
                        "date_time,is_synced,is_starred,is_income,record_type, identifier) " +
                        "VALUES('20.00','chase','fun','movie','sunray'," +
                        "1544454,7843,1,1,1,'MONTHLY', '1')");

        // Will fail because identifier is NonNull but is not being inserted
        try {
            db.execSQL(
                    "INSERT INTO " +
                            "Expense(amount,payment_method,category,description,store,sheet_id," +
                            "date_time,is_synced,is_starred,is_income,record_type) " +
                            "VALUES('20.00','chase','fun','movie','sunray'," +
                            "1544454,7843,1,1,1,'MONTHLY')");
            Assert.fail("Expected exception did not occur");
        } catch (SQLiteException e) {
            Assert.assertTrue(e.getMessage().contains("NOT NULL constraint failed"));
        }
    }
}
