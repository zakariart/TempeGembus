package com.tempegembus.zakaria.tempegembus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.tempegembus.zakaria.tempegembus.Model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePsikologActivity extends AppCompatActivity {

    ImageView kembali;
    CircleImageView profile_image_psi;
    Button btn_mulai;
    TextView nama_psikolog;
    MaterialEditText jenkel_psi, sipp, lokasi_psi;

    Intent intent;
    FirebaseUser fuser;
    DatabaseReference reference;
    String userid;

    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_psikolog);

        profile_image_psi = findViewById(R.id.profile_image_psi);
        nama_psikolog = findViewById(R.id.nama_psikolog);
        jenkel_psi = findViewById(R.id.jenkel_psi);
        sipp = findViewById(R.id.sipp);
        lokasi_psi = findViewById(R.id.lokasi_psi);
        kembali = findViewById(R.id.kembali);
        btn_mulai = findViewById(R.id.btn_mulai);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilePsikologActivity.this, MainActivity.class).setFlags(Intent.	FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
        });

        btn_mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startActivity = new Intent(ProfilePsikologActivity.this, MessageActivity.class);
                startActivity.putExtra("userid", userid);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                nama_psikolog.setText(user.getNama());
                jenkel_psi.setText(user.getJenkel());
                sipp.setText(user.getNip());
                lokasi_psi.setText(user.getLokasi());

                if (user.getImageURL().equals("default")) {
                    profile_image_psi.setImageResource(R.drawable.foto_profil);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image_psi);
                }

//                readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }
}
