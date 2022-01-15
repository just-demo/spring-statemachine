package demo.statemachine.config;

import static demo.statemachine.entity.OrderEvent.CANCEL;
import static demo.statemachine.entity.OrderEvent.FULFILL;
import static demo.statemachine.entity.OrderEvent.PAY;
import static demo.statemachine.entity.OrderState.CANCELLED;
import static demo.statemachine.entity.OrderState.FULFILLED;
import static demo.statemachine.entity.OrderState.PAID;
import static demo.statemachine.entity.OrderState.SUBMITTED;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import demo.statemachine.entity.OrderEvent;
import demo.statemachine.entity.OrderState;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableStateMachineFactory
public class OrderConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

  @Override
  public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
    transitions
        .withExternal().source(SUBMITTED).target(PAID).event(PAY)
        .and()
        .withExternal().source(PAID).target(FULFILLED).event(FULFILL)
        .and()
        .withExternal().source(SUBMITTED).target(CANCELLED).event(CANCEL)
        .and()
        .withExternal().source(PAID).target(CANCELLED).event(CANCEL);
  }

  @Override
  public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
    states
        .withStates()
        .initial(SUBMITTED)
        .state(PAID)
        .end(FULFILLED)
        .end(CANCELLED);
  }

  @Override
  public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
    config.withConfiguration()
        .autoStartup(false)
        .listener(new StateMachineListenerAdapter<>() {
          @Override
          public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
            log.info("State changed: {} -> {}", from.getId(), to.getId());
          }
        });
  }
}
