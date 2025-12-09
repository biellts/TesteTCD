package br.com.sigapar1.dao;

import br.com.sigapar1.entity.TokenResetSenha;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

@Stateless
public class TokenResetSenhaDAO extends GenericDAO<TokenResetSenha> {

    public TokenResetSenhaDAO() {
        super(TokenResetSenha.class);
    }

    public TokenResetSenha buscarPorToken(String token) {
        try {
            return getEntityManager()
                    .createQuery("SELECT t FROM TokenResetSenha t WHERE t.token = :tk",
                            TokenResetSenha.class)
                    .setParameter("tk", token)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }
}
