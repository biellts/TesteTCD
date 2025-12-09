package br.com.sigapar1.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DiaUtil {

    /**
     * Converte "MONDAY" para a próxima segunda (incluindo hoje se ainda não passou).
     */
    public static LocalDate proximaData(String diaSemana) {
        DayOfWeek target = DayOfWeek.valueOf(diaSemana.toUpperCase());
        LocalDate hoje = LocalDate.now();
        DayOfWeek hojeDW = hoje.getDayOfWeek();

        int diff = target.getValue() - hojeDW.getValue();
        if (diff < 0) diff += 7; // próxima semana

        return hoje.plusDays(diff);
    }
}
