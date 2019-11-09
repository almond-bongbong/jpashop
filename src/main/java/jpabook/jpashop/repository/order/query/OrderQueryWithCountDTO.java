package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode
public class OrderQueryWithCountDTO {

	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private Address address;
	private int orderItemsCount;

	public OrderQueryWithCountDTO(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, int orderItemsCount) {
		this.orderId = orderId;
		this.name = name;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.address = address;
		this.orderItemsCount = orderItemsCount;
	}
}
