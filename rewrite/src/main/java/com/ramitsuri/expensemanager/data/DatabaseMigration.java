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
}
