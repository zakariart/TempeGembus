package com.tempegembus.zakaria.tempegembus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tempegembus.zakaria.tempegembus.DataSiswaActivity;
import com.tempegembus.zakaria.tempegembus.Model.Siswa;
import com.tempegembus.zakaria.tempegembus.R;

import java.util.List;

public class SiswaAdapter extends RecyclerView.Adapter<SiswaAdapter.ViewHolder> {

    private Context mContext;
    private List<Siswa> mSiswas;
    private String namaSiswa;

    public SiswaAdapter(Context context, List<Siswa> siswas, String namasiswa) {
        mContext = context;
        mSiswas = siswas;
        namaSiswa = namasiswa;

    }

    @NonNull
    @Override
    public SiswaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.siswa_item, parent, false);
        return new SiswaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Siswa siswa = mSiswas.get(position);
        final String namaSiswa = mSiswas.get(position).getNamaSiswa();
        final String idSiswa = mSiswas.get(position).getIdSiswa();
        final String usiaSiswa = mSiswas.get(position).getUsiaSiswa();

        holder.nama_siswa.setText(namaSiswa);
        holder.usia_siswa.setText(usiaSiswa);

        if (siswa.getImageURLSiswa().equals("default")) {
            holder.profile_image_siswa.setImageResource(R.drawable.foto_profil);
        } else {
            Glide.with(mContext).load(siswa.getImageURLSiswa()).into(holder.profile_image_siswa);
        }

        if (siswa.getStatusSiswa().equals("Sudah Konfirmasi")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DataSiswaActivity.class);
                intent.putExtra("userid", idSiswa);
                intent.putExtra("username", namaSiswa);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSiswas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView nama_siswa;
        public ImageView profile_image_siswa, img_on, img_off;
        private TextView usia_siswa;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view_siswa);
            nama_siswa = itemView.findViewById(R.id.nama_siswa);
            profile_image_siswa = itemView.findViewById(R.id.profile_image_siswa);
            usia_siswa = itemView.findViewById(R.id.usia_siswa);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}