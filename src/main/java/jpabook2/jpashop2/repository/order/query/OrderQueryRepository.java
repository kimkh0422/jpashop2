package jpabook2.jpashop2.repository.order.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderSearch;
import jpabook2.jpashop2.domain.QMember;
import jpabook2.jpashop2.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;


    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {

        List<OrderQueryDto> result = findOrders();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderITemMap(toOrderIds(result));

        result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;

    }

    private List<Long> toOrderIds(List<OrderQueryDto> result){
        return result.stream()
                .map(o->o.getOrderId())
                .collect(Collectors.toList());
    }


    private Map<Long, List<OrderItemQueryDto>> findOrderITemMap(List<Long> orderIds){
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook2.jpashop2.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in  : orderIds",
                OrderItemQueryDto.class).setParameter("orderIds",orderIds).getResultList();

        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }




    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook2.jpashop2.repository.order.query.OrderQueryDto(o.id, m.name,o.orderDate, o.status, d.address)" +
                        " from Order o"+
                        " join o.member m"+
                        " join o.delivery d", OrderQueryDto.class).getResultList();

    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook2.jpashop2.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = : orderId",
                        OrderItemQueryDto.class).setParameter("orderId", orderId)
                .getResultList();

    }

    private List<Order> findAll(OrderSearch orderSearch){

        QOrder order = QOrder.order;
        QMember member = QMember.member;

        JPAQueryFactory query = new JPAQueryFactory(em);

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .limit(1000)
                .fetch();
    }


}
