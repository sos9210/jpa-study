package jpabook.jpashop;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    //스프링부트가 자동으로 엔티티매니저팩토리, 엔티티매니저를 주입해준다.
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class,id);
    }

}
