package com.example.fnialprojectandriod;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class WallActivity extends AppCompatActivity {

    private TextView tvWallEventName;
    private ImageView btnBackWall;
    private RecyclerView rvWall;
    private FloatingActionButton fabAddMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        // אתחול רכיבים
        tvWallEventName = findViewById(R.id.tvWallEventName);
        btnBackWall = findViewById(R.id.btnBackWall);
        rvWall = findViewById(R.id.rvWall);
        fabAddMemory = findViewById(R.id.fabAddMemory);

        // קבלת נתונים מה-Intent
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        if (eventName != null) {
            tvWallEventName.setText(eventName);
        }

        btnBackWall.setOnClickListener(v -> finish());

        // הגדרת הרשימה (בינתיים ריקה)
        rvWall.setLayoutManager(new LinearLayoutManager(this));
        
        // כאן בהמשך נחבר את ה-Adapter של התמונות מה-Firestore
    }
}
