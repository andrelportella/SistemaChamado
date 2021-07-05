package Conexao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ChamadoPU");

    public static EntityManager getEntityManager() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("ChamadoPU");
        return factory.createEntityManager();

    }

    public static EntityManager getEntityManagerAniversario() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("ListaAniversario");
        return factory.createEntityManager();
    }
//
//    @Produces
//    @RequestScoped
//    public EntityManager criarEntityManager() {
//        return emf.createEntityManager();
//    }
//
//    public void fecharEntityManager(@Disposes EntityManager em) {
//        if (em != null && em.isOpen()) {
//            em.close();
//        }
//    }
}
