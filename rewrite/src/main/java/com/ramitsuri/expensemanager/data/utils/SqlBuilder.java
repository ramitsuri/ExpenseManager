package com.ramitsuri.expensemanager.data.utils;

import android.text.TextUtils;
import android.util.Pair;

import com.ramitsuri.expensemanager.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

public class SqlBuilder {
    @NonNull
    private final StringBuilder mSql;

    private List<String> mArgs;

    public SqlBuilder() {
        mSql = new StringBuilder();
    }

    @Nonnull
    @Override
    public String toString() {
        return mSql.toString().replaceAll(" {2,}", " ").trim();
    }

    public List<String> getArgs() {
        return mArgs;
    }

    public SqlBuilder append(String string) {
        mSql.append(string);
        return this;
    }

    public SqlBuilder select(String... args) {
        return append(" SELECT ").appendArgs(args);
    }

    public SqlBuilder update(String tableName) {
        return append(" UPDATE " + tableName);
    }

    public SqlBuilder setEqualTo(String column, Object result) {
        return append(" SET " + column + " = " + result + " ");
    }

    public SqlBuilder from(String tableName) {
        return append(" FROM ").appendArgs(new String[] {tableName});
    }

    public SqlBuilder join(String tableName) {
        return append(" JOIN " + tableName);
    }

    public SqlBuilder leftOuterJoin(String tableName, String joinOnLink) {
        return append(" LEFT OUTER JOIN " + tableName + " ON " + joinOnLink);
    }

    public SqlBuilder on(String firstColumn, String secondColumn) {
        return append(" ON " + firstColumn + " = " + secondColumn);
    }

    public SqlBuilder groupBy(String column) {
        return append(" GROUP BY " + column);
    }

    public SqlBuilder where() {
        if (!contains("WHERE")) {
            return append(" WHERE ");
        }
        return this;
    }

    public SqlBuilder whereOrAnd() {
        if (!contains("WHERE")) {
            return append(" WHERE ");
        }
        return and();
    }

    public SqlBuilder whereOrOr() {
        if (!contains("WHERE")) {
            return append(" WHERE ");
        }
        return or();
    }

    public SqlBuilder whereParen(String condition) {
        return append(" WHERE (" + condition + ") ");
    }

    public SqlBuilder and() {
        return append(" AND ");
    }

    public SqlBuilder or() {
        return append(" OR ");
    }

    public String notIn(@NonNull Object[] args) {
        return new SqlBuilder()
                .append(" NOT IN ( ").appendArgs(args).append(" ) ").toString();
    }

    public String in(@NonNull Object[] args) {
        return new SqlBuilder()
                .append(" IN ( ").appendArgs(args).append(" ) ").toString();
    }

    public <T> SqlBuilder in(@NonNull List<T> args) {
        append(" IN (")
                .appendArgs(args)
                .append(") ");
        return addArgs(args);
    }

    public <T> SqlBuilder between(@NonNull Pair<T, T> pair) {
        return append("BETWEEN ? AND ? ")
                .addArg(pair.first)
                .addArg(pair.second);
    }

    public <T> SqlBuilder between(String column, @NonNull List<Pair<T, T>> pairs) {
        append(" ( ")
                .append(TextUtils.join(" OR ",
                        Collections.nCopies(pairs.size(), " (" + column + " BETWEEN ? AND ?) ")))
                .append(" ) ");
        for (Pair<T, T> pair : pairs) {
            addArg(pair.first)
                    .addArg(pair.second);
        }
        return this;
    }

    public SqlBuilder column(String columnName) {
        return append(" " + columnName + " ");
    }

    public SqlBuilder equal(Object parameter) {
        append(" = ? ");
        return addArg(String.valueOf(parameter));
    }

    public SqlBuilder addArg(Object arg) {
        if (mArgs == null) {
            mArgs = new ArrayList<>();
        }
        mArgs.add(String.valueOf(arg));
        return this;
    }

    public <T> SqlBuilder addArgs(@NonNull List<T> args) {
        for (T arg : args) {
            mArgs.add(String.valueOf(arg));
        }
        return this;
    }

    private SqlBuilder appendArgs(@NonNull Object[] args) {
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                append(", ");
            }
            append(args[i] != null ? args[i].toString() : null);
        }
        return this;
    }

    private <T> SqlBuilder appendArgs(@NonNull List<T> args) {
        return append(TextUtils.join(",", Collections.nCopies(args.size(), "?")));
    }

    private boolean contains(String text) {
        return mSql.indexOf(text) != -1;
    }
}
