package com.tempegembus.zakaria.tempegembus.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.tempegembus.zakaria.tempegembus.Model.Laporan;
import com.tempegembus.zakaria.tempegembus.R;

import java.util.List;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {

    private Context mContext;
    private List<Laporan> mLaporan;
    private String teksLaporan;

    FirebaseUser fuser;

    public LaporanAdapter(Context context, List<Laporan> laporans, String teksLaporan) {
        this.mLaporan = laporans;
        this.mContext = context;
        this.teksLaporan = teksLaporan;
    }

    @NonNull
    @Override
    public LaporanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.laporan_item, parent, false);
        return new LaporanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaporanAdapter.ViewHolder holder, int position) {

        final Laporan laporan = mLaporan.get(position);
        final String idLap = mLaporan.get(position).getIdLap();
        final String teksLaporan = mLaporan.get(position).getTeksLaporan();
        final String waktuLap = mLaporan.get(position).getWaktuLap();

        holder.teks_laporan.setText(teksLaporan);
        holder.waktu_laporan.setText(waktuLap);
    }

    @Override
    public int getItemCount() {
        return mLaporan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView teks_laporan, waktu_laporan;

        public ViewHolder(View itemView) {
            super(itemView);
            teks_laporan = itemView.findViewById(R.id.teks_laporan);
            waktu_laporan = itemView.findViewById(R.id.waktu_laporan);
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        fuser = FirebaseAuth.getInstance().getCurrentUser();
//        if (mChat.get(position).getSender().equals(fuser.getUid())) {
//            return MSG_TYPE_RIGHT;
//        } else {
//            return MSG_TYPE_LEFT;
//        }
//    }
}
