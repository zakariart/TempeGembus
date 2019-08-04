package com.tempegembus.zakaria.tempegembus;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tempegembus.zakaria.tempegembus.Model.User;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class UbahProfilActivity extends AppCompatActivity {

    MaterialEditText nama, email, ttl, nip, lokasi, password;
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
//        email = findViewById(R.id.email);
        jenkel = findViewById(R.id.jenkel);
        nip = findViewById(R.id.nip);
        lokasi = findViewById(R.id.lokasi);
        password = findViewById(R.id.password);
        btn_ubah = findViewById(R.id.btn_ubah);

        auth = FirebaseAuth.getInstance();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        ttl = findViewById(R.id.ttl);
        ttl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        String[] list = getResources().getStringArray(R.array.gender);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, list);

        jenkel.setAdapter(adapter);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                nama.setText(user.getNama());
//                email.setText(user.getEmail());
                ttl.setText(user.getTtl());
                jenkel.setText(user.getJenkel());
                nip.setText(user.getNip());
                lokasi.setText(user.getLokasi());

                btn_ubah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String txt_nama = nama.getText().toString();
//                        String txt_email = email.getText().toString();
                        String txt_ttl = ttl.getText().toString();
                        String txt_jenkel = jenkel.getText().toString();
                        String txt_nip = nip.getText().toString();
                        String txt_lokasi = lokasi.getText().toString();
                        String txt_password = password.getText().toString();

                        if (txt_password.equals(user.getPassword())) {
                            ubahProfile(txt_nama, txt_ttl, txt_jenkel, txt_nip, txt_lokasi, txt_password);
                            Toast.makeText(UbahProfilActivity.this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UbahProfilActivity.this, "Kata Sandi salah!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDateDialog() {

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                ttl.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    private void ubahProfile(final String nama, final String ttl, final String jenkel,
                             final String nip, final String lokasi, final String password) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nama", nama);
//        hashMap.put("email", email);
        hashMap.put("ttl", ttl);
        hashMap.put("jenkel", jenkel);
        hashMap.put("nip", nip);
        hashMap.put("lokasi", lokasi);

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

