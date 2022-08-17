package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        //EntityManagerFactory는 데이터베이스당 하나만 생성해서 애플리케이션 전체에서 공유한다
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        //EntityManager는 쓰레드간 공유해선 안된다.
        EntityManager em = emf.createEntityManager();
/*

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Parent parent = new Parent();
            Child child1 = new Child();
            Child child2 = new Child();
            parent.addChild(child1);
            parent.addChild(child2);
            em.persist(parent);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());

            em.remove(findParent);

            tx.commit();
        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
 */
    }
}
