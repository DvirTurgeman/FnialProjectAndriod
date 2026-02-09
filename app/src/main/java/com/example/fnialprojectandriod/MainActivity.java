package com.example.fnialprojectandriod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private Button btnJoin;
    private RelativeLayout btnCreateEventContainer;
    private ImageView ivNotification;
    private LinearLayout navMyEvents;

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
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvGreeting = findViewById(R.id.tvGreeting);
        btnJoin = findViewById(R.id.btnJoin);
        btnCreateEventContainer = findViewById(R.id.btnCreateEventContainer);
        ivNotification = findViewById(R.id.ivNotification);
        navMyEvents = findViewById(R.id.navMyEvents);

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
        btnJoin.setOnClickListener(v -> Toast.makeText(this, "מנסה להתחבר לאירוע...", Toast.LENGTH_SHORT).show());

        btnCreateEventContainer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        ivNotification.setOnClickListener(v -> Toast.makeText(this, "אין התראות חדשות", Toast.LENGTH_SHORT).show());

        navMyEvents.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyEventsActivity.class);
            startActivity(intent);
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
