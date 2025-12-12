package br.com.sigapar1.config;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.Role;
import br.com.sigapar1.service.UsuarioService;
import br.com.sigapar1.util.HashUtil;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Startup
public class StartupAdmin {

    @Inject
    private UsuarioService usuarioService;

    @PostConstruct
    public void init() {
        try {
            // Permite sobrescrever via variáveis de ambiente:
            // SIGAPAR_ADMIN_EMAIL, SIGAPAR_ADMIN_PASS, SIGAPAR_RESET_ADMIN_PASS
            String adminEmail = System.getenv("SIGAPAR_ADMIN_EMAIL");
            if (adminEmail == null || adminEmail.isBlank()) {
                adminEmail = "admin@sigapar.com";
            }

            String adminPass = System.getenv("SIGAPAR_ADMIN_PASS");
            if (adminPass == null || adminPass.isBlank()) {
                adminPass = "1234";
            }

            boolean resetPass = "true".equalsIgnoreCase(System.getenv("SIGAPAR_RESET_ADMIN_PASS"));

            List<Usuario> admins = usuarioService.listarPorRole(Role.ROLE_ADMIN);

            if (admins == null || admins.isEmpty()) {
                // Criar admin com credenciais padrão (será hasheada em UsuarioService.salvar)
                Usuario admin = new Usuario();
                admin.setNome("Administrador do Sistema");
                admin.setEmail(adminEmail);
                admin.setCpf("00000000000");
                admin.setTelefone("0000000000");
                admin.setSenha(adminPass);
                admin.setRole(Role.ROLE_ADMIN);
                admin.setAtivo(true);
                admin.setEmailConfirmed(true);

                usuarioService.salvar(admin);

                System.out.println("ADMIN criado com sucesso! email=" + adminEmail + " senha=" + adminPass);
            } else {
                String emails = admins.stream().map(Usuario::getEmail).collect(Collectors.joining(","));
                System.out.println("ADMIN já existe: " + emails);
                if (resetPass) {
                    Usuario existente = admins.get(0);
                    existente.setSenha(adminPass);
                    usuarioService.atualizar(existente);
                    System.out.println("Senha do admin resetada para: " + adminPass + " (email=" + existente.getEmail() + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
