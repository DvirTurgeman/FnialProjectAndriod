package com.example.fnialprojectandriod;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WallActivity extends AppCompatActivity {

    private TextView tvWallEventName;
    private ImageView btnBackWall, btnDeleteEvent;
    private RecyclerView rvWall;
    private FloatingActionButton fabAddMemory;

    private String eventCode;
    private String eventCreatorUid;
    private FirebaseFirestore db;
    private MemoryAdapter adapter;
    private List<Memory> memoryList;
    private ListenerRegistration memoryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        db = FirebaseFirestore.getInstance();

        tvWallEventName = findViewById(R.id.tvWallEventName);
        btnBackWall = findViewById(R.id.btnBackWall);
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent);
        rvWall = findViewById(R.id.rvWall);
        fabAddMemory = findViewById(R.id.fabAddMemory);

        String eventName = getIntent().getStringExtra("EVENT_NAME");
        eventCode = getIntent().getStringExtra("EVENT_CODE");

        if (eventName != null) {
            tvWallEventName.setText(eventName);
        }

        btnBackWall.setOnClickListener(v -> finish());
        btnDeleteEvent.setOnClickListener(v -> confirmDeleteEvent());

        fabAddMemory.setOnClickListener(v -> {
            if (eventCode != null) {
                Intent intent = new Intent(WallActivity.this, AddMemoryActivity.class);
                intent.putExtra("EVENT_CODE", eventCode);
                startActivity(intent);
            }
        });

        setupRecyclerView();
        fetchEventCreator();
    }

    private void fetchEventCreator() {
        if (eventCode == null) return;
        db.collection("events").document(eventCode).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                eventCreatorUid = documentSnapshot.getString("creatorUid");
                String currentUid = FirebaseAuth.getInstance().getUid();
                
                // הצגת כפתור מחיקת אירוע רק למנהל
                if (currentUid != null && currentUid.equals(eventCreatorUid)) {
                    btnDeleteEvent.setVisibility(View.VISIBLE);
                }
                
                if (adapter != null) adapter.notifyDataSetChanged();
            }
        });
    }

    private void confirmDeleteEvent() {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת אירוע")
                .setMessage("האם אתה בטוח שברצונך למחוק את כל האירוע? פעולה זו תמחוק את כל התמונות ותסיר את האירוע מכל המשתתפים.")
                .setPositiveButton("מחק הכל", (dialog, which) -> deleteFullEvent())
                .setNegativeButton("ביטול", null)
                .show();
    }

    private void deleteFullEvent() {
        if (eventCode == null) return;
        Toast.makeText(this, "מוחק אירוע, אנא המתן...", Toast.LENGTH_LONG).show();

        // 1. קבלת כל הזיכרונות כדי לדעת כמה להוריד לכל משתמש
        db.collection("events").document(eventCode).collection("memories").get()
                .addOnSuccessListener(memorySnapshots -> {
                    Map<String, Integer> userMemoryCounts = new HashMap<>();
                    for (QueryDocumentSnapshot doc : memorySnapshots) {
                        String uploader = doc.getString("uploaderUid");
                        if (uploader != null) {
                            userMemoryCounts.put(uploader, userMemoryCounts.getOrDefault(uploader, 0) + 1);
                        }
                    }

                    // 2. מציאת כל המשתמשים שהשתתפו באירוע
                    db.collection("users").whereArrayContains("myEventIds", eventCode).get()
                            .addOnSuccessListener(userSnapshots -> {
                                WriteBatch batch = db.batch();

                                // עדכון כל משתמש: הסרת האירוע והפחתת זיכרונות
                                for (QueryDocumentSnapshot userDoc : userSnapshots) {
                                    String userId = userDoc.getId();
                                    int memoriesToRemove = userMemoryCounts.getOrDefault(userId, 0);
                                    
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("myEventIds", FieldValue.arrayRemove(eventCode));
                                    if (memoriesToRemove > 0) {
                                        updates.put("memoryCount", FieldValue.increment(-memoriesToRemove));
                                    }
                                    batch.update(userDoc.getReference(), updates);
                                }

                                // 3. מחיקת כל הזיכרונות
                                for (QueryDocumentSnapshot memDoc : memorySnapshots) {
                                    batch.delete(memDoc.getReference());
                                }

                                // 4. מחיקת האירוע עצמו
                                batch.delete(db.collection("events").document(eventCode));

                                // ביצוע הכל כפעולה אחת (Batch)
                                batch.commit().addOnSuccessListener(aVoid -> {
                                    Toast.makeText(WallActivity.this, "האירוע נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                                    finish();
                                }).addOnFailureListener(e -> Toast.makeText(WallActivity.this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show());
                            });
                });
    }

    private void setupRecyclerView() {
        memoryList = new ArrayList<>();
        adapter = new MemoryAdapter(memoryList);
        rvWall.setLayoutManager(new LinearLayoutManager(this));
        rvWall.setAdapter(adapter);
    }

    private void listenForMemories() {
        if (eventCode == null) return;
        memoryListener = db.collection("events").document(eventCode).collection("memories")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    memoryList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        memoryList.add(doc.toObject(Memory.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void deleteMemory(Memory memory) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת זיכרון")
                .setMessage("האם אתה בטוח שברצונך למחוק את הברכה הזו?")
                .setPositiveButton("מחק", (dialog, which) -> {
                    db.collection("events").document(eventCode).collection("memories")
                            .document(memory.getMemoryId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                String uploaderUid = memory.getUploaderUid();
                                if (uploaderUid != null) {
                                    db.collection("users").document(uploaderUid)
                                            .update("memoryCount", FieldValue.increment(-1));
                                }
                                Toast.makeText(this, "הזיכרון נמחק", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenForMemories();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (memoryListener != null) memoryListener.remove();
    }

    private class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {
        private List<Memory> list;
        public MemoryAdapter(List<Memory> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Memory memory = list.get(position);
            String currentUid = FirebaseAuth.getInstance().getUid();

            holder.tvGreeting.setText(memory.getGreeting());
            holder.tvUser.setText("נשלח על ידי " + memory.getUserName());
            Glide.with(holder.itemView.getContext()).load(memory.getImageUrl()).centerCrop().into(holder.ivImage);

            boolean isUploader = currentUid != null && currentUid.equals(memory.getUploaderUid());
            boolean isEventCreator = currentUid != null && currentUid.equals(eventCreatorUid);

            if (isUploader || isEventCreator) {
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> deleteMemory(memory));
            } else {
                holder.btnDelete.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage, btnDelete;
            TextView tvGreeting, tvUser;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.ivMemoryImage);
                tvGreeting = itemView.findViewById(R.id.tvMemoryGreeting);
                tvUser = itemView.findViewById(R.id.tvMemoryUser);
                btnDelete = itemView.findViewById(R.id.btnDeleteMemory);
            }
        }
    }
}
