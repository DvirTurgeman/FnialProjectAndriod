package com.example.fnialprojectandriod;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvAvatarLetter, tvProfileName, tvProfileEmail, tvStatsEvents, tvStatsMemories, btnBackProfile;
    private Button btnLogout;
    private RecyclerView rvProfileEvents;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Event> profileEvents;
    private ProfileEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            finish();
            return;
        }

        // אתחול רכיבים
        tvAvatarLetter = findViewById(R.id.tvAvatarLetter);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvStatsEvents = findViewById(R.id.tvStatsEvents);
        tvStatsMemories = findViewById(R.id.tvStatsMemories);
        btnBackProfile = findViewById(R.id.btnBackProfile);
        btnLogout = findViewById(R.id.btnLogout);
        rvProfileEvents = findViewById(R.id.rvProfileEvents);

        // הגדרת נתונים בסיסיים
        String name = user.getDisplayName();
        if (name == null || name.isEmpty()) name = user.getEmail();
        
        tvProfileName.setText(name);
        tvProfileEmail.setText(user.getEmail());
        if (name != null && !name.isEmpty()) {
            tvAvatarLetter.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }

        // הגדרת רשימה
        rvProfileEvents.setLayoutManager(new LinearLayoutManager(this));
        profileEvents = new ArrayList<>();
        adapter = new ProfileEventAdapter(profileEvents);
        rvProfileEvents.setAdapter(adapter);

        // כפתורי ניווט
        btnBackProfile.setOnClickListener(v -> finish());
        
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadStatsAndEvents(user.getUid());
    }

    private void loadStatsAndEvents(String uid) {
        // טעינת אירועים ועדכון הסטטיסטיקה
        db.collection("users").document(uid).collection("myEvents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profileEvents.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            profileEvents.add(event);
                        }
                        adapter.notifyDataSetChanged();
                        tvStatsEvents.setText(String.valueOf(profileEvents.size()));
                    }
                });
        
        // כאן בעתיד נוסיף טעינה של מספר התמונות (Memories)
        tvStatsMemories.setText("0");
    }

    // אדפטר פנימי
    private class ProfileEventAdapter extends RecyclerView.Adapter<ProfileEventAdapter.ViewHolder> {
        private List<Event> list;
        public ProfileEventAdapter(List<Event> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Event event = list.get(position);
            holder.tvName.setText(event.getEventName());
            holder.tvDate.setText(event.getEventDate());
            holder.tvCode.setText("#" + event.getEventCode());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDate, tvCode;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvItemEventName);
                tvDate = itemView.findViewById(R.id.tvItemEventDate);
                tvCode = itemView.findViewById(R.id.tvItemEventCode);
            }
        }
    }
}
