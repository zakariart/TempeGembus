package com.tempegembus.zakaria.tempegembus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class InformedConsentActivity extends AppCompatActivity {

    Button btn_kirim, btn_kembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informed_consent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Informed Consent");

        btn_kirim = findViewById(R.id.btn_kirim);
        btn_kembali = findViewById(R.id.btn_kembali);

        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InformedConsentActivity.this, MainActivity.class));
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InformedConsentActivity.this, TambahDataSiswa.class));
            }
        });
    }
}

