package com.tempegembus.zakaria.tempegembus.Model;

public class Laporan {

    private String teksLaporan;
    private String idLap;
    private String waktuLap;

    public Laporan (String teksLaporan, String idLap, String waktuLap){
        this.teksLaporan = teksLaporan;
        this.idLap = idLap;
        this.waktuLap = waktuLap;
    }

    public Laporan(){
    }

    public String getTeksLaporan() {
        return teksLaporan;
    }

    public void setTeksLaporan(String teksLaporan) {
        this.teksLaporan = teksLaporan;
    }

    public String getIdLap() {
        return idLap;
    }

    public void setIdLap(String idLap) { this.idLap = idLap;
    }

    public String getWaktuLap() { return waktuLap; }

    public void setWaktuLap(String waktuLap) {this.waktuLap = waktuLap; }
}
