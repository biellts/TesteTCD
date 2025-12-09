package br.com.sigapar1.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.time.LocalDate;

@FacesConverter(value = "localDateConverter", managed = true)
public class LocalDateConverter implements Converter<LocalDate> {

    @Override
    public LocalDate getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println(">>> LocalDateConverter.getAsObject() chamado com value: " + value);
        
        if (value == null || value.isEmpty()) {
            System.out.println(">>> Value nulo ou vazio");
            return null;
        }
        
        try {
            LocalDate result = LocalDate.parse(value);
            System.out.println(">>> Convertido para LocalDate: " + result);
            return result;
        } catch (Exception e) {
            System.out.println(">>> ERRO ao converter para LocalDate: " + value);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDate value) {
        if (value == null) {
            return "";
        }
        String result = value.toString();
        System.out.println(">>> LocalDateConverter.getAsString() -> " + result);
        return result;
    }
}