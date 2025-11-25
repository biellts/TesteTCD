import br.com.sigapar1.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestePostgres {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sigaparPU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Usuario u = new Usuario();
        u.setNome("Gabriel Siqueira");
        u.setCpf("12345678900");
        u.setEmail("gabriel@email.com");

        em.persist(u);

        em.getTransaction().commit();

        System.out.println("Usuário salvo com ID: " + u.getId());

        em.close();
        emf.close();
    }
}
