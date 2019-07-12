package com.tempegembus.zakaria.tempegembus.Fragments;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tempegembus.zakaria.tempegembus.Adapter.SiswaAdapter;
import com.tempegembus.zakaria.tempegembus.Model.Siswa;
import com.tempegembus.zakaria.tempegembus.R;
import com.tempegembus.zakaria.tempegembus.TambahDataSiswa;

import java.util.ArrayList;
import java.util.List;

public class BerandaFragment extends Fragment {

    private RecyclerView recyclerView;

    private SiswaAdapter siswaAdapter;
    private List<Siswa> mSiswas;
    FirebaseUser fuser;
    DatabaseReference reference;

    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSiswas = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        readSiswas();

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TambahDataSiswa.class);
                getActivity().startActivity(intent);
            }
        });

        return view;

    }

    private void readSiswas() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Siswa");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSiswas = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Siswa siswa = snapshot.getValue(Siswa.class);

                    siswa.setIdSiswa(snapshot.getKey());
                    mSiswas.add(siswa);

                }

                siswaAdapter = new SiswaAdapter(getContext(), mSiswas, "Nama Siswa");
                recyclerView.setAdapter(siswaAdapter);
                /* } */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
