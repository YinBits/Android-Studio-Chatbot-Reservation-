package com.example.tina;
public class Reserva {
    private String nomeCliente;
    private String dataReserva;
    private String horarioReserva;
    private String numeroMesa;
    private String numeroPessoas;

    public Reserva() {
        // Construtor vazio necessário para Firebase
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(String dataReserva) {
        this.dataReserva = dataReserva;
    }

    public String getHorarioReserva() {
        return horarioReserva;
    }

    public void setHorarioReserva(String horarioReserva) {
        this.horarioReserva = horarioReserva;
    }

    public String getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(String numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public String getNumeroPessoas() {
        return numeroPessoas;
    }

    public void setNumeroPessoas(String numeroPessoas) {
        this.numeroPessoas = numeroPessoas;
    }
}
