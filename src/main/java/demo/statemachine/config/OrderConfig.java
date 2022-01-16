package demo.statemachine.config;

import static demo.statemachine.entity.OrderEvent.CANCEL;
import static demo.statemachine.entity.OrderEvent.PAY;
import static demo.statemachine.entity.OrderEvent.SHIP;
import static demo.statemachine.entity.OrderState.CANCELLED;
import static demo.statemachine.entity.OrderState.PAID;
import static demo.statemachine.entity.OrderState.SHIPPED;
import static demo.statemachine.entity.OrderState.SUBMITTED;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import demo.statemachine.entity.OrderEvent;
import demo.statemachine.entity.OrderState;
import demo.statemachine.listener.OrderListener;

@Configuration
@EnableStateMachineFactory
public class OrderConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

  @Override
  public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
    transitions
        .withExternal().source(SUBMITTED).target(PAID).event(PAY)
        .and()
        .withExternal().source(PAID).target(SHIPPED).event(SHIP)
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
        .end(SHIPPED)
        .end(CANCELLED);
  }

  @Override
  public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
    config.withConfiguration()
        .autoStartup(false)
        .listener(new OrderListener());
  }
}
