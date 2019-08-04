package com.tempegembus.zakaria.tempegembus;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.tempegembus.zakaria.tempegembus.Adapter.MessageAdapter;
import com.tempegembus.zakaria.tempegembus.Fragments.APIService;
import com.tempegembus.zakaria.tempegembus.Model.Chat;
import com.tempegembus.zakaria.tempegembus.Model.User;
import com.tempegembus.zakaria.tempegembus.Notifications.Client;
import com.tempegembus.zakaria.tempegembus.Notifications.Data;
import com.tempegembus.zakaria.tempegembus.Notifications.MyResponse;
import com.tempegembus.zakaria.tempegembus.Notifications.Sender;
import com.tempegembus.zakaria.tempegembus.Notifications.Token;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView nama;

    //tambahan
    Bitmap photo;
    String mCurrentPhotoPath;

    FirebaseUser fuser;
    DatabaseReference reference;
    List<String> listSpinner = new ArrayList<>();
    Spinner spinner;

    ImageButton btn_send, chat_add_btn, btn_attach;
    EditText text_send;
    TextView text_time;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Uri videoUri;
    Intent intent;

    ValueEventListener seenListener;

    String userid;

    APIService apiService;

    boolean notify = false;

    private static final int GALLERY_PICK = 1;
    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int VIDEO_REQUEST_CODE = 2;
    private Uri imageUri;
    private StorageTask uploadTask;

    // Storage Firebase
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        spinner = findViewById(R.id.spnTakeCamera);
        listSpinner.add("");
        listSpinner.add("Photo");
        listSpinner.add("Video");


        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listSpinner);
        spinner.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        nama = findViewById(R.id.nama);
        btn_send = findViewById(R.id.btn_send);
        chat_add_btn = findViewById(R.id.chat_add_btn);
        text_send = findViewById(R.id.text_send);
        btn_attach = findViewById(R.id.btn_attach);
        text_time = findViewById(R.id.text_time);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, ProfilePsikologActivity.class);
                intent.putExtra("userid", userid);
                MessageActivity.this.startActivity(intent);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm");
                String mydate = dateformat.format(c.getTime());

                text_time.setText(mydate);

                String time = text_time.getText().toString();

                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg, time);
                } else {
                    Toast.makeText(MessageActivity.this, "Pesan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        //------- IMAGE STORAGE ---------
//        mImageStorage = FirebaseStorage.getInstance().getReference("message_images");

        btn_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                Intent galleryIntent = new Intent();
                galleryIntent.setType("*/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "PILIH GAMBAR ATAU VIDEO"), GALLERY_PICK);

            }
        });

        chat_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
//
//                }
//                dispatchTakePictureIntent();
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                spinner.performClick();


//                Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
//                startActivityForResult(intent, CAMERA_REQUEST_CODE);

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItemPosition() == 1) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

                } else if (spinner.getSelectedItemPosition() == 2) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(intent, VIDEO_REQUEST_CODE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                nama.setText(user.getNama());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.drawable.foto_profil);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message, String time) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("type", "text");
        hashMap.put("isseen", false);
        hashMap.put("time", time);

        reference.child("Chats").push().setValue(hashMap);


        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());


        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getNama(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading");
            pd.show();

            mImageStorage = FirebaseStorage.getInstance().getReference("message_images");

            if (imageUri != null) {
                final StorageReference fileReference = mImageStorage.child(System.currentTimeMillis()
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
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {

                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm");
                            String mydate = dateformat.format(c.getTime());

                            text_time.setText(mydate);

                            String time = text_time.getText().toString();

                            Uri downloadUri = task.getResult();
                            String mUri = downloadUri.toString();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", fuser.getUid());
                            hashMap.put("receiver", userid);
                            hashMap.put("message", "" + mUri);
                            hashMap.put("type", "image");
                            hashMap.put("isseen", false);
                            hashMap.put("time", time);

                            reference.child("Chats").push().setValue(hashMap);

                            pd.dismiss();

                            // add user to chat fragment
                            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(fuser.getUid())
                                    .child(userid);

                            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        chatRef.child("id").setValue(userid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(userid)
                                    .child(fuser.getUid());
                            chatRefReceiver.child("id").setValue(fuser.getUid());

                            final String msg = "*Pesan Media*";

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (notify) {
                                        sendNotifiaction(userid, user.getNama(), msg);
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();

                    }
                });
            } else {
                Toast.makeText(this, "Tidak ada gambar terrpilih", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK
        ) {
            photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            submit(photo);

        } else if (requestCode == VIDEO_REQUEST_CODE & resultCode == RESULT_OK) {
            videoUri = data.getData();

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading");
            pd.show();

            mImageStorage = FirebaseStorage.getInstance().getReference("message_video");

            if (videoUri != null) {
                final StorageReference fileReference = mImageStorage.child(System.currentTimeMillis()
                        + "." + "mp4");

                uploadTask = fileReference.putFile(videoUri);

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
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm");
                            String mydate = dateformat.format(c.getTime());

                            text_time.setText(mydate);

                            String time = text_time.getText().toString();

                            Uri downloadUri = task.getResult();
                            String mUri = downloadUri.toString();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", fuser.getUid());
                            hashMap.put("receiver", userid);
                            hashMap.put("message", "" + mUri);
                            hashMap.put("type", "image");
                            hashMap.put("isseen", false);
                            hashMap.put("time", time);

                            reference.child("Chats").push().setValue(hashMap);

                            pd.dismiss();

                            // add user to chat fragment
                            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(fuser.getUid())
                                    .child(userid);

                            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        chatRef.child("id").setValue(userid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(userid)
                                    .child(fuser.getUid());
                            chatRefReceiver.child("id").setValue(fuser.getUid());

                            final String msg = "*Pesan Gambar*";

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (notify) {
                                        sendNotifiaction(userid, user.getNama(), msg);
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            } else {
                Toast.makeText(this, "Tidak ada gambar terrpilih", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.logo, username + ": " + message, "Pesan Baru",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
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

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
        spinner.setSelection(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }


    public void submit(Bitmap photo) {

        final ProgressDialog pd = new ProgressDialog(MessageActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] b = stream.toByteArray();
        final String timeNow = String.valueOf(System.currentTimeMillis()) + ".jpg";
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("message_foto").child(
                timeNow);

        UploadTask uploadTask = storageReference.putBytes(b);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm");
                    String mydate = dateformat.format(c.getTime());

                    text_time.setText(mydate);

                    String time = text_time.getText().toString();

                    Uri downloadUri = task.getResult();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", fuser.getUid());
                    hashMap.put("receiver", userid);
                    hashMap.put("message", "" + downloadUri);
                    hashMap.put("type", "image");
                    hashMap.put("isseen", false);
                    hashMap.put("time", time);

                    reference.child("Chats").push().setValue(hashMap);

//                    pd.dismiss();

                    // add user to chat fragment
                    final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(fuser.getUid())
                            .child(userid);

                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                chatRef.child("id").setValue(userid);
                            }

                            pd.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(userid)
                            .child(fuser.getUid());
                    chatRefReceiver.child("id").setValue(fuser.getUid());

                    final String msg = "*Foto*";

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (notify) {
                                sendNotifiaction(userid, user.getNama(), msg);
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //                HashMap<String, Object> hashMap = new HashMap<>();
//                hashMap.put("sender", fuser.getUid());
//                hashMap.put("receiver", userid);
//                hashMap.put("message", "" + mUri);
//                Log.d("CEKURI" , "uri " + mUri);
//                hashMap.put("type", "image");
//                hashMap.put("isseen", false);
//
//                reference.child("Chats").push().setValue(hashMap);
//
//                // add user to chat fragment
//                final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                        .child(fuser.getUid())
//                        .child(userid);
//
//                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.exists()) {
//                            chatRef.child("id").setValue(userid);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//                final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
//                        .child(userid)
//                        .child(fuser.getUid());
//                chatRefReceiver.child("id").setValue(fuser.getUid());
//
//                final String msg = "*Foto*";
//
//                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                reference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//                        if (notify) {
//                            sendNotifiaction(userid, user.getNama(), msg);
//                        }
//                        notify = false;
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MessageActivity.this,"failed",Toast.LENGTH_LONG).show();
//
//
//            }
//        });

                } else {
                    pd.dismiss();
                }
            }
        });

        //StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
//        storageReference.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//
//                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
//                Toast.makeText(MessageActivity.this, "uploaded", Toast.LENGTH_SHORT).show();
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//
////                String mUri = downloadUri.toString();
//
//                HashMap<String, Object> hashMap = new HashMap<>();
//                hashMap.put("sender", fuser.getUid());
//                hashMap.put("receiver", userid);
//                hashMap.put("message", "" + mUri);
//                Log.d("CEKURI" , "uri " + mUri);
//                hashMap.put("type", "image");
//                hashMap.put("isseen", false);
//
//                reference.child("Chats").push().setValue(hashMap);
//
//                // add user to chat fragment
//                final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                        .child(fuser.getUid())
//                        .child(userid);
//
//                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.exists()) {
//                            chatRef.child("id").setValue(userid);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//                final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
//                        .child(userid)
//                        .child(fuser.getUid());
//                chatRefReceiver.child("id").setValue(fuser.getUid());
//
//                final String msg = "*Foto*";
//
//                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                reference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//                        if (notify) {
//                            sendNotifiaction(userid, user.getNama(), msg);
//                        }
//                        notify = false;
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MessageActivity.this,"failed",Toast.LENGTH_LONG).show();
//
//
//            }
//        });

    }


}
