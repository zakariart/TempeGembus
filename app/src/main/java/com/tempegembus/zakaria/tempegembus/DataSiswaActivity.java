package com.tempegembus.zakaria.tempegembus;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
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
import com.tempegembus.zakaria.tempegembus.Model.Siswa;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DataSiswaActivity extends AppCompatActivity {

    CircleImageView profile_image_siswa, add_profile_image;
    MaterialEditText nama_siswa, email_ortu, usia_siswa, jenkel_siswa, nisn;

    DatabaseReference reference;
    FirebaseUser fuser;
    private Context mContext;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    ImageView kembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_siswa);

        profile_image_siswa = findViewById(R.id.profile_image_siswa);
        add_profile_image = findViewById(R.id.add_profile_image);
        nama_siswa = findViewById(R.id.nama_siswa);
        email_ortu = findViewById(R.id.email_ortu);
        jenkel_siswa = findViewById(R.id.jenkel_siswa);
        usia_siswa = findViewById(R.id.usia_siswa);
        nisn = findViewById(R.id.nisn);
        kembali = findViewById(R.id.kembali);

        storageReference = FirebaseStorage.getInstance().getReference("Foto Siswa");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Siswa");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Siswa siswa = snapshot.getValue(Siswa.class);
                    siswa.setIdSiswa(snapshot.getKey());

                    nama_siswa.setText(siswa.getNamaSiswa());
                    email_ortu.setText(siswa.getEmailOrtu());
                    usia_siswa.setText(siswa.getUsiaSiswa());
                    jenkel_siswa.setText(siswa.getJenkelSiswa());
                    nisn.setText(siswa.getNisn());

                    if (siswa.getImageURLSiswa().equals("default")) {
                        profile_image_siswa.setImageResource(R.drawable.foto_profil);
                    } else {
                        Glide.with(getApplicationContext()).load(siswa.getImageURLSiswa()).into(profile_image_siswa);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DataSiswaActivity.this, MainActivity.class));
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


                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Siswa siswa = snapshot.getValue(Siswa.class);

                                    String key = snapshot.getKey();

                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Siswa");
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("imageURLsiswa", "" + mUri);

                                    siswa.setImageURLSiswa(mUri);

                                    reference.child(key).child("imageURLsiswa").setValue("" + mUri);

                                    pd.dismiss();
                                }
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
