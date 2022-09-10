package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Rollback(false)
@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");

        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertEquals(findMember.getId(),savedMember.getId());
        assertEquals(findMember.getUsername(),savedMember.getUsername());
        assertEquals(findMember,savedMember);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertEquals(findMember1,member1);
        assertEquals(findMember2,member2);

        //전체조회검증
        List<Member> all = memberRepository.findAll();
        assertEquals(all.size(),2);

        //카운트 검증증
        long count = memberRepository.count();
        assertEquals(count,2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertEquals(deletedCount,0);
    }
    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertEquals(result.get(0).getUsername(),"AAA");
        assertEquals(result.get(0).getAge(),20);
        assertEquals(result.size(),1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertEquals(findMember,m1);
    }
    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",10);
        Member findMember = result.get(0);
        assertEquals(findMember,m1);
    }
    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findListByUsername = memberRepository.findListByUsername("AAA");
        Optional<Member> findOptionalByUsername = memberRepository.findOptionalByUsername("AAA");
        Member findMemberByUsername = memberRepository.findMemberByUsername("AAA");
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Slice<Member> slice = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content1 = page.getContent();
        List<Member> content2 = slice.getContent();

        //엔티티 -> DTO
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        /*page*/
        assertEquals(content1.size(),3);             //한페이지당 출력되는개수
        assertEquals(page.getTotalElements(),5);    //요소(글)의 전체개수
        assertEquals(page.getNumber(),0);           //현재페이지넘버
        assertEquals(page.getTotalPages(),2);       //전체페이지 수
        assertEquals(page.isFirst(),true);          //현재페이지가 첫번째인지
        assertEquals(page.hasNext(),true);          //다음페이지 존재여부

        /*slice*/
        assertEquals(content2.size(),3);             //한페이지당 출력되는개수
        assertEquals(slice.getNumber(),0);          //현재페이지넘버
        assertEquals(slice.isFirst(),true);         //현재페이지가 첫번째인지
        assertEquals(slice.hasNext(),true);         //다음페이지 존재여부
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        //        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);

        System.out.println("member5 = " + member5);

        //then
        assertEquals(resultCount,3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();
        //List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        //List<Member> members = memberRepository.findMemberEntityGraph();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("TeamClass = " + member.getTeam().getClass());
            System.out.println("memberTeam = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");
        em.flush();
    }

    @Test
    public void lock(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername(member1.getUsername());
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }
}