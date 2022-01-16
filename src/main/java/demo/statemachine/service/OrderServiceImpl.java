package demo.statemachine.service;

import static demo.statemachine.entity.OrderEvent.FULFILL;
import static demo.statemachine.entity.OrderEvent.PAY;
import static demo.statemachine.entity.OrderState.SUBMITTED;
import static java.util.Optional.ofNullable;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import demo.statemachine.entity.Order;
import demo.statemachine.entity.OrderEvent;
import demo.statemachine.entity.OrderState;
import demo.statemachine.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private static final String ORDER_ID_HEADER = "orderId";

  private final OrderRepository orderRepository;
  private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

  @Override
  public Order create() {
    return orderRepository.save(new Order(SUBMITTED));
  }

  @Override
  public void pay(Long orderId) {
    sendEvent(orderId, PAY);
  }

  @Override
  public void fulfill(Long orderId) {
    sendEvent(orderId, FULFILL);
  }

  private void sendEvent(Long orderId, OrderEvent orderEvent) {
    StateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory.getStateMachine(orderId.toString());
    stateMachine.stop();
    stateMachine.getStateMachineAccessor().doWithAllRegions(stateMachineAccess -> {
      stateMachineAccess.addStateMachineInterceptor(new StateMachineInterceptorAdapter<>() {
        @Override
        public void preStateChange(State<OrderState, OrderEvent> state, Message<OrderEvent> message,
            Transition<OrderState, OrderEvent> transition, StateMachine<OrderState, OrderEvent> stateMachine,
            StateMachine<OrderState, OrderEvent> rootStateMachine) {
          ofNullable(message)
              .map(Message::getHeaders)
              .map(headers -> headers.get(ORDER_ID_HEADER))
              .map(Long.class::cast)
              .flatMap(orderRepository::findById)
              .ifPresent(order -> {
                order.setState(state.getId());
                orderRepository.save(order);
              });
        }
      });
      stateMachineAccess.resetStateMachine(new DefaultStateMachineContext<>(
          orderRepository.findById(orderId).get().getState(), null, null, null));
    });
    stateMachine.start();
    stateMachine.sendEvent(withPayload(orderEvent)
        .setHeader(ORDER_ID_HEADER, orderId)
        .build());
  }
}
