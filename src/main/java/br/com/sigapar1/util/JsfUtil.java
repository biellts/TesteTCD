package br.com.sigapar1.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.IOException;

public class JsfUtil {

    public static void addInfoMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null)
        );
    }

    public static void addErrorMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null)
        );
    }

    public static void addWarnMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null)
        );
    }

    // -----------------------------
    // Alias
    // -----------------------------
    public static void addInfo(String msg) { addInfoMessage(msg); }
    public static void addError(String msg) { addErrorMessage(msg); }
    public static void addWarn(String msg) { addWarnMessage(msg); }
    public static void addSuccess(String msg) { addInfoMessage(msg); }

    // *** MÉTODO QUE ESTÁ FALTANDO NO SEU PROJETO ***
    public static void addSuccessMessage(String msg) {
        addInfoMessage(msg);
    }
    // ------------------------------------------------

    // -----------------------------
    // Session helpers
    // -----------------------------
    public static Object getSession(String key) {
        return FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get(key);
    }

    public static void setSession(String key, Object value) {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .put(key, value);
    }

    public static void invalidateSession() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();
    }

    // -----------------------------
    // Redirect simples
    // -----------------------------
    public static void redirect(String url) {
        try {
            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .redirect(url);
        } catch (IOException e) {
            addError("Falha ao redirecionar: " + url);
        }
    }
}
