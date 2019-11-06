package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDTO;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

	private final OrderRepository orderRepository;
	private final OrderSimpleQueryRepository orderSimpleQueryRepository;

	@GetMapping("/api/v1/simple-orders")
	public List<Order> ordersV1() {
		return orderRepository.findAll(new OrderSearch());
	}

	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDTO> ordersV2() {
		return orderRepository.findAll(new OrderSearch()).stream()
				.map(SimpleOrderDTO::new)
				.collect(Collectors.toList());
	}

	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDTO> ordersV3() {
		return orderRepository.findAllWithMemberDelivery(new OrderSearch()).stream()
				.map(SimpleOrderDTO::new)
				.collect(Collectors.toList());
	}

	@GetMapping("/api/v4/simple-orders")
	public List<OrderSimpleQueryDTO> ordersV4() {
		return orderSimpleQueryRepository.findOrderDTOs();
	}

	@Data
	private static class SimpleOrderDTO {
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;

		SimpleOrderDTO(Order order) {
			orderId = order.getId();
			name = order.getMember().getName();
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress();
		}
	}
}
