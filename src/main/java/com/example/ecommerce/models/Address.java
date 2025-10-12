package com.example.ecommerce.models;

import static com.example.ecommerce.constants.StringConstants.REQUIRED_FIELD;

import com.example.ecommerce.validators.State;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * This Address embeddable contains all information and properties about an Address
 */
@Embeddable
@Table(name = "addresses")
public class Address {

  @NotBlank(message = "street " + REQUIRED_FIELD)
  private String street;

  @NotBlank(message = "city " + REQUIRED_FIELD)
  private String city;

  @NotBlank(message = "state " + REQUIRED_FIELD)
  @State
  private String state;

  @NotBlank(message = "zipcode " + REQUIRED_FIELD)
  @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "zipcode must conform to standard US 5 digit "
      + "code or US zipcode with  4 digit extension (i.e. '12345' or '12345-1234').")
  private String zipcode;

  public Address() {
  }

  public Address(String street, String city, String state, String zipcode) {
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipcode = zipcode;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  @Override
  public String toString() {
    return "Address{" +
        "street='" + street + '\'' +
        ", city='" + city + '\'' +
        ", state='" + state + '\'' +
        ", zipcode='" + zipcode + '\'' +
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
    Address address = (Address) o;
    return Objects.equals(street, address.street) && Objects.equals(city,
        address.city) && Objects.equals(state, address.state) && Objects.equals(
        zipcode, address.zipcode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, city, state, zipcode);
  }

  @JsonIgnore
  public boolean isEmpty() {
    return
        Objects.isNull(street) &&
            Objects.isNull(city) &&
            Objects.isNull(state) &&
            Objects.isNull(zipcode);
  }
}
