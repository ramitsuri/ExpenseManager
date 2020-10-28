package com.ramitsuri.expensemanager.data;

import androidx.annotation.NonNull;
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

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `SheetInfo` " +
                    "(" +
                    "`mId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`sheet_name` TEXT, " +
                    "`sheet_id` INTEGER NOT NULL " +
                    ")");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Budget` " +
                    "(" +
                    "`mId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, " +
                    "`amount` TEXT, " +
                    "`categories` TEXT " +
                    ")");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `EditedSheet` " +
                    "(" +
                    "`mId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`sheet_id` INTEGER NOT NULL " +
                    ")");
            database.execSQL(
                    "CREATE UNIQUE INDEX `index_EditedSheet_sheet_id` ON `EditedSheet` (`sheet_id`)");
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `Expense` " +
                    "ADD COLUMN " +
                    "'is_income' INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `Expense` " +
                    "ADD COLUMN " +
                    "`record_type` TEXT NOT NULL DEFAULT 'MONTHLY'");
            database.execSQL("ALTER TABLE `Expense` " +
                    "ADD COLUMN " +
                    "`identifier` TEXT");
            database.execSQL("ALTER TABLE `Budget` " +
                    "ADD COLUMN " +
                    "`record_type` TEXT NOT NULL DEFAULT 'MONTHLY'");
            database.execSQL("ALTER TABLE `Category` " +
                    "ADD COLUMN " +
                    "`record_type` TEXT NOT NULL DEFAULT 'MONTHLY'");
        }
    };
}
