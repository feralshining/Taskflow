package com.taskflow.activities.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.taskflow.R;
import com.taskflow.utils.TaskFlowUI;
import com.taskflow.data.TodoDBHelper;
import com.taskflow.data.TodoItem;
import com.taskflow.data.TodoListAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 투두리스트 작성 및 관리 화면
 * 
 * 주요 기능:
 * - ListView를 통한 투두 항목 표시
 * - SQLite DB를 활용한 데이터 저장 및 조회
 * - CRUD (Create, Read, Update, Delete) 기능
 * - 검색 기능
 * - 완료 상태 관리
 */
public class TodoListActivity extends AppCompatActivity {
    // UI 컴포넌트
    private ListView todoListView;

    // 데이터 관리
    private TodoDBHelper dbHelper;
    private TodoListAdapter adapter;
    private List<TodoItem> todoList;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        // 현재 날짜 저장 - TaskFlowUI.date가 null이면 오늘 날짜 사용
        if (TaskFlowUI.date == null || TaskFlowUI.date.isEmpty()) {
            currentDate = formatDateWithDay(new java.util.Date());
            TaskFlowUI.date = currentDate;
        } else {
            currentDate = TaskFlowUI.date;
        }

        // UI 초기화
        initializeViews();

        // DB 헬퍼 초기화
        dbHelper = new TodoDBHelper(this);

        // 투두리스트 로드
        loadTodoList();

        // 하단 네비게이션 설정
        TaskFlowUI.initBottomNav(this);
        highlightCurrentNav();

        // [ 버튼 - 추가 버튼 클릭시 다이얼로그 표시 ]
        ImageButton addBTN = findViewById(R.id.add_btn);
        addBTN.setOnClickListener(v -> showAddTodoDialog());
    }

    /**
     * UI 컴포넌트를 초기화합니다.
     */
    private void initializeViews() {
        todoListView = findViewById(R.id.todo_listview);
    }

    /**
     * DB에서 오늘 날짜의 투두 리스트를 로드합니다.
     */
    private void loadTodoList() {
        todoList = dbHelper.getTodosForToday();

        if (adapter == null) {
            adapter = new TodoListAdapter(this, todoList, dbHelper);
            todoListView.setAdapter(adapter);
        } else {
            adapter.updateData(todoList);
        }

        // 항목 개수 표시
        int totalCount = todoList.size();
        int completedCount = 0;
        for (TodoItem item : todoList) {
            if (item.isCompleted()) {
                completedCount++;
            }
        }

        if (totalCount > 0) {
            setTitle("투두리스트 (" + completedCount + "/" + totalCount + " 완료)");
        } else {
            setTitle("투두리스트");
        }
    }

    /**
     * 할 일 추가 다이얼로그를 표시합니다.
     * 작업(홈) 탭에서는 오늘 날짜로 자동 설정됩니다.
     */
    private void showAddTodoDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_todo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText todoInput = dialog.findViewById(R.id.todo_input);
        TextView deadlineText = dialog.findViewById(R.id.deadline_text);
        LinearLayout deadlineLayout = (LinearLayout) deadlineText.getParent();
        TextView timeText = dialog.findViewById(R.id.time_text);
        LinearLayout timeLayout = (LinearLayout) timeText.getParent();
        Button addBtn = dialog.findViewById(R.id.add_btn_dialog);

        // 마감 기한을 오늘로 고정
        final Calendar selectedDeadline = Calendar.getInstance();
        deadlineText.setText("오늘");

        // 날짜는 오늘로 고정되어 있지만 UI는 표시 (변경 불가)
        deadlineLayout.setEnabled(false);
        deadlineText.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // 시간 기본값 설정 (17:00)
        final int[] selectedHour = { 17 };
        final int[] selectedMinute = { 0 };
        timeText.setText(String.format(Locale.getDefault(), "오후 %d:%02d", selectedHour[0] - 12, selectedMinute[0]));

        // 시간 선택 클릭 리스너
        timeLayout.setOnClickListener(v -> {
            android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        selectedHour[0] = hourOfDay;
                        selectedMinute[0] = minute;

                        // AM/PM 형식으로 표시
                        String amPm = hourOfDay >= 12 ? "오후" : "오전";
                        int displayHour = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                        timeText.setText(String.format(Locale.getDefault(), "%s %d:%02d", amPm, displayHour, minute));
                    },
                    selectedHour[0],
                    selectedMinute[0],
                    false); // 12시간 형식
            timePickerDialog.show();
        });

        // 추가 버튼
        addBtn.setOnClickListener(v -> {
            String task = todoInput.getText().toString().trim();
            if (task.isEmpty()) {
                TaskFlowUI.showText(this, "내용을 입력해주세요.");
                return;
            }

            // 마감 기한을 yyyy-MM-dd (요일) 형식으로 저장
            String deadlineDate = formatDateWithDay(selectedDeadline.getTime());

            // 시간을 HH:mm 형식으로 저장
            String deadlineTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour[0], selectedMinute[0]);

            // DB에 추가
            long result = dbHelper.addTodo(deadlineDate, deadlineTime, task);
            if (result != -1) {
                loadTodoList();
                TaskFlowUI.showText(this, "추가되었습니다.");
                dialog.dismiss();
            } else {
                TaskFlowUI.showText(this, "추가 실패. 다시 시도해주세요.");
            }
        });

        dialog.show();
    }

    /**
     * 날짜를 yyyy-MM-dd (요일) 형식으로 포맷합니다.
     * 
     * @param date 변환할 날짜
     * @return yyyy-MM-dd (요일) 형식의 문자열
     */
    private String formatDateWithDay(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.KOREAN);

        String formattedDate = dateFormat.format(date);
        String dayOfWeek = dayFormat.format(date);

        return formattedDate + " (" + dayOfWeek + ")";
    }

    /**
     * 현재 활성화된 네비게이션 버튼을 하이라이트합니다.
     */
    private void highlightCurrentNav() {
        // 작업 버튼(home_btn)을 selected 이미지로 변경
        ImageView homeIcon = findViewById(R.id.home_icon);
        TextView homeText = findViewById(R.id.home_text);

        if (homeIcon != null) {
            homeIcon.setImageResource(R.drawable.selected_home);
        }
        if (homeText != null) {
            homeText.setTextColor(Color.parseColor("#6366F1"));
        }

        // 나머지 버튼들은 nonSelected 이미지로 설정
        ImageView calendarIcon = findViewById(R.id.calendar_icon);
        TextView calendarText = findViewById(R.id.calendar_text);
        ImageView settingIcon = findViewById(R.id.setting_icon);
        TextView settingText = findViewById(R.id.setting_text);

        if (calendarIcon != null) {
            calendarIcon.setImageResource(R.drawable.nonselected_calendar);
        }
        if (calendarText != null) {
            calendarText.setTextColor(Color.parseColor("#6B7280"));
        }
        if (settingIcon != null) {
            settingIcon.setImageResource(R.drawable.nonselected_user);
        }
        if (settingText != null) {
            settingText.setTextColor(Color.parseColor("#6B7280"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 캘린더에서 돌아왔을 때 할 일 목록 새로고침
        loadTodoList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
