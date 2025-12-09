package br.com.sigapar1.converter;

import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.service.HorarioService;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value = "horarioConverter", managed = true)
public class HorarioConverter implements Converter<Horario> {

    @Inject
    private HorarioService horarioService;

    @Override
    public Horario getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return horarioService.buscarPorId(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Horario horario) {
        if (horario == null || horario.getId() == null) {
            return "";
        }
        return String.valueOf(horario.getId());
    }
}
