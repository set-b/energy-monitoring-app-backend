package com.example.ecommerce.models;

import static com.example.ecommerce.constants.StringConstants.REQUIRED_FIELD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This Greeting Entity contains all information and properties about a Greeting Object (in this
 * case, the text property of the Greeting).
 */
@Entity
@Table(name = "greetings")
public class Greeting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  @NotNull(message = "text " + REQUIRED_FIELD)
  @Size(min = 2, max = 100, message = "Greeting must be between 2 and 100 characters in length")
  private String text;

  public Greeting() {
  }

  public Greeting(String greeting) {
    this.text = greeting;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String greeting) {
    this.text = greeting;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Greeting greeting1 = (Greeting) o;
    return id == greeting1.id && Objects.equals(text, greeting1.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, text);
  }

  @Override
  public String toString() {
    return "Greeting{" + "Id=" + id + ", Text='" + text + '\'' + '}';
  }

  @JsonIgnore
  public boolean isEmpty() {
    return Objects.isNull(id) &&
        Objects.isNull(text);
  }

}
