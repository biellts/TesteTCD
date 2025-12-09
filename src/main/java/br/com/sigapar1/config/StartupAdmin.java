package br.com.sigapar1.config;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.Role;
import br.com.sigapar1.service.UsuarioService;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class StartupAdmin {

    @Inject
    private UsuarioService usuarioService;

    @PostConstruct
    public void init() {
        try {
            // Se já existir admin, não cria outro
            Usuario adminExistente = usuarioService.buscarPorEmail("admin@sigapar.com");
            if (adminExistente != null) {
                System.out.println("ADMIN já existe! Ignorando criação automática.");
                return;
            }

            // Criar admin
            Usuario admin = new Usuario();
            admin.setNome("Administrador do Sistema");
            admin.setEmail("admin@gmail.com");
            admin.setCpf("00000000000");
            admin.setTelefone("0000000000");
            admin.setSenha("1234"); // SERÁ HASHED automaticamente
            admin.setRole(Role.ROLE_ADMIN);
            admin.setAtivo(true);

            // IMPORTANTE → Admin já entra confirmado
            admin.setEmailConfirmed(true);

            usuarioService.salvar(admin);

            System.out.println("ADMIN criado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
