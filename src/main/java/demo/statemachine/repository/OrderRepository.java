package demo.statemachine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.statemachine.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
