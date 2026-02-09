package com.example.fnialprojectandriod;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AddMemoryActivity extends AppCompatActivity {

    private ImageView ivSelectedImage, btnBackAdd;
    private LinearLayout layoutPlaceholder;
    private EditText etGreeting;
    private Button btnPost;
    private Uri imageUri;

    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        layoutPlaceholder = findViewById(R.id.layoutPlaceholder);
        etGreeting = findViewById(R.id.etGreeting);
        btnPost = findViewById(R.id.btnPost);
        btnBackAdd = findViewById(R.id.btnBackAdd);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        ivSelectedImage.setImageURI(imageUri);
                        ivSelectedImage.setVisibility(View.VISIBLE);
                        layoutPlaceholder.setVisibility(View.GONE);
                    }
                });

        layoutPlaceholder.setOnClickListener(v -> mGetContent.launch("image/*"));
        ivSelectedImage.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnBackAdd.setOnClickListener(v -> finish());
        btnPost.setOnClickListener(v -> uploadImageAndPost());
    }

    private void uploadImageAndPost() {
        String greeting = etGreeting.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String eventCode = getIntent().getStringExtra("EVENT_CODE");

        if (imageUri == null) {
            Toast.makeText(this, "נא לבחור תמונה", Toast.LENGTH_SHORT).show();
            return;
        }
        if (greeting.isEmpty()) {
            Toast.makeText(this, "נא לכתוב ברכה", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPost.setEnabled(false);
        Toast.makeText(this, "מעלה זיכרון...", Toast.LENGTH_SHORT).show();

        // העלאה ל-Storage (כדי שזה יעבוד, וודא שיש לך Firebase Storage מוגדר בפרויקט)
        String fileName = "images/" + UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveMemoryToFirestore(greeting, imageUrl, user.getDisplayName(), eventCode);
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "העלאת התמונה נכשלה. וודא ש-Storage מוגדר.", Toast.LENGTH_LONG).show();
                    btnPost.setEnabled(true);
                });
    }

    private void saveMemoryToFirestore(String greeting, String imageUrl, String userName, String eventCode) {
        if (userName == null || userName.isEmpty()) userName = "אורח";
        
        Memory memory = new Memory(greeting, imageUrl, userName, System.currentTimeMillis());

        FirebaseFirestore.getInstance().collection("events").document(eventCode).collection("memories")
                .add(memory)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "הזיכרון פורסם בהצלחה!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בפרסום", Toast.LENGTH_SHORT).show();
                    btnPost.setEnabled(true);
                });
    }
}
