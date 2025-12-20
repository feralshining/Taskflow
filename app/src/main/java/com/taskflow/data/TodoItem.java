package com.taskflow.data;

/**
 * 투두 항목을 표현하는 데이터 모델 클래스
 * 데이터베이스의 todo_table 테이블과 매핑됩니다.
 */
public class TodoItem {
    private int id; // PK
    private String date; // yyyy-MM-dd 형식
    private String time; // HH:mm 형식
    private String task;
    private boolean completed;

    public TodoItem() {
    }

    /**
     * 모든 필드를 초기화하는 생성자
     * 
     * @param id        투두 항목 ID
     * @param date      날짜
     * @param time      시간
     * @param task      할 일 내용
     * @param completed 완료 여부
     */
    public TodoItem(int id, String date, String time, String task, boolean completed) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.task = task;
        this.completed = completed;
    }

    /**
     * 새 항목 추가용 생성자 (ID 자동 생성)
     * 
     * @param date 날짜
     * @param task 할 일 내용
     */
    public TodoItem(String date, String task) {
        this.date = date;
        this.task = task;
        this.completed = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * 날짜를 포맷팅하여 반환합니다.
     * 
     * @return 포맷된 날짜 (yyyy년 MM월 dd일)
     */
    public String getFormattedDate() {
        if (date == null || date.length() != 8) {
            return date;
        }
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return year + "년 " + month + "월 " + day + "일";
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", task='" + task + '\'' +
                ", completed=" + completed +
                '}';
    }
}
