package com.example.fnialprojectandriod;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        tvAvatarLetter = findViewById(R.id.tvAvatarLetter);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvStatsEvents = findViewById(R.id.tvStatsEvents);
        tvStatsMemories = findViewById(R.id.tvStatsMemories);
        btnBackProfile = findViewById(R.id.btnBackProfile);
        btnLogout = findViewById(R.id.btnLogout);
        rvProfileEvents = findViewById(R.id.rvProfileEvents);

        String name = user.getDisplayName();
        if (name == null || name.isEmpty()) name = user.getEmail();
        
        tvProfileName.setText(name);
        tvProfileEmail.setText(user.getEmail());
        if (name != null && !name.isEmpty()) {
            tvAvatarLetter.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }

        rvProfileEvents.setLayoutManager(new LinearLayoutManager(this));
        profileEvents = new ArrayList<>();
        adapter = new ProfileEventAdapter(profileEvents);
        rvProfileEvents.setAdapter(adapter);

        btnBackProfile.setOnClickListener(v -> finish());
        
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadUserData(user.getUid());
    }

    private void loadUserData(String uid) {
        db.collection("users").document(uid).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                // 1. טעינת מונה הזיכרונות
                Long memoryCount = userDoc.getLong("memoryCount");
                tvStatsMemories.setText(memoryCount != null ? String.valueOf(memoryCount) : "0");

                // 2. טעינת רשימת ה-IDs של האירועים
                List<String> eventIds = (List<String>) userDoc.get("myEventIds");
                
                if (eventIds != null && !eventIds.isEmpty()) {
                    tvStatsEvents.setText(String.valueOf(eventIds.size()));
                    
                    // 3. משיכת המידע המלא על האירועים מהקולקציה הראשית
                    db.collection("events").whereIn("eventId", eventIds)
                            .get()
                            .addOnSuccessListener(eventsSnapshot -> {
                                profileEvents.clear();
                                for (QueryDocumentSnapshot eventDoc : eventsSnapshot) {
                                    Event event = eventDoc.toObject(Event.class);
                                    profileEvents.add(event);
                                }
                                adapter.notifyDataSetChanged();
                            });
                } else {
                    tvStatsEvents.setText("0");
                    profileEvents.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

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

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, WallActivity.class);
                intent.putExtra("EVENT_CODE", event.getEventCode());
                intent.putExtra("EVENT_NAME", event.getEventName());
                startActivity(intent);
            });
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
