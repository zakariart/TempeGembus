package com.tempegembus.zakaria.tempegembus;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tempegembus.zakaria.tempegembus.Model.User;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class UbahProfilActivity extends AppCompatActivity {

    MaterialEditText nama, email, ttl, nip, lokasi, password, re_password;
    Button btn_ubah;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    MaterialBetterSpinner jenkel;

    FirebaseAuth auth;
    FirebaseUser fuser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ubah Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(UbahProfilActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
        });

        nama = findViewById(R.id.nama);
        email = findViewById(R.id.email);
        jenkel = findViewById(R.id.jenkel);
        nip = findViewById(R.id.nip);
        lokasi = findViewById(R.id.lokasi);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);
        btn_ubah = findViewById(R.id.btn_ubah);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nama.setText(user.getNama());
                email.setText(user.getEmail());
//                ttl.setText(user.getTtl());
                jenkel.setText(user.getJenkel());
                nip.setText(user.getNip());
                lokasi.setText(user.getLokasi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();

                String txt_nama = nama.getText().toString();
                String txt_email = email.getText().toString();
//                String txt_ttl = ttl.getText().toString();
                String txt_jenkel = jenkel.getText().toString();
                String txt_nip = nip.getText().toString();
                String txt_lokasi = lokasi.getText().toString();
                String txt_password = password.getText().toString();
                String txt_re_password = re_password.getText().toString();

                ubahProfile(txt_nama, txt_email, txt_jenkel, txt_nip, txt_lokasi, txt_password, txt_re_password);
                pd.dismiss();

            }

        });
    }

    private void ubahProfile(final String nama, final String email, final String jenkel,
                             final String nip, final String lokasi, final String password, String re_password) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());


        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("id", userid);
        hashMap.put("nama", nama);
        hashMap.put("email", email);
//        hashMap.put("ttl", ttl);
        hashMap.put("jenkel", jenkel);
        hashMap.put("nip", nip);
        hashMap.put("lokasi", lokasi);
//        hashMap.put("password", password);

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(UbahProfilActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


}

