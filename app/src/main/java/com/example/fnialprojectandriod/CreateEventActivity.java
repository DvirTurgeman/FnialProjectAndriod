package com.example.fnialprojectandriod;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etEventName, etEventDate;
    private Button btnCreate, btnShare, btnCopy;
    private LinearLayout layoutSuccess;
    private TextView tvGeneratedCode;
    private ImageView btnBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        db = FirebaseFirestore.getInstance();

        etEventName = findViewById(R.id.etEventName);
        etEventDate = findViewById(R.id.etEventDate);
        btnCreate = findViewById(R.id.btnCreate);
        layoutSuccess = findViewById(R.id.layoutSuccess);
        tvGeneratedCode = findViewById(R.id.tvGeneratedCode);
        btnShare = findViewById(R.id.btnShare);
        btnCopy = findViewById(R.id.btnCopy);
        btnBack = findViewById(R.id.btnBack);

        etEventDate.setOnClickListener(v -> showDatePicker());
        btnBack.setOnClickListener(v -> finish());
        btnCreate.setOnClickListener(v -> createEvent());

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Event Code", tvGeneratedCode.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "הקוד הועתק!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> etEventDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void createEvent() {
        String name = etEventName.getText().toString().trim();
        String date = etEventDate.getText().toString().trim();
        String uid = FirebaseAuth.getInstance().getUid();

        if (name.isEmpty() || date.isEmpty() || uid == null) {
            Toast.makeText(this, "אנא מלא את כל הפרטים", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventCode = String.valueOf(100000 + new Random().nextInt(900000));

        Map<String, Object> event = new HashMap<>();
        event.put("eventName", name);
        event.put("eventDate", date);
        event.put("eventCode", eventCode);
        event.put("creatorUid", uid);
        event.put("createdAt", FieldValue.serverTimestamp());

        // 1. שמירת האירוע באוסף הכללי
        db.collection("events").document(eventCode)
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    // 2. שמירת האירוע תחת "האירועים שלי" של המשתמש
                    db.collection("users").document(uid).collection("myEvents").document(eventCode)
                            .set(event)
                            .addOnSuccessListener(aVoid2 -> {
                                tvGeneratedCode.setText(eventCode);
                                layoutSuccess.setVisibility(View.VISIBLE);
                                btnCreate.setVisibility(View.GONE);
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה ביצירה", Toast.LENGTH_SHORT).show());
    }
}
