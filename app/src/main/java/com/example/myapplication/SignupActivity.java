package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.inputUsername.getText().toString();
                String name = binding.inputName.getText().toString();
                String password = binding.inputPassword.getText().toString();
                String confirm = binding.inputConfirm.getText().toString();

                if (username.equals("") || name.equals("") || password.equals("") || confirm.equals(""))
                    Toast.makeText(SignupActivity.this, "All field are mandatory", Toast.LENGTH_SHORT).show();
                else {
                    if (password.equals(confirm)) {
                        Boolean checkUsername = databaseHelper.checkUsername(username);

                        if (checkUsername == false) {
                            boolean insert = databaseHelper.insertData(username, name, password, confirm);

                            if (insert == true) {
                                Toast.makeText(SignupActivity.this, "RegisterSuccess", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignupActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Username cannot same with pass", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "invalid password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}