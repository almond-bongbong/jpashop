package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
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
	public void 상품주문() throws Exception {
		Member member = createMember("cmlee");
		Item book = createBook("시골 JPA", 10000, 10);

		int orderCount = 2;

		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

		Order order = orderRepository.findOne(orderId);

		assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, order.getStatus());
		assertEquals("수문한 상품 종류 수가 정확해야 한다.", 1, order.getOrderItems().size());
		assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount, order.getTotalPrice());
		assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());
	}

	@Test(expected = NotEnoughStockException.class)
	public void 상품주문_재고수량초과() throws Exception {
		Member member = createMember("cmlee");
		Item book = createBook("시골 JPA", 10000, 10);

		int orderCount = 11;

		orderService.order(member.getId(), book.getId(), orderCount);

		fail("재고 수량 부족 예외가 발생해야 한다.");
	}

	@Test
	public void 주문취소() throws Exception {
		Member member = createMember("cmlee2");
		Item book = createBook("Nextjs", 20000, 5);

		int orderCount = 2;

		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

		orderService.cancelOrder(orderId);

		Order getOrder = orderRepository.findOne(orderId);

		assertEquals("주문 취소시 상태는 CANCEL 이다.", OrderStatus.CANCEL, getOrder.getStatus());
		assertEquals("주문 취소된 상품은 그만큼 재고 갯수가 증가해야 한다.", 5, book.getStockQuantity());
	}

	private Member createMember(String name) {
		Member member = new Member();
		member.setName(name);
		member.setAddress(new Address("서울", "마곡중앙5로", "12345"));

		em.persist(member);
		return member;
	}

	private Item createBook(String name, int price, int stockQuantity) {
		Item book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);

		return book;
	}
 }