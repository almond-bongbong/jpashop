package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDTO;
import jpabook.jpashop.repository.order.query.OrderQueryDTO;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.repository.order.query.OrderQueryWithCountDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

	private final OrderRepository orderRepository;
	private final OrderQueryRepository orderQueryRepository;

	@GetMapping("/api/v1/orders")
	public List<Order> ordersV1() {
		List<Order> all = orderRepository.findAll(new OrderSearch());

		for (Order order : all) {
			order.getMember().getName();
			order.getDelivery().getAddress();
			List<OrderItem> orderItems = order.getOrderItems();
			orderItems.stream().forEach(o -> o.getItem().getName());
		}

		return all;
	}

	@GetMapping("/api/v2/orders")
	public List<OrderDTO> ordersV2() {
		return orderRepository.findAll(new OrderSearch()).stream()
				.map(OrderDTO::new)
				.collect(Collectors.toList());
	}

	@GetMapping("/api/v3/orders")
	public List<OrderDTO> ordersV3() {
		return orderRepository.findAllWithItem().stream()
				.map(OrderDTO::new)
				.collect(Collectors.toList());
	}

	@GetMapping("/api/v3.1/orders")
	public List<OrderDTO> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
										@RequestParam(value = "limit", defaultValue = "100") int limit) {
		return orderRepository.findAllWithMemberDelivery(offset, limit).stream()
				.map(OrderDTO::new)
				.collect(Collectors.toList());
	}

	@GetMapping("/api/v4/orders")
	public List<OrderQueryDTO> ordersV4() {
		return orderQueryRepository.findOrderQueryDTOs();
	}

	@GetMapping("/api/v5/orders")
	public List<OrderQueryDTO> ordersV5() {
		return orderQueryRepository.findAllByDTO_optimization();
	}

	@GetMapping("/api/v6/orders")
	public List<OrderFlatDTO> ordersV6() {
		return orderQueryRepository.findAllByDTO_flat();
	}

	@GetMapping("/api/v7/orders")
	public List<OrderQueryWithCountDTO> ordersV7() {
		return orderQueryRepository.findAllByDTO_withCount();
	}

	@Getter
	static class OrderDTO {

		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		private List<OrderItemDTO> orderItems;

		public OrderDTO(Order order) {
			orderId = order.getId();
			name = order.getMember().getName();
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress();
			orderItems = order.getOrderItems().stream()
					.map(OrderItemDTO::new)
					.collect(Collectors.toList());
		}
	}

	@Getter
	static class OrderItemDTO {

		private String itemName;
		private int orderPrice;
		private int count;

		public OrderItemDTO(OrderItem orderItem) {
			itemName = orderItem.getItem().getName();
			orderPrice = orderItem.getTotalPrice();
			count = orderItem.getCount();
		}
	}
}
