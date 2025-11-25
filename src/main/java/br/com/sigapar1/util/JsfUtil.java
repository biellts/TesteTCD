package br.com.sigapar1.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class JsfUtil {

    public static void addInfoMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    public static void addErrorMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public static void addWarnMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null));
    }

    // Métodos simplificados
    public static void addInfo(String msg) {
        addInfoMessage(msg);
    }

    public static void addError(String msg) {
        addErrorMessage(msg);
    }

    public static void addWarn(String msg) {
        addWarnMessage(msg);
    }

    // Session helpers
    public static Object getSession(String key) {
        return FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get(key);
    }

    public static void setSession(String key, Object value) {
        FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .put(key, value);
    }

    public static void invalidateSession() {
        FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .invalidateSession();
    }
}
