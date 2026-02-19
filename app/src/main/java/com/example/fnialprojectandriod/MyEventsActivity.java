package com.example.fnialprojectandriod;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView rvMyEvents;
    private EventAdapter adapter;
    private List<Event> eventList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        db = FirebaseFirestore.getInstance();
        rvMyEvents = findViewById(R.id.rvMyEvents);
        rvMyEvents.setLayoutManager(new LinearLayoutManager(this));
        
        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList);
        rvMyEvents.setAdapter(adapter);

        loadMyEvents();
    }

    private void loadMyEvents() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("users").document(uid).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists() && userDoc.contains("myEventIds")) {
                List<String> eventIds = (List<String>) userDoc.get("myEventIds");

                if (eventIds != null && !eventIds.isEmpty()) {
                    db.collection("events").whereIn("eventId", eventIds)
                            .get()
                            .addOnSuccessListener(eventsSnapshot -> {
                                eventList.clear(); // Clear the list that the adapter uses
                                for (QueryDocumentSnapshot eventDoc : eventsSnapshot) {
                                    Event event = eventDoc.toObject(Event.class);
                                    eventList.add(event); // Add the new Event object to the correct list
                                }
                                adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                            })
                            .addOnFailureListener(e -> {
                                // It's good practice to log errors
                                android.util.Log.e("MyEventsActivity", "Error loading event details", e);
                            });
                }
            }
        }).addOnFailureListener(e -> {
            android.util.Log.e("MyEventsActivity", "Error loading user document", e);
        });
    }



    // Adapter פנימי לצורך הפשטות
    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
        private List<Event> list;

        public EventAdapter(List<Event> list) { this.list = list; }

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
                Intent intent = new Intent(MyEventsActivity.this, WallActivity.class);
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
