package br.com.sigapar1.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import br.com.sigapar1.entity.Usuario;

@WebFilter("*.xhtml")
public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String url = req.getRequestURI(); // ex: /app/admin/dashboard.xhtml

        // 1. Recursos JSF/PrimeFaces → sempre liberar (CSS, JS, imagens, etc.)
        if (url.contains("faces.resource") || url.contains("javax.faces.resource")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Páginas públicas → liberar para todos (logado ou não)
        if (url.contains("/publico/")
                || url.endsWith("/index.xhtml")
                || url.endsWith("/logout.xhtml")
                || url.endsWith("/login.xhtml")
                || url.contains("login_")) {  // login_admin, login_atendente, etc.
            chain.doFilter(request, response);
            return;
        }

        // 3. Usuário não logado → redireciona para escolha de acesso
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            res.sendRedirect(req.getContextPath() + "/publico/escolher-acesso.xhtml");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + "/publico/escolher-acesso.xhtml");
            return;
        }

        String role = usuario.getRole().name();

        // 4. Liberação por role
        if ("ROLE_ADMIN".equals(role)) {
            // Admin acessa tudo (admin/*, login_admin, etc.)
            chain.doFilter(request, response);
            return;
        }

        // ROLE_USER - só suas telas
        if ("ROLE_USER".equals(role)) {
            if (url.contains("/usuarios/") ||
                url.contains("/agendamento/")) {
                chain.doFilter(request, response);
                return;
            }
        }

        // ROLE_ATTENDANT - só suas telas
        if ("ROLE_ATTENDANT".equals(role)) {
            if (url.contains("/atendente/")) {
                chain.doFilter(request, response);
                return;
            }
        }

        // ROLE_RECEPTIONIST - só suas telas
        if ("ROLE_RECEPTIONIST".equals(role)) {
            if (url.contains("/recepcao/")) {
                chain.doFilter(request, response);
                return;
            }
        }

        // Se chegou aqui → o usuário NÃO tem permissão para a página atual
        // (ex.: atendente tentando acessar /admin/dashboard.xhtml, ou user tentando /atendente/home)
        res.sendRedirect(req.getContextPath() + "/erro/403.xhtml");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}