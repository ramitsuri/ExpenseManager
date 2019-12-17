package com.ramitsuri.expensemanager.data;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration {

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Log` " +
                    "(" +
                    "`mId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`time` INTEGER NOT NULL, " +
                    "`type` TEXT, " +
                    "`result` TEXT, " +
                    "`message` TEXT, " +
                    "`acknowledged` INTEGER NOT NULL" +
                    ")");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `Expense` " +
                    "ADD COLUMN " +
                    "'sheet_id' INTEGER NOT NULL DEFAULT -1");
        }
    };
}
