package br.com.sigapar1.util;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    public static HttpSession getSession() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null) return null;
        return (HttpSession) ctx.getExternalContext().getSession(true);
    }

    public static void set(String key, Object value) {
        HttpSession session = getSession();
        if (session != null) session.setAttribute(key, value);
    }

    public static Object get(String key) {
        HttpSession session = getSession();
        return session != null ? session.getAttribute(key) : null;
    }

    public static void remove(String key) {
        HttpSession session = getSession();
        if (session != null) session.removeAttribute(key);
    }

    public static void invalidate() {
        HttpSession session = getSession();
        if (session != null) session.invalidate();
    }
}
