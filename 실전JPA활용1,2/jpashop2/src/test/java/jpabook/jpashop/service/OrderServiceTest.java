package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;
    @Test
    void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Item book = createBook("ASDFVBBBB", 10000, 20);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        //상품 주문시 상태는 ORDER
        Assertions.assertEquals(OrderStatus.ORDER,getOrder.getStatus());

        //주문한 상품 종류 수가 정확해야 한다.
        Assertions.assertEquals(1,getOrder.getOrderItems().size());

        //주문 가격은 가격 * 수량이다.
        Assertions.assertEquals(10000 * orderCount,getOrder.getTotalPrice());

        //주문 수량만큼 재고가 줄어야 한다.
        Assertions.assertEquals(18,book.getStockQuantity());
    }

    @Test
    void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("ASDFVBBBB", 10000, 20);

        int orderCount = 11;

        //when
        orderService.order(member.getId(),item.getId(),orderCount);

        //then
        Assertions.assertThrows(NotEnoughStockException.class, () ->
                orderService.order(member.getId(),item.getId(),orderCount));
    }

    @Test
    void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("AAA!!!", 10000, 12);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        //주문 취소시 상태는 CANCEL 이다.
        Assertions.assertEquals(OrderStatus.CANCEL, getOrder.getStatus());

        //주문이 취소된 상품은 그만큼 재고가 증가해야 한다.
        Assertions.assertEquals(12,item.getStockQuantity());
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("서울","어딘가","123-123"));
        em.persist(member);
        return member;
    }
}
