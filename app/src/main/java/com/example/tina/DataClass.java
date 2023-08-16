package com.example.tina;

public class DataClass {

    private String nome;
    private String telefone;
    private String dataNascimento;
    private String cpf;
    private String email;

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

    public DataClass(String nome, String telefone, String dataNascimento, String cpf, String email) {
        this.nome = nome;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.email = email;
    }

    public DataClass() {

    }
}