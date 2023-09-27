package com.example.tina;

public class DataClass {

    private String nome, telefone, dataNascimento, cpf, email, imageURL;

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public String getCPF() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public DataClass(String nome, String telefone, String dataNascimento, String cpf, String email, String imageURL) {
        this.nome = nome;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.email = email;
        this.imageURL = imageURL;
    }

    public DataClass() {

    }
}