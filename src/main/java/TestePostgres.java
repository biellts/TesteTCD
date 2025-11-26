import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestePostgres {
    public static void main(String[] args) {
        // TEM QUE SER "sigaparLocalPU" (com L maiúsculo no final)
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sigaparLocalPU");
        
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Usuario u = new Usuario();
        u.setNome("Gabriel Siqueira");
        u.setCpf("12345678900");
        u.setEmail("gabriel@email.com");
        u.setSenha("1234");  // depois vamos criptografar, mas agora só para testar
        u.setAtivo(true);
        u.setRole(Role.ROLE_USER);

        em.persist(u);
        em.getTransaction().commit();  // aqui que tem que dar o INSERT no console

        System.out.println("Usuário salvo! ID gerado = " + u.getId());

        em.close();
        emf.close();
    }
}