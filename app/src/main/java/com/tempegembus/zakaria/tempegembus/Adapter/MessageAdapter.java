package com.tempegembus.zakaria.tempegembus.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
//    private String msgimage;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
//        this.msgimage = msgimage;
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
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        final Chat chat = mChat.get(position);
        String message_type = chat.getType();

//        holder.show_message.setText(chat.getMessage());

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.drawable.foto_profil);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Dilihat");
            } else {
                holder.txt_seen.setText("Terkirim");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

        if (message_type.equals("text")) {
            holder.show_message.setText(chat.getMessage());


        } else if (message_type.equals("image")) {
            holder.show_message.setVisibility(View.GONE);
            holder.layout_holder_gambar.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(chat.getMessage()).into(holder.message_image_layout);

            holder.message_image_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
//                    mBuilder.setMessage(chat.getMessage());
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
                    Glide.with(mContext).asBitmap().load(chat.getMessage()).placeholder(R.drawable.beranda_guru).into(photoView);
                    mBuilder.setView(photoView);

                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image, message_image_layout;
        public RelativeLayout layout_holder_gambar;
        public TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            message_image_layout = itemView.findViewById(R.id.message_image_layout);
            layout_holder_gambar = itemView.findViewById(R.id.layout_holder_gambar);
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