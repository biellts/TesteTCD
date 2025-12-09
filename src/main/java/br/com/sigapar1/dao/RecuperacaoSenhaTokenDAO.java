package br.com.sigapar1.dao;

import br.com.sigapar1.entity.RecuperacaoSenhaToken;
import br.com.sigapar1.entity.Usuario;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

@Stateless
public class RecuperacaoSenhaTokenDAO extends GenericDAO<RecuperacaoSenhaToken> {

    public RecuperacaoSenhaTokenDAO() {
        super(RecuperacaoSenhaToken.class);
    }

    public RecuperacaoSenhaToken buscarPorUsuario(Usuario u) {
        try {
            return getEntityManager()
                    .createQuery("SELECT t FROM RecuperacaoSenhaToken t WHERE t.usuario = :u",
                            RecuperacaoSenhaToken.class)
                    .setParameter("u", u)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public RecuperacaoSenhaToken buscarPorToken(String token) {
        try {
            return getEntityManager()
                    .createQuery("SELECT t FROM RecuperacaoSenhaToken t WHERE t.token = :t",
                            RecuperacaoSenhaToken.class)
                    .setParameter("t", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /** 
     * 🔥 IMPORTANTE: Remoção DEFINITIVA com merge interno.
     * Sem isso o Hibernate NÃO remove o registro antigo!
     */
    public void excluir(RecuperacaoSenhaToken token) {
        if (token == null) return;
        RecuperacaoSenhaToken managed = getEntityManager().merge(token);
        getEntityManager().remove(managed);
        getEntityManager().flush();  // ✔ força sincronização imediata!
    }
}
