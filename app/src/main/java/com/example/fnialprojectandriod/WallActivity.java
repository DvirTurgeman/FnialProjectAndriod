package com.example.fnialprojectandriod;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WallActivity extends AppCompatActivity {

    private TextView tvWallEventName;
    private ImageView btnBackWall;
    private RecyclerView rvWall;
    private FloatingActionButton fabAddMemory;

    private String eventCode;
    private FirebaseFirestore db;
    private MemoryAdapter adapter;
    private List<Memory> memoryList;
    private ListenerRegistration memoryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        db = FirebaseFirestore.getInstance();

        // אתחול רכיבים
        tvWallEventName = findViewById(R.id.tvWallEventName);
        btnBackWall = findViewById(R.id.btnBackWall);
        rvWall = findViewById(R.id.rvWall);
        fabAddMemory = findViewById(R.id.fabAddMemory);

        // קבלת נתונים מה-Intent
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        eventCode = getIntent().getStringExtra("EVENT_CODE");

        if (eventName != null) {
            tvWallEventName.setText(eventName);
        }

        btnBackWall.setOnClickListener(v -> finish());

        // חיבור כפתור הוספת זיכרון
        fabAddMemory.setOnClickListener(v -> {
            if (eventCode != null) {
                Intent intent = new Intent(WallActivity.this, AddMemoryActivity.class);
                intent.putExtra("EVENT_CODE", eventCode);
                startActivity(intent);
            }
        });

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        memoryList = new ArrayList<>();
        adapter = new MemoryAdapter(memoryList);
        rvWall.setLayoutManager(new LinearLayoutManager(this));
        rvWall.setAdapter(adapter);
    }

    private void listenForMemories() {
        if (eventCode == null) return;

        // האזנה בזמן אמת לעדכונים בקיר
        memoryListener = db.collection("events").document(eventCode).collection("memories")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;

                    memoryList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Memory memory = doc.toObject(Memory.class);
                        memoryList.add(memory);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenForMemories(); // התחל להאזין כשהמסך נראה
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (memoryListener != null) {
            memoryListener.remove(); // הפסק להאזין כדי לחסוך במשאבים
        }
    }

    // Adapter פנימי להצגת הזיכרונות
    private class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {
        private List<Memory> list;

        public MemoryAdapter(List<Memory> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Memory memory = list.get(position);
            holder.tvGreeting.setText(memory.getGreeting());
            holder.tvUser.setText("נשלח על ידי " + memory.getUserName());

            // הצגת התמונה מהאינטרנט באמצעות Glide
            Glide.with(holder.itemView.getContext())
                    .load(memory.getImageUrl())
                    .into(holder.ivImage);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvGreeting, tvUser;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.ivMemoryImage);
                tvGreeting = itemView.findViewById(R.id.tvMemoryGreeting);
                tvUser = itemView.findViewById(R.id.tvMemoryUser);
            }
        }
    }
}
