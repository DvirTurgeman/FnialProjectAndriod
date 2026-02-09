package com.example.fnialprojectandriod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private Button btnJoin;
    private RelativeLayout btnCreateEventContainer;
    private ImageView ivNotification;
    private LinearLayout navMyEvents, navProfile;
    private EditText code1, code2, code3, code4, code5, code6;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    updateUI(FirebaseAuth.getInstance().getCurrentUser());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול רכיבים
        tvGreeting = findViewById(R.id.tvGreeting);
        btnJoin = findViewById(R.id.btnJoin);
        btnCreateEventContainer = findViewById(R.id.btnCreateEventContainer);
        ivNotification = findViewById(R.id.ivNotification);
        navMyEvents = findViewById(R.id.navMyEvents);
        navProfile = findViewById(R.id.navProfile);
        
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startSignIn();
        } else {
            updateUI(currentUser);
        }

        setupClickListeners();
    }

    private void startSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void setupClickListeners() {
        btnJoin.setOnClickListener(v -> joinEventByCode());

        btnCreateEventContainer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        ivNotification.setOnClickListener(v -> Toast.makeText(this, "אין התראות חדשות", Toast.LENGTH_SHORT).show());

        navMyEvents.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyEventsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void joinEventByCode() {
        String code = code1.getText().toString() + code2.getText().toString() + 
                     code3.getText().toString() + code4.getText().toString() + 
                     code5.getText().toString() + code6.getText().toString();

        if (code.length() < 6) {
            Toast.makeText(this, "נא להזין קוד מלא (6 ספרות)", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("events").document(code).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        db.collection("users").document(uid).collection("myEvents").document(code)
                                .set(event)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "הצטרפת לאירוע בהצלחה!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, WallActivity.class);
                                    intent.putExtra("EVENT_CODE", code);
                                    intent.putExtra("EVENT_NAME", event.getEventName());
                                    startActivity(intent);
                                });
                    } else {
                        Toast.makeText(this, "קוד לא תקין, אירוע לא נמצא", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            if (name == null || name.isEmpty()) {
                name = user.getEmail();
            }
            tvGreeting.setText("שלום, " + name);
        }
    }
}
