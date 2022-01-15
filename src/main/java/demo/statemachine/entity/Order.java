package demo.statemachine.entity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue
  private Long id;
  @CreationTimestamp
  private Instant createdDate;
  private OrderState state;

  public Order(OrderState state) {
    this.state = state;
  }

}
