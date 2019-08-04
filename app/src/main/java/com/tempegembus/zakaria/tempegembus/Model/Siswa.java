package com.tempegembus.zakaria.tempegembus.Model;

public class Siswa {

    private String idSiswa;
    private String namaSiswa;
    private String emailOrtu;
    private String usiaSiswa;
    private String jenkelSiswa;
    private String idPsikolog;
    private String imageURLsiswa;
    private String statusSiswa;

    public Siswa(String idSiswa, String namaSiswa, String emailOrtu, String usiaSiswa, String jenkelSiswa,
                 String idPsikolog, String imageURLsiswa, String statusSiswa) {
        this.idSiswa = idSiswa;
        this.namaSiswa = namaSiswa;
        this.emailOrtu = emailOrtu;
        this.usiaSiswa = usiaSiswa;
        this.jenkelSiswa = jenkelSiswa;
        this.idPsikolog = idPsikolog;
        this.imageURLsiswa = imageURLsiswa;
        this.statusSiswa = statusSiswa;
    }

    public Siswa() {
    }

    public String getIdSiswa() {
        return idSiswa;
    }

    public void setIdSiswa(String idSiswa) {
        this.idSiswa = idSiswa;
    }

    public String getNamaSiswa() {
        return namaSiswa;
    }

    public void setNamaSiswa(String namaSiswa) {
        this.namaSiswa = namaSiswa;
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

    public String getIdPsikolog() {
        return idPsikolog;
    }

    public String getImageURLSiswa() {
        return imageURLsiswa;
    }

    public void setImageURLSiswa(String imageURLsiswa) {
        this.imageURLsiswa = imageURLsiswa;
    }

    public String getStatusSiswa() {
        return statusSiswa;
    }

    public void setStatusSiswa(String statusSiswa) {
        this.statusSiswa = statusSiswa;
    }

}
