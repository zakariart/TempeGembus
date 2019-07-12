package com.tempegembus.zakaria.tempegembus.Model;

public class User {

    private String id;
    private String nama;
    private String email;
    private String ttl;
    private String jenkel;
    private String nip;
    private String lokasi;
    private String password;
    private String imageURL;
    private String status;
    private String search;
    private String jenisAkun;

    public User(String id, String nama, String email, String ttl, String jenkel,
                String nip, String lokasi, String password, String imageURL, String status, String search, String jenisAkun) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.ttl = ttl;
        this.jenkel = jenkel;
        this.nip = nip;
        this.lokasi = lokasi;
        this.password = password;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.jenisAkun = jenisAkun;
    }

    public String getId() {
        return id;
    }

    public User() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTtl() {
        return ttl;
    }

    public String getJenkel() {
        return jenkel;
    }

    public String getNip() {
        return nip;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setUsername(String nama) {
        this.nama = nama;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getJenisAkun() {
        return jenisAkun;
    }

    public void setJenisAkun(String jenisAkun) {
        this.jenisAkun = jenisAkun;
    }
}