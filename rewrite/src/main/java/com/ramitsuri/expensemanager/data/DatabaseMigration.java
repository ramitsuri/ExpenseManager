package com.ramitsuri.expensemanager.data;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;

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

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            String createTemp = "CREATE TABLE IF NOT EXISTS `ExpenseTmp` " +
                    "(`mId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`date_time` INTEGER NOT NULL, " +
                    "`amount` TEXT NOT NULL, " +
                    "`payment_method` TEXT NOT NULL, " +
                    "`category` TEXT NOT NULL, " +
                    "`description` TEXT NOT NULL, " +
                    "`store` TEXT NOT NULL, " +
                    "`is_synced` INTEGER NOT NULL, " +
                    "`is_starred` INTEGER NOT NULL, " +
                    "`sheet_id` INTEGER NOT NULL, " +
                    "`is_income` INTEGER NOT NULL, " +
                    "`record_type` TEXT NOT NULL DEFAULT 'MONTHLY', " +
                    "`identifier` TEXT NOT NULL)";
            database.execSQL(createTemp);

            String copyData = "INSERT INTO 'ExpenseTmp' " +
                    "(`date_time`, " +
                    "`amount`, " +
                    "`payment_method`, " +
                    "`category`, " +
                    "`description`, " +
                    "`store`, " +
                    "`is_synced`, " +
                    "`is_starred`, " +
                    "`sheet_id`, " +
                    "`is_income`, " +
                    "`record_type`, " +
                    "`identifier`) " +
                    "SELECT " +
                    "`date_time`, " +
                    "`amount`, " +
                    "`payment_method`, " +
                    "`category`, " +
                    "`description`, " +
                    "`store`, " +
                    "`is_synced`, " +
                    "`is_starred`, " +
                    "`sheet_id`, " +
                    "`is_income`, " +
                    "`record_type`, " +
                    "`identifier` FROM 'Expense'";
            database.execSQL(copyData);

            String dropExpense = "DROP TABLE 'Expense'";
            database.execSQL(dropExpense);

            String renameExpense = "ALTER TABLE 'ExpenseTmp' RENAME TO 'Expense'";
            database.execSQL(renameExpense);
        }
    };

    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add RecurringExpenseInfo table
            database.execSQL("CREATE TABLE IF NOT EXISTS `RecurringExpenseInfo` " +
                    "(" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`identifier` TEXT NOT NULL, " +
                    "`last_occur` INTEGER NOT NULL, " +
                    "`recur_type` TEXT NOT NULL " +
                    ")");

            // Add AddType column to Expense
            database.execSQL("ALTER TABLE `Expense` " +
                    "ADD COLUMN " +
                    "`add_type` TEXT NOT NULL DEFAULT 'MANUAL'");
        }
    };
}
