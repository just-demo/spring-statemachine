package demo.statemachine.listener;

import org.springframework.messaging.Message;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import demo.statemachine.entity.OrderEvent;
import demo.statemachine.entity.OrderState;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class OrderListener extends StateMachineListenerAdapter<OrderState, OrderEvent> {

  @Override
  public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
    log.info("State changed: {} -> {}", from.getId(), to.getId());
  }

  @Override
  public void eventNotAccepted(Message<OrderEvent> event) {
    log.info("Event not accepted: {}", event.getPayload());
  }
}
