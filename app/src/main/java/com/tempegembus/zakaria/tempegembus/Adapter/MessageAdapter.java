package com.tempegembus.zakaria.tempegembus.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tempegembus.zakaria.tempegembus.Model.Chat;
import com.tempegembus.zakaria.tempegembus.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    private MediaController ctlr;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {

        final Chat chat = mChat.get(position);
        final String time = mChat.get(position).getTime();
        String message_type = chat.getType();

//        holder.show_message.setText(chat.getMessage());

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.drawable.foto_profil);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_time.setText(time);
                holder.txt_seen.setText("Dilihat");
            } else {
                holder.txt_time.setText(time);
                holder.txt_seen.setText("Terkirim");
            }
        } else {
            holder.txt_time.setText(time);
            holder.txt_seen.setVisibility(View.GONE);
        }

        if (message_type.equals("text")) {
            holder.show_message.setText(chat.getMessage());
        } else if (message_type.equals("image")) {
            if (chat.getMessage().contains(".mp4")) {
                holder.show_message.setVisibility(View.GONE);
                holder.layout_holder_gambar.setVisibility(View.VISIBLE);
                holder.message_image_layout2.setVisibility(View.INVISIBLE);
                holder.message_image_layout.setVisibility(View.VISIBLE);
                holder.message_image_layout.setVideoURI(Uri.parse(chat.getMessage()));
//                holder.message_image_layout.requestFocus();
                holder.message_image_layout.start();
                holder.message_image_layout.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                    }
                });

                holder.message_image_layout.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        holder.message_image_layout.start();
                        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                MediaController mediaController = new MediaController(mContext);
                                holder.message_image_layout.setMediaController(mediaController);
                                mediaController.setAnchorView(holder.message_image_layout);
                            }
                        });
                    }
                });

                holder.message_image_layout.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        return false;
                    }
                });
            } else {
                holder.show_message.setVisibility(View.GONE);
                holder.layout_holder_gambar.setVisibility(View.VISIBLE);
                holder.message_image_layout.setVisibility(View.INVISIBLE);

                Glide.with(mContext).load(chat.getMessage()).into(holder.message_image_layout2);

                holder.message_image_layout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());

                        mBuilder.setTitle("Pesan Gambar");
                        mBuilder.setIcon(R.drawable.logo);

                        mBuilder.setNeutralButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                    }
                                });
//                    LayoutInflater inflater = LayoutInflater.from(mContext);
//                    View mView = inflater.inflate(R.layout.dialog_custom_layout, null);
//                    PhotoView photoView = mView.findViewById(R.id.photoview);
//
//                    View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
//                    PhotoView photoView = mView.findViewById(R.id.photoview);
//                    mBuilder.setView(mView);

                        ImageView photoView = new ImageView(mContext);

                        Glide.with(mContext).load(chat.getMessage()).placeholder(R.drawable.beranda_guru).into(photoView);
                        mBuilder.setView(photoView);

                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();
                    }
                });
            }
        }
//		else if (message_type.equals("video")) {
//            holder.show_message.setVisibility(View.GONE);
//            holder.layout_holder_gambar.setVisibility(View.VISIBLE);
//            Glide.with(mContext).load(chat.getMessage()).into(holder.message_image_layout);
//
//            holder.message_image_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
//
//                    mBuilder.setTitle("Pesan Video");
//                    mBuilder.setIcon(R.drawable.logo);
//
//                    mBuilder.setNeutralButton("OK",
//                            new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface arg0,
//                                                    int arg1) {
//
//                                }
//                            });
//
//                    ImageView photoView = new ImageView(mContext);
//
//                    Glide.with(mContext).load(chat.getMessage()).placeholder(R.drawable.beranda_guru).into(photoView);
//                    mBuilder.setView(photoView);
//
//                    AlertDialog mDialog = mBuilder.create();
//                    mDialog.show();
//                }
//            });
//
//        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public VideoView message_image_layout;
        public ImageView profile_image, message_image_layout2;
        public RelativeLayout layout_holder_gambar;
        public TextView txt_seen, txt_time;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            message_image_layout2 = itemView.findViewById(R.id.message_image_layout2);
            message_image_layout = itemView.findViewById(R.id.message_image_layout);
            layout_holder_gambar = itemView.findViewById(R.id.layout_holder_gambar);
            txt_time = itemView.findViewById(R.id.txt_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}