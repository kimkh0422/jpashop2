package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.repository.MemberRepositoryOld;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepositoryOld memberRepository;

    @Test
    public void 회원가입() throws Exception{
        //Given
        Member member = new Member();
        member.setName("깅깅빙");

        //when
        Long saveId = memberService.join(member);

        //Then
        assertEquals(member,memberRepository.findOne(saveId));
    }
    
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception{
        //Given
        Member member1 = new Member();
        member1.setName("깅깅빙");

        Member member2 = new Member();
        member2.setName("깅깅빙");

        //When
        memberService.join(member1);
        memberService.join(member2);

        //Then
        fail("예외가 발생해야 한다.");
    }
}