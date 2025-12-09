package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.dao.UsuarioDAO;
import br.com.sigapar1.entity.*;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Stateless
public class WalkinService {

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private AgendamentoDAO agendamentoDAO;

    public Agendamento registrarWalkin(String nome, String cpf, String telefone, Servico servico) {

        // 1️⃣ Busca usuário por CPF
        Usuario usuario = usuarioDAO.buscarPorCpf(cpf);

        // 2️⃣ Se não existir → cria um novo temporário
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setNome(nome.trim());
            usuario.setCpf(cpf);
            usuario.setTelefone(telefone);
            usuario.setRole(Role.ROLE_USER);
            usuarioDAO.salvar(usuario);
        }

        // 3️⃣ Cria o agendamento Walk-in
        Agendamento ag = new Agendamento();
        ag.setUsuario(usuario);
        ag.setServico(servico);
        ag.setData(LocalDate.now());
        ag.setDataHora(LocalDateTime.now());
        ag.setHoraCheckin(LocalTime.now());
        ag.setStatus(StatusAgendamento.EM_FILA);
    
       agendamentoDAO.salvar(ag);

        return ag;
    }
}
