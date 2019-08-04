package com.tempegembus.zakaria.tempegembus.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tempegembus.zakaria.tempegembus.Adapter.SiswaAdapter;
import com.tempegembus.zakaria.tempegembus.Adapter.UserAdapter;
import com.tempegembus.zakaria.tempegembus.Model.Siswa;
import com.tempegembus.zakaria.tempegembus.Model.User;
import com.tempegembus.zakaria.tempegembus.R;
import com.tempegembus.zakaria.tempegembus.TambahDataSiswa;

import java.util.ArrayList;
import java.util.List;

public class BerandaFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView daftar_judul;

    private SiswaAdapter siswaAdapter;
    private List<Siswa> mSiswas;

    FirebaseUser fuser;
    DatabaseReference reference;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        daftar_judul = view.findViewById(R.id.daftar_judul);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSiswas = new ArrayList<>();
        mUsers = new ArrayList<>();

        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TambahDataSiswa.class);
                getActivity().startActivity(intent);
            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getJenisAkun().equals("psikolog")) {
                    daftar_judul.setText("Daftar Guru / Orang Tua");
                    fab.clearAnimation();
                    fab.setVisibility(View.GONE);
                    readUsers();
                } else {
                    readSiswas();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

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

                    if (!siswa.getStatusSiswa().equals("Dihapus")) {
                        mSiswas.add(siswa);
                    }
                }

                siswaAdapter = new SiswaAdapter(getContext(), mSiswas, "Nama Siswa");
                recyclerView.setAdapter(siswaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void readSiswas() {
//
//        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
//        Query query = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid())
//                .child("Siswa").orderByChild("statusSiswa").equalTo("Belum Konfirmasi");
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mSiswas = new ArrayList<>();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Siswa siswa = snapshot.getValue(Siswa.class);
//
//                    assert siswa != null;
//                    assert fuser != null;
//
//                    siswa.setIdSiswa(snapshot.getKey());
//                    mSiswas.add(siswa);
//
//                }
//
//                siswaAdapter = new SiswaAdapter(getContext(), mSiswas, "Nama Siswa");
//                recyclerView.setAdapter(siswaAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void readUsers() {


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (!user.getJenisAkun().equals("psikolog")) {
                        mUsers.add(user);
                    }

                }

                userAdapter = new UserAdapter(getContext(), mUsers, false, "Nama User");
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
