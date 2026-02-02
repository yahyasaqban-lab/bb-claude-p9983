package com.bbclaude.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "bbclaude.db";
    private static final int DB_VERSION = 1;

    public ChatDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "role TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "ts INTEGER NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }

    public long insertMessage(String role, String content, long timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("role", role);
        values.put("content", content);
        values.put("ts", timestamp);
        return db.insert("messages", null, values);
    }

    public List<Message> getAllMessages() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, role, content, ts FROM messages ORDER BY id ASC", null);
        List<Message> out = new ArrayList<Message>();
        try {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String role = cursor.getString(1);
                String content = cursor.getString(2);
                long ts = cursor.getLong(3);
                out.add(new Message(id, role, content, ts));
            }
        } finally {
            cursor.close();
        }
        return out;
    }

    public List<Message> getRecentMessages(int limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, role, content, ts FROM messages ORDER BY id DESC LIMIT ?",
                new String[] { String.valueOf(limit) }
        );
        List<Message> out = new ArrayList<Message>();
        try {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String role = cursor.getString(1);
                String content = cursor.getString(2);
                long ts = cursor.getLong(3);
                out.add(new Message(id, role, content, ts));
            }
        } finally {
            cursor.close();
        }
        return out;
    }
}
