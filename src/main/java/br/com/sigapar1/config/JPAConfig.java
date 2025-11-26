package br.com.sigapar1.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JPAConfig {

    @Produces
    @PersistenceContext(unitName = "sigaparPU")
    private EntityManager em;
}
