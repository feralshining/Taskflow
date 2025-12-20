package com.taskflow.activities.main;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.taskflow.R;
import com.taskflow.activities.todo.TodoListActivity;
import com.taskflow.data.TodoDBHelper;
import com.taskflow.data.TodoItem;
import com.taskflow.data.TodoListAdapter;
import com.taskflow.utils.TaskFlowUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Calendar currentCalendar;
    private Calendar selectedCalendar;
    private GridLayout calendarGrid;
    private TextView currentMonthText;
    private TextView selectedDateText;
    private ListView todoListView;
    private TodoListAdapter adapter;
    private List<TodoItem> todoList;
    private TodoDBHelper dbHelper;
    private View selectedDayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // 초기화
        currentCalendar = Calendar.getInstance();
        selectedCalendar = Calendar.getInstance();
        dbHelper = new TodoDBHelper(this);

        // UI 컴포넌트 찾기
        calendarGrid = findViewById(R.id.calendar_grid);
        currentMonthText = findViewById(R.id.current_month_text);
        selectedDateText = findViewById(R.id.selected_date_text);
        todoListView = findViewById(R.id.todo_listview);

        // 할 일 목록 초기화
        todoList = new ArrayList<>();
        adapter = new TodoListAdapter(this, todoList, dbHelper);
        adapter.setOnDataChangedListener(() -> {
            // 데이터 변경 시 캘린더 갱신
            updateCalendar();
        });

        // 이전/다음 달 버튼
        ImageButton prevMonthBtn = findViewById(R.id.prev_month_btn);
        ImageButton nextMonthBtn = findViewById(R.id.next_month_btn);

        prevMonthBtn.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextMonthBtn.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        // 하단 네비게이션
        TaskFlowUI.initBottomNav(this);
        highlightCurrentNav();

        // + 할 일 추가 버튼
        android.widget.Button addTodoBtn = findViewById(R.id.add_todo_btn);
        addTodoBtn.setOnClickListener(v -> showAddTodoDialog());

        // 캘린더 표시
        updateCalendar();
        loadTodosForSelectedDate();

        // 뒤로 가기 비활성화
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing
            }
        });
    }

    /**
     * 캘린더를 업데이트합니다.
     */
    private void updateCalendar() {
        // 월 텍스트 업데이트
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy년 M월", Locale.KOREAN);
        currentMonthText.setText(monthFormat.format(currentCalendar.getTime()));

        // 그리드 초기화
        calendarGrid.removeAllViews();

        // 해당 월의 1일로 이동
        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // 1일의 요일 (1=일요일, 7=토요일)
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 빈 칸 추가 (1일 이전)
        for (int i = 1; i < firstDayOfWeek; i++) {
            addDayView(0, false, false, false);
        }

        // 날짜 추가
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int day = 1; day <= daysInMonth; day++) {
            boolean isToday = (today.get(Calendar.YEAR) == tempCalendar.get(Calendar.YEAR) &&
                    today.get(Calendar.MONTH) == tempCalendar.get(Calendar.MONTH) &&
                    today.get(Calendar.DAY_OF_MONTH) == day);

            boolean isSelected = (selectedCalendar.get(Calendar.YEAR) == tempCalendar.get(Calendar.YEAR) &&
                    selectedCalendar.get(Calendar.MONTH) == tempCalendar.get(Calendar.MONTH) &&
                    selectedCalendar.get(Calendar.DAY_OF_MONTH) == day);

            // 해당 날짜에 할 일이 있는지 확인
            String dateStr = sdf.format(tempCalendar.getTime());
            boolean hasTodos = dbHelper.hasTodosOnDate(dateStr);

            addDayView(day, isToday, isSelected, hasTodos);
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        updateSelectedDateText();
    }

    /**
     * 날짜 뷰를 추가합니다.
     */
    private void addDayView(int day, boolean isToday, boolean isSelected, boolean hasTodos) {
        // LinearLayout으로 감싸서 숫자와 점을 모두 표시
        android.widget.LinearLayout dayLayout = new android.widget.LinearLayout(this);
        dayLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        dayLayout.setGravity(Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(4, 4, 4, 4);
        dayLayout.setLayoutParams(params);

        if (day == 0) {
            // 빈 칸
            calendarGrid.addView(dayLayout);
            return;
        }

        TextView dayView = new TextView(this);
        dayView.setText(String.valueOf(day));
        dayView.setTextSize(16);
        dayView.setGravity(Gravity.CENTER);
        dayView.setPadding(16, 8, 16, 8);

        // 배경 설정
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);

        if (isSelected) {
            // 선택된 날짜
            background.setColor(Color.parseColor("#6366F1"));
            dayView.setTextColor(Color.WHITE);
            selectedDayView = dayLayout;
        } else if (isToday) {
            // 오늘 날짜
            background.setColor(Color.parseColor("#E0E7FF"));
            dayView.setTextColor(Color.parseColor("#6366F1"));
        } else {
            background.setColor(Color.TRANSPARENT);
            dayView.setTextColor(Color.parseColor("#1F2937"));
        }

        dayView.setBackground(background);
        dayLayout.addView(dayView);

        // 할 일이 있으면 점 표시
        if (hasTodos) {
            View dot = new View(this);
            android.widget.LinearLayout.LayoutParams dotParams = new android.widget.LinearLayout.LayoutParams(8, 8);
            dotParams.setMargins(0, 4, 0, 0);
            dot.setLayoutParams(dotParams);

            GradientDrawable dotBg = new GradientDrawable();
            dotBg.setShape(GradientDrawable.OVAL);
            dotBg.setColor(isSelected ? Color.WHITE : Color.parseColor("#6366F1"));
            dot.setBackground(dotBg);

            dayLayout.addView(dot);
        }

        // 클릭 이벤트
        final int selectedDay = day;
        dayLayout.setOnClickListener(v -> {
            // 이전 선택 해제
            if (selectedDayView != null && selectedDayView != dayLayout) {
                updateCalendar();
            }

            // 새로운 선택
            selectedCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            selectedCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);

            updateCalendar();
            updateSelectedDateText();
            loadTodosForSelectedDate();
        });

        calendarGrid.addView(dayLayout);
    }

    /**
     * 선택된 날짜 텍스트를 업데이트합니다.
     */
    private void updateSelectedDateText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M월 d일", Locale.KOREAN);
        selectedDateText.setText("※ 선택 날짜: " + dateFormat.format(selectedCalendar.getTime()));
    }

    /**
     * 선택된 날짜의 할 일을 로드합니다.
     */
    private void loadTodosForSelectedDate() {
        String dateStr = formatDateWithDay(selectedCalendar.getTime());
        List<TodoItem> todos = dbHelper.getTodosByDate(dateStr);

        // 할 일 목록 업데이트
        todoList.clear();
        todoList.addAll(todos);

        // 어댑터 설정 및 새로고침
        if (todoListView.getAdapter() == null) {
            todoListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 날짜를 yyyy-MM-dd (요일) 형식으로 포맷합니다.
     */
    private String formatDateWithDay(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.KOREAN);

        String formattedDate = dateFormat.format(date);
        String dayOfWeek = dayFormat.format(date);

        return formattedDate + " (" + dayOfWeek + ")";
    }

    /**
     * 할 일 추가 다이얼로그를 표시합니다.
     * 캘린더 탭에서는 선택된 날짜로 자동 설정됩니다.
     */
    private void showAddTodoDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_todo);
        dialog.getWindow()
                .setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        android.widget.EditText todoInput = dialog.findViewById(R.id.todo_input);
        android.widget.TextView deadlineText = dialog.findViewById(R.id.deadline_text);
        android.widget.LinearLayout deadlineLayout = (android.widget.LinearLayout) deadlineText.getParent();
        android.widget.TextView timeText = dialog.findViewById(R.id.time_text);
        android.widget.LinearLayout timeLayout = (android.widget.LinearLayout) timeText.getParent();
        android.widget.Button addBtn = dialog.findViewById(R.id.add_btn_dialog);

        // 마감 기한을 선택된 날짜로 자동 설정
        final Calendar deadline = (Calendar) selectedCalendar.clone();
        deadlineText.setText(formatDateWithDay(deadline.getTime()));

        // 시간 기본값 설정 (17:00)
        final int[] selectedHour = { 17 };
        final int[] selectedMinute = { 0 };
        timeText.setText(
                String.format(java.util.Locale.getDefault(), "오후 %d:%02d", selectedHour[0] - 12, selectedMinute[0]));

        // 날짜 선택 레이아웃 표시 (변경 가능)
        deadlineLayout.setOnClickListener(v -> {
            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        deadline.set(year, month, dayOfMonth);
                        deadlineText.setText(formatDateWithDay(deadline.getTime()));
                    },
                    deadline.get(Calendar.YEAR),
                    deadline.get(Calendar.MONTH),
                    deadline.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

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
                        timeText.setText(
                                String.format(java.util.Locale.getDefault(), "%s %d:%02d", amPm, displayHour, minute));
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
            String deadlineDate = formatDateWithDay(deadline.getTime());

            // 시간을 HH:mm 형식으로 저장
            String deadlineTime = String.format(java.util.Locale.getDefault(), "%02d:%02d", selectedHour[0],
                    selectedMinute[0]);

            // DB에 추가
            long result = dbHelper.addTodo(deadlineDate, deadlineTime, task);
            if (result != -1) {
                loadTodosForSelectedDate();
                updateCalendar();
                TaskFlowUI.showText(this, "추가되었습니다.");
                dialog.dismiss();
            } else {
                TaskFlowUI.showText(this, "추가 실패. 다시 시도해주세요.");
            }
        });

        dialog.show();
    }

    /**
     * 현재 활성화된 네비게이션 버튼을 하이라이트합니다.
     */
    private void highlightCurrentNav() {
        // 캘린더 버튼(todo_btn)을 selected 이미지로 변경
        ImageView calendarIcon = findViewById(R.id.calendar_icon);
        TextView calendarText = findViewById(R.id.calendar_text);

        if (calendarIcon != null) {
            calendarIcon.setImageResource(R.drawable.selected_calendar);
        }
        if (calendarText != null) {
            calendarText.setTextColor(Color.parseColor("#6366F1"));
        }

        // 나머지 버튼들은 nonSelected 이미지로 설정
        ImageView homeIcon = findViewById(R.id.home_icon);
        TextView homeText = findViewById(R.id.home_text);
        ImageView settingIcon = findViewById(R.id.setting_icon);
        TextView settingText = findViewById(R.id.setting_text);

        if (homeIcon != null) {
            homeIcon.setImageResource(R.drawable.nonselected_home);
        }
        if (homeText != null) {
            homeText.setTextColor(Color.parseColor("#6B7280"));
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
        // 돌아왔을 때 할 일 목록 새로고침
        loadTodosForSelectedDate();
    }
}
