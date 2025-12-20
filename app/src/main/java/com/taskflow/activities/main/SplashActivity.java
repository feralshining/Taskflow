package com.taskflow.activities.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.core.splashscreen.SplashScreen;
import com.taskflow.activities.todo.TodoListActivity;

public class SplashActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // TodoListActivity로 직접 이동
        Intent intent = new Intent(this, TodoListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
