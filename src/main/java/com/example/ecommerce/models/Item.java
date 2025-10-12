package com.example.ecommerce.models;

import static com.example.ecommerce.constants.StringConstants.BAD_DATA;
import static com.example.ecommerce.constants.StringConstants.REQUIRED_FIELD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * This Item Entity contains all information and properties about an Item Object.
 */
@Entity
@Table(name = "items")
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Valid
  private Long productId;

  @NotNull(message = "quantity " + REQUIRED_FIELD)
  @Min(value = 0, message = BAD_DATA + " must be a value greater than or equal to zero")
  private Integer quantity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  private Order order;

  public Item() {
  }

  public Item(Long productId, Integer quantity, Order order) {
    this.productId = productId;
    this.quantity = quantity;
    this.order = order;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  @Override
  public String toString() {
    return "Item{" +
        "id=" + id +
        ", productId=" + productId +
        ", quantity=" + quantity +
        ", order=" + order +
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
    Item item = (Item) o;
    return Objects.equals(id, item.id) && Objects.equals(productId,
        item.productId) && Objects.equals(quantity, item.quantity)
        && Objects.equals(order, item.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, productId, quantity, order);
  }

  @JsonIgnore
  public boolean isEmpty() {
    return Objects.isNull(id) &&
        Objects.isNull(productId) &&
        Objects.isNull(quantity) &&
        Objects.isNull(order);
  }
}
