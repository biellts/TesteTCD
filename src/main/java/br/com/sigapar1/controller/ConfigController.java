package br.com.sigapar1.controller;


import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class ConfigController implements Serializable {

    private String abaAtiva = "horario";

    public void setAba(String aba) {
        this.abaAtiva = aba;
    }

    public String getAbaAtiva() {
        return abaAtiva;
    }
}
