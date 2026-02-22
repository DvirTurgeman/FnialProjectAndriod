package com.example.fnialprojectandriod;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private Button btnJoin;
    private RelativeLayout btnCreateEventContainer;
    private LinearLayout navMyEvents, navProfile;
    private EditText code1, code2, code3, code4, code5, code6;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // התחברות הצליחה - עכשיו אפשר לאתחל את האפליקציה
                    initApp(FirebaseAuth.getInstance().getCurrentUser());
                } else {
                    Toast.makeText(this, "חובה להתחבר כדי להשתמש באפליקציה", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // בדיקה ראשונית - האם המשתמש מחובר? (לפני setContentView)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startSignIn();
        } else {
            // אם הוא כבר מחובר, נטען את הממשק מיד
            initApp(currentUser);
        }
    }

    private void initApp(FirebaseUser user) {
        // פונקציה זו טוענת את הממשק רק כשיש משתמש מחובר
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבים (findViewById יעבוד רק אחרי setContentView)
        tvGreeting = findViewById(R.id.tvGreeting);
        btnJoin = findViewById(R.id.btnJoin);
        btnCreateEventContainer = findViewById(R.id.btnCreateEventContainer);
        navMyEvents = findViewById(R.id.navMyEvents);
        navProfile = findViewById(R.id.navProfile);

        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);

        updateUI(user);
        setupClickListeners();
    }

    private void startSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.Theme_FnialProjectAndriod)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void setupClickListeners() {
        btnJoin.setOnClickListener(v -> joinEventByCode());
        btnCreateEventContainer.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateEventActivity.class));
        });
        navMyEvents.setOnClickListener(v -> {
            startActivity(new Intent(this, MyEventsActivity.class));
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void joinEventByCode() {
        String code = code1.getText().toString() + code2.getText().toString() +
                code3.getText().toString() + code4.getText().toString() +
                code5.getText().toString() + code6.getText().toString();

        if (code.length() < 6) {
            Toast.makeText(this, "נא להזין קוד מלא", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        db.collection("events").document(code).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                db.collection("users").document(uid)
                        .update("myEventIds", FieldValue.arrayUnion(code))
                        .addOnSuccessListener(aVoid -> {
                            clearCodeFields();
                            Intent intent = new Intent(this, WallActivity.class);
                            intent.putExtra("EVENT_CODE", code);
                            if (event != null) intent.putExtra("EVENT_NAME", event.getEventName());
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Map<String, Object> data = new HashMap<>();
                            data.put("myEventIds", FieldValue.arrayUnion(code));
                            db.collection("users").document(uid).set(data, SetOptions.merge())
                                    .addOnSuccessListener(aVoid2 -> {
                                        clearCodeFields();
                                        Intent intent = new Intent(this, WallActivity.class);
                                        intent.putExtra("EVENT_CODE", code);
                                        if (event != null) intent.putExtra("EVENT_NAME", event.getEventName());
                                        startActivity(intent);
                                    });
                        });
            } else {
                Toast.makeText(this, "אירוע לא נמצא", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCodeFields() {
        code1.setText(""); code2.setText(""); code3.setText("");
        code4.setText(""); code5.setText(""); code6.setText("");
        code1.requestFocus();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            if (name == null || name.isEmpty()) name = user.getEmail();
            tvGreeting.setText("שלום, " + name);
        }
    }
}