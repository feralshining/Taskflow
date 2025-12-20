package com.taskflow.activities.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.taskflow.R;
import com.taskflow.utils.TaskFlowUI;

public class SettingActivity extends AppCompatActivity {

    @SuppressLint({ "CutPasteId", "UseSwitchCompatOrMaterialCode" })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // [ 하단 네비게이션 ]
        TaskFlowUI.initBottomNav(this);
        highlightCurrentNav();

        // 테마 변경 클릭 (현재는 라이트 테마만)
        CardView themeCard = findViewById(R.id.theme_card);
        themeCard.setOnClickListener(v -> {
            Toast.makeText(this, "현재 라이트 테마만 지원됩니다", Toast.LENGTH_SHORT).show();
        });

        // 데이터 백업 & 복원 클릭
        CardView backupCard = findViewById(R.id.backup_card);
        backupCard.setOnClickListener(v -> {
            Toast.makeText(this, "데이터 백업 기능 준비 중입니다", Toast.LENGTH_SHORT).show();
        });

        // 로그아웃 버튼
        Button logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(v -> {
            Toast.makeText(this, "로그아웃 기능 준비 중입니다", Toast.LENGTH_SHORT).show();
        });

        // 뒤로 가기 버튼 처리
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing - 뒤로 가기 버튼 비활성화
            }
        });
    }

    /**
     * 현재 활성화된 네비게이션 버튼을 하이라이트합니다.
     */
    private void highlightCurrentNav() {
        // 내정보 버튼(my_page_btn)을 selected 이미지로 변경
        android.widget.ImageView settingIcon = findViewById(R.id.setting_icon);
        TextView settingText = findViewById(R.id.setting_text);

        if (settingIcon != null) {
            settingIcon.setImageResource(R.drawable.selected_user);
        }
        if (settingText != null) {
            settingText.setTextColor(android.graphics.Color.parseColor("#6366F1"));
        }

        // 나머지 버튼들은 nonSelected 이미지로 설정
        android.widget.ImageView homeIcon = findViewById(R.id.home_icon);
        TextView homeText = findViewById(R.id.home_text);
        android.widget.ImageView calendarIcon = findViewById(R.id.calendar_icon);
        TextView calendarText = findViewById(R.id.calendar_text);

        if (homeIcon != null) {
            homeIcon.setImageResource(R.drawable.nonselected_home);
        }
        if (homeText != null) {
            homeText.setTextColor(android.graphics.Color.parseColor("#6B7280"));
        }
        if (calendarIcon != null) {
            calendarIcon.setImageResource(R.drawable.nonselected_calendar);
        }
        if (calendarText != null) {
            calendarText.setTextColor(android.graphics.Color.parseColor("#6B7280"));
        }
    }
}
