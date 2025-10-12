package com.example.ecommerce.models;

import static com.example.ecommerce.constants.StringConstants.REQUIRED_FIELD;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.OptBoolean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 * This Order Entity contains all information and properties about an Order and its properties.
 */
@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Valid
  private Long customerId;

  @NotNull(message = "Date " + REQUIRED_FIELD)
  @JsonFormat(pattern = "yyyy-MM-dd", lenient = OptBoolean.FALSE)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date date;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<Item> items = new ArrayList<>();

  @NotNull(message = "order total " + REQUIRED_FIELD)
  @NumberFormat(style = Style.CURRENCY)
  private BigDecimal orderTotal;

  public Order() {
  }

  public Order(Long customerId, Date date, List<Item> items, BigDecimal orderTotal) {
    this.customerId = customerId;
    this.date = date;
    this.items = items;
    this.orderTotal = orderTotal;
  }

  public void addItemToOrder(Item item) {
    item.setOrder(this);
    this.items.add(item);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public BigDecimal getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(BigDecimal orderTotal) {
    this.orderTotal = orderTotal;
  }

  @Override
  public String toString() {
    return "Order{" +
        "id=" + id +
        ", customerId=" + customerId +
        ", date=" + date +
        ", items=" + items +
        ", orderTotal=" + orderTotal +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return Objects.equals(id, order.id) && Objects.equals(customerId,
        order.customerId) && Objects.equals(date, order.date) && Objects.equals(
        items, order.items) && Objects.equals(orderTotal, order.orderTotal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, customerId, date, items, orderTotal);
  }

  @JsonIgnore
  public boolean isEmpty() {
    return
        Objects.isNull(id) &&
            Objects.isNull(customerId) &&
            Objects.isNull(date) &&
            Objects.isNull(items) &&
            Objects.isNull(orderTotal);
  }
}
