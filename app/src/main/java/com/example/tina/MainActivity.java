package com.example.tina;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;


    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                loadFragment(new Home());
                return true;
            } else if (item.getItemId() == R.id.menu_chatbot) {
                loadFragment(new Chatbot());
                return true;
            } else if (item.getItemId() == R.id.menu_perfil) {
                loadFragment(new Perfil());
                return true;
            } else if (item.getItemId() == R.id.menu_eventos) {
                loadFragment(new Eventos());
                return true;
            } else if (item.getItemId() == R.id.menu_cardapio) {
                loadFragment(new Cardapio());
                return true;
            }
            return false;
        });
            loadFragment(new Home());
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentuser == null) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
    }
}