package com.tempegembus.zakaria.tempegembus;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText nama, email, ttl, nip, lokasi, password, re_password;
    Button btn_register;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    TextView jenis_akun;
    RadioGroup radio_group;
    RadioButton pengguna, psikolog;

    MaterialBetterSpinner jenkel;

    FirebaseAuth auth;
    FirebaseUser fuser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daftar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        nama = findViewById(R.id.nama);
        email = findViewById(R.id.email);
        jenkel = findViewById(R.id.jenkel);
        nip = findViewById(R.id.nip);
        lokasi = findViewById(R.id.lokasi);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);
        btn_register = findViewById(R.id.btn_register);

        jenis_akun = findViewById(R.id.jenis_akun);
        pengguna = findViewById(R.id.pengguna);
        psikolog = findViewById(R.id.psikolog);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

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

        //cobacobi
//        jenkel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (jenkel.getAdapter().toString().equals("Perempuan")) {
//                    nip.setHint("cewe");
//                } else if (jenkel.getAdapter().toString().equals("Laki-laki")) {
//                    nip.setHint("cowo");
//                }
//            }
//        });
//
//        jenkel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//                String mSelectedText = adapterView.getItemAtPosition(position).toString();
//                int mSelectedId = position;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        radio_group = findViewById(R.id.radio_group);
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch (checkedId) {
                    case R.id.pengguna:
//                        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
                        nip.setHint("NIP / NIK");
                        nip.setFloatingLabelText("NIP / NIK");
                        jenis_akun.setText("pengguna");
                        break;
                    case R.id.psikolog:
//                        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
                        nip.setHint("SIPP");
                        nip.setFloatingLabelText("SIPP");
                        jenis_akun.setText("psikolog");
                        break;
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();

                String txt_nama = nama.getText().toString();
                String txt_email = email.getText().toString();
                String txt_ttl = ttl.getText().toString();
                String txt_jenkel = jenkel.getText().toString();
                String txt_nip = nip.getText().toString();
                String txt_lokasi = lokasi.getText().toString();
                String txt_password = password.getText().toString();
                String txt_re_password = re_password.getText().toString();
                String txt_jenis_akun = jenis_akun.getText().toString();

                if (TextUtils.isEmpty(txt_nama) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_ttl) ||
                        TextUtils.isEmpty(txt_jenkel) || TextUtils.isEmpty(txt_nip) || TextUtils.isEmpty(txt_lokasi) ||
                        TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_re_password)) {
                    Toast.makeText(RegisterActivity.this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Kata sandi minimal 6 karakter", Toast.LENGTH_SHORT).show();
                } else {
                    register(txt_nama, txt_email, txt_ttl, txt_jenkel, txt_nip, txt_lokasi, txt_password, txt_re_password, txt_jenis_akun);
                    pd.dismiss();
                }
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

    private void register(final String nama, final String email, final String ttl, final String jenkel,
                          final String nip, final String lokasi, final String password, String re_password, final String jenis_akun) {

        auth.createUserWithEmailAndPassword(email, re_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//                                                Toast.makeText(RegisterActivity.this, "Registered successfully. Please check your email for verification",
//                                                        Toast.LENGTH_LONG).show();
                                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                                assert firebaseUser != null;
                                                String userid = firebaseUser.getUid();

                                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("id", userid);
                                                hashMap.put("nama", nama);
                                                hashMap.put("email", email);
                                                hashMap.put("ttl", ttl);
                                                hashMap.put("jenkel", jenkel);
                                                hashMap.put("nip", nip);
                                                hashMap.put("lokasi", lokasi);
                                                hashMap.put("password", password);
                                                hashMap.put("imageURL", "default");
                                                hashMap.put("status", "offline");
                                                hashMap.put("search", nama.toLowerCase());
                                                hashMap.put("jenisAkun", jenis_akun);

                                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });


                        } else {
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}