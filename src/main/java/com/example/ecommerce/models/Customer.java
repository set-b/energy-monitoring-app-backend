package com.example.ecommerce.models;

import static com.example.ecommerce.constants.StringConstants.REQUIRED_FIELD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * This Customer Entity contains all information and properties about a Customer Object(in this
 */
@Entity
@Table(name = "customers")
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "name " + REQUIRED_FIELD)
  @Column(name = "customer_name")
  private String name;

  @NotBlank(message = "email " + REQUIRED_FIELD)
  @Email
  @Column(unique = true)
  private String email;

  @Embedded
  @Valid
  private Address address;

  public Customer() {
  }

  public Customer(String name, String email, Address address) {
    this.name = name;
    this.email = email;
    this.address = address;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  @Override
  public String toString() {
    return "Customer{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", address=" + address +
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
    Customer customer = (Customer) o;
    return Objects.equals(id, customer.id) && Objects.equals(name, customer.name)
        && Objects.equals(email, customer.email) && Objects.equals(address,
        customer.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email, address);
  }

  @JsonIgnore
  public boolean isEmpty() {
    return
        Objects.isNull(id) &&
            Objects.isNull(name) &&
            Objects.isNull(email) &&
            Objects.isNull(address);
  }
}
