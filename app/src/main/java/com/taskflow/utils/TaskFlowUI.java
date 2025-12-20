package com.taskflow.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import com.taskflow.R;
import com.taskflow.activities.main.MainActivity;
import com.taskflow.activities.todo.TodoListActivity;
import com.taskflow.activities.settings.SettingActivity;
import java.util.HashMap;
import java.util.Map;

public class TaskFlowUI {
    public static String date;

    /**
     * 하단 네비게이션 바 버튼 초기화 및 클릭 리스너 설정
     * 액티비티 생성 시 한 번 호출하여 리스너를 부착합니다.
     */
    public static void initBottomNav(AppCompatActivity activity) {
        Map<Integer, Class<?>> btnMap = new HashMap<>();
        btnMap.put(R.id.home_btn, TodoListActivity.class);
        btnMap.put(R.id.calendar_btn, MainActivity.class);
        btnMap.put(R.id.my_page_btn, SettingActivity.class);

        for (Map.Entry<Integer, Class<?>> entry : btnMap.entrySet()) {
            android.view.View button = activity.findViewById(entry.getKey());
            if (button != null) {
                button.setOnClickListener(v -> {

                    // 같은 화면이면 아무것도 하지 않음
                    if (activity.getClass().equals(entry.getValue())) { return; }

                    Intent intent = new Intent(activity, entry.getValue());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    activity.startActivity(intent);
                    activity.overridePendingTransition(0,0);//즉시 전환용
                });
            }
        }
    }

    /**
     * 입력받은 메세지를 토스트 안내창으로 표시합니다.
     */
    public static void showText(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
