package com.tempegembus.zakaria.tempegembus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tempegembus.zakaria.tempegembus.Model.Siswa;

import java.util.HashMap;

public class InformedConsentActivity extends AppCompatActivity {

    Button btn_kirim, btn_kembali;
    TextView textView, nama_siswa, email_ortu;

    Intent intent;
    String userid;

    FirebaseAuth auth;

    DatabaseReference reference;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informed_consent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Informed Consent");

//        textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);

        textView = findViewById(R.id.textView);
        nama_siswa = findViewById(R.id.nama_siswa);
        email_ortu = findViewById(R.id.email_ortu);
        btn_kirim = findViewById(R.id.btn_kirim);
        btn_kembali = findViewById(R.id.btn_kembali);

        final ProgressDialog pd = new ProgressDialog(InformedConsentActivity.this);
        pd.setMessage("Sedang Mengirim Email");

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        auth = FirebaseAuth.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Siswa").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Siswa siswa = dataSnapshot.getValue(Siswa.class);
                nama_siswa.setText(siswa.getNamaSiswa());
                email_ortu.setText(siswa.getEmailOrtu());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                String txt_email_ortu = email_ortu.getText().toString();

                auth.sendPasswordResetEmail(txt_email_ortu).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ubahStatus();
                            pd.dismiss();
                            Toast.makeText(InformedConsentActivity.this, "Email Konfirmasi Terkirim", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(InformedConsentActivity.this, DataSiswaActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));

                        } else {
                            pd.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(InformedConsentActivity.this, error, Toast.LENGTH_SHORT).show();
                            Toast.makeText(InformedConsentActivity.this, "Email Konfirmasi Tidak Terkirim", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(InformedConsentActivity.this, DataSiswaActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));

            }
        });
    }

    private void ubahStatus() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("statusSiswa", "Sudah Konfirmasi");

        reference.updateChildren(map);
    }
}

