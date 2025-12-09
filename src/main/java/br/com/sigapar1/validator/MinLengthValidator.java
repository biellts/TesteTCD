
package br.com.sigapar1.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("minLengthValidator")
public class MinLengthValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        if (value == null) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Campo obrigatório.",
                            "Você deve preencher este campo."));
        }

        String texto = value.toString();

        // Obtém o tamanho mínimo informado pelo atributo
        Object minAttr = component.getAttributes().get("minLength");
        int min = (minAttr != null) ? Integer.parseInt(minAttr.toString()) : 20;

        if (texto.trim().length() < min) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Texto muito curto.",
                            "O texto deve ter no mínimo " + min + " caracteres."));
        }
    }
}
