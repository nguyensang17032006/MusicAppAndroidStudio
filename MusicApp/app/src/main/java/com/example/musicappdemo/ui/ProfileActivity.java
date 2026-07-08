package com.example.musicappdemo.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicappdemo.R;
import com.example.musicappdemo.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Setup Gender Dropdown
        String[] genders = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        binding.actvGender.setAdapter(adapter);

        // Edit/Save Toggle Logic
        binding.btnEditSave.setOnClickListener(v -> {
            if (!isEditMode) {
                enterEditMode();
            } else {
                saveProfileChanges();
            }
        });

        binding.btnLogout.setOnClickListener(v -> finish());
    }

    private void enterEditMode() {
        isEditMode = true;
        binding.btnEditSave.setText("Save");
        binding.btnEditSave.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // Hide pencil icon
        
        // Enable fields
        binding.etProfileEmail.setEnabled(true);
        binding.actvGender.setEnabled(true);
        
        Toast.makeText(this, "You can edit now", Toast.LENGTH_SHORT).show();
    }

    private void saveProfileChanges() {
        // Logic to save (Simulated)
        boolean success = true; // Assume success for now

        if (success) {
            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
            exitEditMode();
        } else {
            Toast.makeText(this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void exitEditMode() {
        isEditMode = false;
        binding.btnEditSave.setText("Edit");
        binding.btnEditSave.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
        
        // Disable fields
        binding.etProfileEmail.setEnabled(false);
        binding.actvGender.setEnabled(false);
    }
}