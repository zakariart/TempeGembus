package com.tempegembus.zakaria.tempegembus.Model;

public class Siswa {

    private String idSiswa;
    private String namaSiswa;
    private String emailOrtu;
    private String usiaSiswa;
    private String jenkelSiswa;
    private String nisn;
    private String imageURLsiswa;
//    private String status_siswa;

    public Siswa(String idSiswa, String namaSiswa, String emailOrtu, String usiaSiswa, String jenkelSiswa,
                 String nisn, String imageURLsiswa) {
        this.idSiswa = idSiswa;
        this.namaSiswa = namaSiswa;
        this.emailOrtu = emailOrtu;
        this.usiaSiswa = usiaSiswa;
        this.jenkelSiswa = jenkelSiswa;
        this.nisn = nisn;
        this.imageURLsiswa = imageURLsiswa;
//        this.status_siswa = status_siswa;
    }

    public String getIdSiswa() {
        return idSiswa;
    }

    public Siswa() {
    }

    public void setIdSiswa(String idSiswa) {
        this.idSiswa = idSiswa;
    }

    public String getNamaSiswa() {
        return namaSiswa;
    }

    public String getEmailOrtu() {
        return emailOrtu;
    }

    public String getUsiaSiswa() {
        return usiaSiswa;
    }

    public String getJenkelSiswa() {
        return jenkelSiswa;
    }

    public String getNisn() {
        return nisn;
    }

    public void setNamaSiswa(String namaSiswa) {
        this.namaSiswa = namaSiswa;
    }

    public String getImageURLSiswa() {
        return imageURLsiswa;
    }

    public void setImageURLSiswa(String imageURLsiswa) {
        this.imageURLsiswa = imageURLsiswa;
    }

//    public String getStatusSiswa() {
//        return status_siswa;
//    }
//
//    public void setStatusSiswa(String status_siswa) {
//        this.status_siswa = status_siswa;
//    }

}
