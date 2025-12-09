package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.entity.Feriado;
import br.com.sigapar1.entity.IntervaloBloqueado;
import br.com.sigapar1.service.HorarioService;
import br.com.sigapar1.service.FeriadoService;
import br.com.sigapar1.service.IntervaloBloqueadoService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Named("horarioController")
@ViewScoped
public class HorarioController implements Serializable {

    private static final long serialVersionUID = 1L;

    // ----------- INJEÇÕES ----------------
    @Inject
    private HorarioService horarioService;

    @Inject
    private FeriadoService feriadoService;

    @Inject
    private IntervaloBloqueadoService intervaloService;

    // ----------- LISTAS ----------------
    private List<Horario> listaHorarios;
    private List<Feriado> feriados;

    // ----------- CAMPOS HORÁRIOS ----------------
    private String diaSemana;
    private String inicioTimeStr;
    private String fimTimeStr;
    private int intervaloMinutos;
    private int capacidadeMax;

    // ----------- CAMPOS FERIADOS ----------------
    private LocalDate novaDataFeriado;

    // ----------- CAMPOS INTERVALO ----------------
    private String intervaloDia;
    private String intervaloInicioStr;
    private String intervaloFimStr;

    // ----------- FORMATADORES ----------------
    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ----------- INIT ----------------
    @PostConstruct
    public void init() {
        listarHorarios();
        listarFeriados();
    }

    // ----------- HORÁRIOS ----------------
    public void listarHorarios() {
        listaHorarios = horarioService.listarTodos();
    }

    public void gerarHorarios() {

        if (diaSemana == null || inicioTimeStr == null || fimTimeStr == null
                || intervaloMinutos <= 0 || capacidadeMax <= 0) {
            return;
        }

        LocalTime inicio = LocalTime.parse(inicioTimeStr, horaFormatter);
        LocalTime fim = LocalTime.parse(fimTimeStr, horaFormatter);

        horarioService.gerarHorarios(diaSemana, inicio, fim, intervaloMinutos, capacidadeMax);
        listarHorarios();
    }

    public void alterarStatus(Long id) {
        horarioService.alterarStatus(id);
        listarHorarios();
    }

    public void removerHorario(Long id) {
        horarioService.remover(id);
        listarHorarios();
    }

    // ----------- FERIADOS ----------------
    public void listarFeriados() {
        feriados = feriadoService.listarTodos();
    }

    public void adicionarFeriado() {
        if (novaDataFeriado != null) {
            feriadoService.adicionar(novaDataFeriado);
            novaDataFeriado = null;
            listarFeriados();
        }
    }

    public void removerFeriado(Long id) {
        feriadoService.remover(id);
        listarFeriados();
    }

    // ----------- FORMATADORES ----------------
    public String getDiaSemanaFormatado(String diaSemana) {
        if (diaSemana == null) {
            return "";
        }

        return switch (diaSemana) {
            case "MONDAY" ->
                "Segunda-feira";
            case "TUESDAY" ->
                "Terça-feira";
            case "WEDNESDAY" ->
                "Quarta-feira";
            case "THURSDAY" ->
                "Quinta-feira";
            case "FRIDAY" ->
                "Sexta-feira";
            case "SATURDAY" ->
                "Sábado";
            case "SUNDAY" ->
                "Domingo";
            default ->
                diaSemana;
        };
    }

    public String formatarHora(Horario h) {
        return h.getHora().format(horaFormatter);
    }

    public String formatarData(Feriado f) {
        return f.getData().format(dataFormatter);
    }

    // ----------- INTERVALOS ----------------
    public void adicionarIntervalo() {
        try {
            LocalTime i = LocalTime.parse(intervaloInicioStr);
            LocalTime f = LocalTime.parse(intervaloFimStr);

            intervaloService.adicionar(intervaloDia, i, f);
            carregarListas();

            JsfUtil.addSuccessMessage("Intervalo adicionado!");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e.getMessage());
        }
    }

    public void removerIntervalo(Long id) {
        intervaloService.remover(id);
        carregarListas();
    }

    public void carregarListas() {
        listarHorarios();
        listarFeriados();
    }

    // ----------- GETTERS / SETTERS ----------------
    public List<Horario> getListaHorarios() {
        return listaHorarios;
    }

    public List<Feriado> getFeriados() {
        return feriados;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getInicioTimeStr() {
        return inicioTimeStr;
    }

    public void setInicioTimeStr(String inicioTimeStr) {
        this.inicioTimeStr = inicioTimeStr;
    }

    public String getFimTimeStr() {
        return fimTimeStr;
    }

    public void setFimTimeStr(String fimTimeStr) {
        this.fimTimeStr = fimTimeStr;
    }

    public int getIntervaloMinutos() {
        return intervaloMinutos;
    }

    public void setIntervaloMinutos(int intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }

    public int getCapacidadeMax() {
        return capacidadeMax;
    }

    public void setCapacidadeMax(int capacidadeMax) {
        this.capacidadeMax = capacidadeMax;
    }

    public LocalDate getNovaDataFeriado() {
        return novaDataFeriado;
    }

    public void setNovaDataFeriado(LocalDate novaDataFeriado) {
        this.novaDataFeriado = novaDataFeriado;
    }

    public String getIntervaloDia() {
        return intervaloDia;
    }

    public void setIntervaloDia(String intervaloDia) {
        this.intervaloDia = intervaloDia;
    }

    public String getIntervaloInicioStr() {
        return intervaloInicioStr;
    }

    public void setIntervaloInicioStr(String intervaloInicioStr) {
        this.intervaloInicioStr = intervaloInicioStr;
    }

    public String getIntervaloFimStr() {
        return intervaloFimStr;
    }

    public void setIntervaloFimStr(String intervaloFimStr) {
        this.intervaloFimStr = intervaloFimStr;
    }

    public List<IntervaloBloqueado> getIntervalos() {
        return intervaloService.listarPorDia(intervaloDia);
    }

}
