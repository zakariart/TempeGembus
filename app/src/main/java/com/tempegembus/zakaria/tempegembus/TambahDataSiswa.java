package com.tempegembus.zakaria.tempegembus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.HashMap;

public class TambahDataSiswa extends AppCompatActivity {

    MaterialEditText nama_siswa, email_ortu, usia_siswa, nisn;
    MaterialBetterSpinner jenkel_siswa;
    Button btn_tambah_siswa;

    FirebaseAuth auth;
    FirebaseUser fuser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_data_siswa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tambah Data Siswa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(TambahDataSiswa.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        nama_siswa = findViewById(R.id.nama_siswa);
        email_ortu = findViewById(R.id.email_ortu);
        jenkel_siswa = findViewById(R.id.jenkel_siswa);
        usia_siswa = findViewById(R.id.usia_siswa);
        nisn = findViewById(R.id.nisn);
        btn_tambah_siswa = findViewById(R.id.btn_tambah_siswa);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        String[] list = getResources().getStringArray(R.array.gender);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list);

        jenkel_siswa.setAdapter(adapter);

        btn_tambah_siswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();

                String txt_nama_siswa = nama_siswa.getText().toString();
                String txt_email_ortu = email_ortu.getText().toString();
                String txt_usia_siswa = usia_siswa.getText().toString();
                String txt_jenkel_siswa = jenkel_siswa.getText().toString();
                String txt_nisn = nisn.getText().toString();

                if (TextUtils.isEmpty(txt_nama_siswa) || TextUtils.isEmpty(txt_email_ortu) || TextUtils.isEmpty(txt_usia_siswa) ||
                        TextUtils.isEmpty(txt_jenkel_siswa) || TextUtils.isEmpty(txt_nisn)) {
                    Toast.makeText(TambahDataSiswa.this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    tambahsiswa(txt_nama_siswa, txt_email_ortu, txt_usia_siswa, txt_jenkel_siswa, txt_nisn);
                    pd.dismiss();
                }
            }
        });
    }

    private void tambahsiswa(final String namaSiswa, final String emailOrtu, final String usiaSiswa,
                             final String jenkelSiswa, final String nisn) {

        DatabaseReference siswaPush = reference.push();
        String idSiswa = siswaPush.getKey();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Siswa");


        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("idSiswa", idSiswa);
        hashMap.put("namaSiswa", namaSiswa);
        hashMap.put("emailOrtu", emailOrtu);
        hashMap.put("usiaSiswa", usiaSiswa + " Tahun");
        hashMap.put("jenkelSiswa", jenkelSiswa);
        hashMap.put("nisn", nisn);
        hashMap.put("imageURLsiswa", "default");

//                            reference.updateChildren(hashMap);
        reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(TambahDataSiswa.this, InformedConsentActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}