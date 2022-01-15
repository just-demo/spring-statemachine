package demo.statemachine.service;

import demo.statemachine.entity.Order;

public interface OrderService {

  Order create();

  void pay(Long orderId);

  void fulfill(Long orderId);
}
