package demo.statemachine;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import demo.statemachine.entity.Order;
import demo.statemachine.service.OrderService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderRunner implements ApplicationRunner {

  private final OrderService orderService;

  @Override
  public void run(ApplicationArguments args) {
    Order order = orderService.create();
    orderService.pay(order.getId());
    orderService.fulfill(order.getId());
  }
}
