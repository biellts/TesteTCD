package br.com.sigapar1.config;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.Role;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {
        "/admin/*",
        "/agendamento/*",
        "/recepcao/*",
        "/atendimento/*"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");

        String path = request.getRequestURI();
        String ctx  = request.getContextPath();

        // Sem login → volta
        if (usuario == null) {
            response.sendRedirect(ctx + "/login.xhtml");
            return;
        }

        // ÁREA ADMIN → apenas admin
        if (path.startsWith(ctx + "/admin") &&
            usuario.getRole() != Role.ROLE_ADMIN) {

            response.sendRedirect(ctx + "/erro403.xhtml");
            return;
        }

        chain.doFilter(req, res);
    }
}
