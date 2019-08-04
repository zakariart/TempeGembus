package com.tempegembus.zakaria.tempegembus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tempegembus.zakaria.tempegembus.Adapter.LaporanAdapter;
import com.tempegembus.zakaria.tempegembus.Model.Laporan;
import com.tempegembus.zakaria.tempegembus.Model.Siswa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DataSiswaActivity extends AppCompatActivity {

    CircleImageView profile_image_siswa, add_profile_image;
    MaterialEditText nama_siswa, email_ortu, usia_siswa, jenkel_siswa, status_siswa;

//Laporan

    EditText teks_laporan2;
    TextView waktu_laporan2;
    ImageButton btn_send, kembali, hapus;
    RecyclerView recycler_view_lap;

    private LaporanAdapter laporanAdapter;
    private List<Laporan> mLaporan;

    FirebaseAuth auth;

    DatabaseReference reference;
    FirebaseUser fuser;
    private Context mContext;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    Intent intent;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_siswa);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        profile_image_siswa = findViewById(R.id.profile_image_siswa);
        add_profile_image = findViewById(R.id.add_profile_image);
        nama_siswa = findViewById(R.id.nama_siswa);
        email_ortu = findViewById(R.id.email_ortu);
        jenkel_siswa = findViewById(R.id.jenkel_siswa);
        usia_siswa = findViewById(R.id.usia_siswa);
        status_siswa = findViewById(R.id.status_siswa);

        kembali = findViewById(R.id.kembali);
        hapus = findViewById(R.id.hapus);

        teks_laporan2 = findViewById(R.id.teks_laporan2);
        waktu_laporan2 = findViewById(R.id.waktu_laporan2);
        btn_send = findViewById(R.id.btn_send);

        recycler_view_lap = findViewById(R.id.recycler_view_lap);
        recycler_view_lap.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycler_view_lap.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        storageReference = FirebaseStorage.getInstance().getReference("Foto Siswa");

        auth = FirebaseAuth.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Siswa").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Siswa siswa = dataSnapshot.getValue(Siswa.class);

                assert siswa != null;

                nama_siswa.setText(siswa.getNamaSiswa());
                email_ortu.setText(siswa.getEmailOrtu());
                usia_siswa.setText(siswa.getUsiaSiswa());
                jenkel_siswa.setText(siswa.getJenkelSiswa());
                status_siswa.setText(siswa.getStatusSiswa());

                if (siswa.getStatusSiswa().equals("Sudah Konfirmasi")) {

                    status_siswa.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    add_profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openImage();
                        }
                    });


                    btn_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat dateformat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                            String mydate = dateformat.format(c.getTime());

                            waktu_laporan2.setText(mydate);

                            String lap = teks_laporan2.getText().toString();
                            String waktu = waktu_laporan2.getText().toString();

                            if (!lap.equals("")) {
                                sendLaporan(lap, waktu);
                            } else {
                                Toast.makeText(DataSiswaActivity.this, "Kolom tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                            }
                            teks_laporan2.setText("");
                        }
                    });
                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(DataSiswaActivity.this);
                    builder.setTitle("Status Belum Konfirmasi!");
                    builder.setMessage("Silahkan klik ya untuk mengirim email konfirmasi.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(DataSiswaActivity.this, InformedConsentActivity.class);
                            intent.putExtra("userid", userid);
                            DataSiswaActivity.this.startActivity(intent);                        }
                    });
                    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();

                    status_siswa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(DataSiswaActivity.this, InformedConsentActivity.class);
                            intent.putExtra("userid", userid);
                            DataSiswaActivity.this.startActivity(intent);
                        }
                    });

                    status_siswa.setTextColor(getResources().getColor(R.color.colorAccent));
                    status_siswa.setUnderlineColor(getResources().getColor(R.color.colorAccent));

                    add_profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(DataSiswaActivity.this, "Anda belum konfirmasi!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    teks_laporan2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(DataSiswaActivity.this, "Anda belum konfirmasi!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    teks_laporan2.setFocusable(false);
                }

                if (siswa.getImageURLSiswa().equals("default")) {
                    profile_image_siswa.setImageResource(R.drawable.foto_profil);
                } else {
                    Glide.with(getApplicationContext()).load(siswa.getImageURLSiswa()).into(profile_image_siswa);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DataSiswaActivity.this, MainActivity.class));
            }
        });

        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusdata();
            }
        });

        mLaporan = new ArrayList<>();
        readLaporan();

    }

    private void hapusdata() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DataSiswaActivity.this);
        builder.setTitle("Hapus Data Siswa");
        builder.setMessage("Anda yakin ingin menghapus data?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity(new Intent(DataSiswaActivity.this, MainActivity.class));

                final ProgressDialog pd = new ProgressDialog(DataSiswaActivity.this);
                pd.setMessage("Sedang Hapus");
                pd.show();

                auth = FirebaseAuth.getInstance();
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Siswa").child(userid);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("statusSiswa", "Dihapus");

                reference.updateChildren(hashMap);

//                reference.removeValue();
                Toast.makeText(getApplicationContext(), "Berhasil Dihapus", Toast.LENGTH_LONG).show();


            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void sendLaporan(String teksLaporan, String waktuLap) {
        DatabaseReference laporanPush = reference.push();
        String idLap = laporanPush.getKey();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid())
                .child("Siswa").child(userid)
                .child("Laporan");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("idLap", idLap);
        hashMap.put("teksLaporan", teksLaporan);
        hashMap.put("waktuLap", waktuLap);

        reference.child(idLap).updateChildren(hashMap);
    }

    private void readLaporan() {

        mLaporan = new ArrayList<>();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                .child("Siswa").child(userid)
                .child("Laporan");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLaporan.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Laporan laporan = snapshot.getValue(Laporan.class);

                    laporan.setIdLap(snapshot.getKey());
                    mLaporan.add(laporan);
                }
                laporanAdapter = new LaporanAdapter(DataSiswaActivity.this, mLaporan, "Teks Laporan");
                recycler_view_lap.setAdapter(laporanAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull final Task<Uri> task) {
                    if (task.isSuccessful()) {

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Uri downloadUri = task.getResult();
                                String mUri = downloadUri.toString();

                                Siswa siswa = dataSnapshot.getValue(Siswa.class);

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid())
                                        .child("Siswa").child(userid);
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("imageURLsiswa", "" + mUri);

                                siswa.setImageURLSiswa(mUri);

                                reference.child("imageURLsiswa").setValue("" + mUri);

                                pd.dismiss();
//                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
