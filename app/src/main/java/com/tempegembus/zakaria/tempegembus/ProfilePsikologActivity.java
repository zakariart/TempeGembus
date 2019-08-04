package com.tempegembus.zakaria.tempegembus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tempegembus.zakaria.tempegembus.Adapter.SiswaAdapter;
import com.tempegembus.zakaria.tempegembus.Model.Siswa;
import com.tempegembus.zakaria.tempegembus.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePsikologActivity extends AppCompatActivity {

    ImageView kembali;
    CircleImageView profile_image_psi;
    Button btn_mulai;
    TextView nama_psikolog;
    MaterialEditText jenkel_psi, sipp, lokasi_psi;
    RecyclerView recycler_view_pilih;
    Spinner spinner_pilih;
    ImageButton btn_pilih;

    private SiswaAdapter siswaAdapter;
    private List<Siswa> mSiswas;

    Intent intent;
    FirebaseUser fuser;
    DatabaseReference reference;
    String userid;

    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_psikolog);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        profile_image_psi = findViewById(R.id.profile_image_psi);
        nama_psikolog = findViewById(R.id.nama_psikolog);
        jenkel_psi = findViewById(R.id.jenkel_psi);
        sipp = findViewById(R.id.sipp);
        lokasi_psi = findViewById(R.id.lokasi_psi);
        kembali = findViewById(R.id.kembali);
        btn_mulai = findViewById(R.id.btn_mulai);
        btn_pilih = findViewById(R.id.btn_pilih);
        spinner_pilih = findViewById(R.id.spinner_pilih);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                nama_psikolog.setText(user.getNama());
                jenkel_psi.setText(user.getJenkel());
                sipp.setText(user.getNip());
                lokasi_psi.setText(user.getLokasi());

                if (user.getJenisAkun().equals("pengguna")) {
                    sipp.setFloatingLabelText("NIP / NIK");
                    btn_mulai.setVisibility(View.GONE);
                    btn_pilih.setVisibility(View.GONE);
                    spinner_pilih.setVisibility(View.GONE);
                    readSiswas2();
                } else {
                    readSiswas();
                }

                if (user.getImageURL().equals("default")) {
                    profile_image_psi.setImageResource(R.drawable.foto_profil);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image_psi);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_pilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_pilih = spinner_pilih.getSelectedItem().toString();
                if (txt_pilih.equals("")) {
                    Toast.makeText(ProfilePsikologActivity.this, "Data Kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    pilihSiswa();
                    Toast.makeText(ProfilePsikologActivity.this, txt_pilih + " ditambahkan", Toast.LENGTH_SHORT).show();
                }

            }
        });

        recycler_view_pilih = findViewById(R.id.recycler_view_pilih);
        recycler_view_pilih.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycler_view_pilih.setLayoutManager(linearLayoutManager);

//        readSiswas();

//        mSiswas = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                User user = dataSnapshot.getValue(User.class);
////                readSiswas();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilePsikologActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
        });

        btn_mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSiswas.isEmpty()) {
                    Toast.makeText(ProfilePsikologActivity.this, "Data Kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ProfilePsikologActivity.this, MessageActivity.class);
                    intent.putExtra("userid", userid);
                    ProfilePsikologActivity.this.startActivity(intent);
                }
            }
        });
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Siswa");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final List<String> listsiswa = new ArrayList<>();
//
//                for (DataSnapshot namaSnapshot : dataSnapshot.getChildren()) {
//                    Siswa siswa = namaSnapshot.getValue(Siswa.class);
//                    siswa.setIdSiswa(namaSnapshot.getKey());
//
//                    if (siswa.getStatusSiswa().equals("Sudah Konfirmasi")) {
//                        String namaSiswa = namaSnapshot.child("namaSiswa").getValue(String.class);
//                        listsiswa.add(namaSiswa);
//                    }
//                }
//
//                ArrayAdapter<String> siswaAdapter = new ArrayAdapter<>(ProfilePsikologActivity.this,
//                        android.R.layout.simple_spinner_item, listsiswa);
//                siswaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner_pilih.setAdapter(siswaAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void readSiswas() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Siswa");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSiswas = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Siswa siswa = snapshot.getValue(Siswa.class);

                    siswa.setIdSiswa(snapshot.getKey());

                    if (siswa.getIdPsikolog().equals(userid)) {
                        mSiswas.add(siswa);
                    }
                }

                siswaAdapter = new SiswaAdapter(ProfilePsikologActivity.this, mSiswas, "Nama Siswa");
                recycler_view_pilih.setAdapter(siswaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> listsiswa = new ArrayList<>();

                for (DataSnapshot namaSnapshot : dataSnapshot.getChildren()) {
                    Siswa siswa = namaSnapshot.getValue(Siswa.class);
                    siswa.setIdSiswa(namaSnapshot.getKey());

                    if (siswa.getStatusSiswa().equals("Sudah Konfirmasi") && siswa.getIdPsikolog().equals("default")) {
                        String namaSiswa = namaSnapshot.child("namaSiswa").getValue(String.class);
                        listsiswa.add(namaSiswa);
                    } else {
                        String namaSiswa = "";
                        listsiswa.add(namaSiswa);
                    }
                }

                ArrayAdapter<String> siswaAdapter = new ArrayAdapter<>(ProfilePsikologActivity.this,
                        android.R.layout.simple_spinner_item, listsiswa);
                siswaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_pilih.setAdapter(siswaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSiswas2() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Siswa");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSiswas = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Siswa siswa = snapshot.getValue(Siswa.class);

                    siswa.setIdSiswa(snapshot.getKey());

                    if (siswa.getIdPsikolog().equals(firebaseUser.getUid())) {
                        mSiswas.add(siswa);
                    }
                }

                siswaAdapter = new SiswaAdapter(ProfilePsikologActivity.this, mSiswas, "Nama Siswa");
                recycler_view_pilih.setAdapter(siswaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pilihSiswa() {
        final String txt_pilih = spinner_pilih.getSelectedItem().toString();

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Siswa");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot namaSnapshot : dataSnapshot.getChildren()) {
                    Siswa siswa = namaSnapshot.getValue(Siswa.class);

//                    reference.orderByChild("namaSiswa").equalTo(txt_pilih);
                    siswa.setIdSiswa(namaSnapshot.getKey());
//                        reference.getKey();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("idPsikolog", userid);

                    reference.child(txt_pilih).updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
//        final Query query = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid())
//                .child("Siswa").orderByChild("namaSiswa").equalTo(txt_pilih);
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Siswa siswa = snapshot.getValue(Siswa.class);
//
//                    assert siswa != null;
//                    assert fuser != null;
//
//                    siswa.setIdSiswa(snapshot.getKey());
//                    HashMap<String, Object> map = new HashMap<>();
//                    map.put("usiaSiswa", userid);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
    }
}
