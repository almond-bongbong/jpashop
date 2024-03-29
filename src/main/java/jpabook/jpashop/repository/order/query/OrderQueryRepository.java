package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

	private final EntityManager em;

	public List<OrderQueryDTO> findOrderQueryDTOs() {
		List<OrderQueryDTO> result = findOrders();
		result.forEach(o -> {
			List<OrderItemQueryDTO> orderItems = findOrderItems(o.getOrderId());
			o.setOrderItems(orderItems);
		});

		return result;
	}

	public List<OrderQueryDTO> findAllByDTO_optimization() {
		List<OrderQueryDTO> orders = findOrders();
		Map<Long, List<OrderItemQueryDTO>> orderItemMap = findOrderItemMap(toOrderIds(orders));
		orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

		return orders;
	}

	private List<Long> toOrderIds(List<OrderQueryDTO> orders) {
		return orders.stream()
					.map(OrderQueryDTO::getOrderId)
					.collect(Collectors.toList());
	}

	private Map<Long, List<OrderItemQueryDTO>> findOrderItemMap(List<Long> orderIds) {
		List<OrderItemQueryDTO> orderItems = em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDTO(oi.order.id, oi.item.name, oi.orderPrice, oi.count) from OrderItem oi" +
					" join oi.item i" +
					" where oi.order.id in :orderIds", OrderItemQueryDTO.class)
				.setParameter("orderIds", orderIds)
				.getResultList();

		return orderItems.stream()
				.collect(Collectors.groupingBy(OrderItemQueryDTO::getOrderId));
	}

	private List<OrderItemQueryDTO> findOrderItems(Long orderId) {
		return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDTO(oi.order.id, oi.item.name, oi.orderPrice, oi.count) from OrderItem oi" +
					" join oi.item i" +
					" where oi.order.id = :orderId", OrderItemQueryDTO.class)
				.setParameter("orderId", orderId)
				.getResultList();
	}

	private List<OrderQueryDTO> findOrders() {
		return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDTO(o.id, m.name, o.orderDate, o.status, o.delivery.address) from Order o" +
					" join o.member m" +
					" join o.delivery d", OrderQueryDTO.class)
				.getResultList();
	}

	public List<OrderFlatDTO> findAllByDTO_flat() {
		return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderFlatDTO(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) from Order o" +
					" join o.member m" +
					" join o.delivery d" +
					" join o.orderItems oi" +
					" join oi.item i", OrderFlatDTO.class)
				.getResultList();
	}

	public List<OrderQueryWithCountDTO> findAllByDTO_withCount() {
		return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryWithCountDTO(o.id, m.name, o.orderDate, o.status, d.address, o.orderItems.size) from Order o" +
					" join o.member m" +
					" join o.delivery d", OrderQueryWithCountDTO.class)
				.getResultList();
	}
}
