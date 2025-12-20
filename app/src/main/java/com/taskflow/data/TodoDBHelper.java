package com.taskflow.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * 투두리스트 데이터를 관리하는 SQLite 데이터베이스 헬퍼 클래스
 * CRUD(Create, Read, Update, Delete) 기능을 제공합니다.
 */
public class TodoDBHelper extends SQLiteOpenHelper {
    // 데이터베이스 버전 및 이름 상수
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "TaskFlow.db";

    // 테이블 및 컬럼 이름 상수
    private static final String TABLE_TODO = "todo_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_TASK = "task";
    private static final String COLUMN_COMPLETED = "completed";
    private static final String COLUMN_CREATED_AT = "created_at";

    /**
     * 생성자
     * 
     * @param context 애플리케이션 컨텍스트
     */
    public TodoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 데이터베이스가 처음 생성될 때 호출됩니다.
     * todo_table을 생성합니다.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT NOT NULL,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_TASK + " TEXT NOT NULL,"
                + COLUMN_COMPLETED + " INTEGER DEFAULT 0,"
                + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_TODO_TABLE);
    }

    /**
     * 데이터베이스 버전이 업그레이드될 때 호출됩니다.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 버전 1에서 2로 업그레이드: time 컬럼 추가
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COLUMN_TIME + " TEXT");
        }
    }

    /**
     * 새로운 투두 항목을 데이터베이스에 추가합니다.
     * 
     * @param date 날짜 (yyyy-MM-dd 형식)
     * @param time 시간 (HH:mm 형식)
     * @param task 할 일 내용
     * @return 추가된 행의 ID (실패 시 -1)
     */
    public long addTodo(String date, String time, String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_TASK, task);
        values.put(COLUMN_COMPLETED, 0);

        long id = db.insert(TABLE_TODO, null, values);
        db.close();
        return id;
    }

    /**
     * 특정 날짜의 모든 투두 항목을 조회합니다.
     * 
     * @param date 날짜 (yyyy-MM-dd 형식)
     * @return TodoItem 리스트
     */
    public List<TodoItem> getTodosByDate(String date) {
        List<TodoItem> todoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TODO +
                " WHERE " + COLUMN_DATE + " LIKE ? " +
                " ORDER BY " + COLUMN_CREATED_AT + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { date + "%" });

        if (cursor.moveToFirst()) {
            do {
                TodoItem item = new TodoItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1);
                todoList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return todoList;
    }

    /**
     * 투두 항목의 완료 상태를 업데이트합니다.
     * 
     * @param id        투두 항목 ID
     * @param completed 완료 여부 (true/false)
     * @return 성공 시 1, 실패 시 0
     */
    public int updateTodoCompleted(int id, boolean completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, completed ? 1 : 0);

        int result = db.update(TABLE_TODO, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    /**
     * 투두 항목을 수정합니다.
     * 
     * @param id   투두 항목 ID
     * @param task 새로운 할 일 내용
     * @return 성공 시 1, 실패 시 0
     */
    public int updateTodoTask(int id, String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);

        int result = db.update(TABLE_TODO, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    /**
     * 투두 항목을 삭제합니다.
     * 
     * @param id 투두 항목 ID
     * @return 성공 시 1, 실패 시 0
     */
    public int deleteTodo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TODO, COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    /**
     * 오늘 날짜의 모든 투두 항목을 조회합니다.
     * 
     * @return TodoItem 리스트
     */
    public List<TodoItem> getTodosForToday() {
        List<TodoItem> todoList = new ArrayList<>();

        // 오늘 날짜 가져오기 (yyyy-MM-dd 형식)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new java.util.Date());

        String selectQuery = "SELECT * FROM " + TABLE_TODO +
                " WHERE " + COLUMN_DATE + " LIKE ? " +
                " ORDER BY " + COLUMN_CREATED_AT + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { today + "%" });

        if (cursor.moveToFirst()) {
            do {
                TodoItem item = new TodoItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1);
                todoList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return todoList;
    }

    /**
     * 특정 날짜에 할 일이 있는지 확인합니다.
     * 
     * @param date 날짜 (yyyy-MM-dd 형식)
     * @return 할 일이 있으면 true, 없으면 false
     */
    public boolean hasTodosOnDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TODO +
                " WHERE " + COLUMN_DATE + " LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[] { date + "%" });

        boolean hasTodos = false;
        if (cursor.moveToFirst()) {
            hasTodos = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();
        return hasTodos;
    }

}
