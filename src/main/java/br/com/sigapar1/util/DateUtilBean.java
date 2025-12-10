package br.com.sigapar1.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Named("dateUtil")
@ApplicationScoped
public class DateUtilBean {

    public String format(LocalDate date) {
        return DateUtil.format(date);
    }

    public String format(LocalDateTime dateTime) {
        return DateUtil.format(dateTime);
    }
}
