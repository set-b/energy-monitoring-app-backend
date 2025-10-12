package com.example.ecommerce.models;

import static com.example.ecommerce.constants.StringConstants.BAD_DATA;
import static com.example.ecommerce.constants.StringConstants.REQUIRED_FIELD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * This User Entity contains all information and properties about a User Object.
 */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "name " + REQUIRED_FIELD)
  @Column(name = "user_name")
  private String name;

  @NotBlank(message = "title " + REQUIRED_FIELD)
  private String title;

  @NotNull(message = "roles " + REQUIRED_FIELD)
  private String[] roles;

  @NotBlank(message = "email " + REQUIRED_FIELD)
  @Email
  @Column(unique = true)
  private String email;

  @NotBlank(message = "password " + REQUIRED_FIELD)
  @Length(min = 8, message = BAD_DATA + "password must be at least 8 characters in length")
  private String password;

  public User() {
  }

  public User(String name, String title,
      @NotNull(message = "roles" + REQUIRED_FIELD) String[] roles,
      String email, String password) {
    this.name = name;
    this.title = title;
    this.roles = roles;
    this.email = email;
    this.password = password;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String[] getRoles() {
    return roles;
  }

  public void setRoles(String[] roles) {
    this.roles = roles;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", title='" + title + '\'' +
        ", roles=" + Arrays.toString(roles) +
        ", email='" + email + '\'' +
        ", password='" + password + '\'' +
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
    User user = (User) o;
    return Objects.equals(id, user.id) && Objects.equals(name, user.name)
        && Objects.equals(title, user.title) && Arrays.equals(roles, user.roles)
        && Objects.equals(email, user.email) && Objects.equals(password,
        user.password);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(id, name, title, email, password);
    result = 31 * result + Arrays.hashCode(roles);
    return result;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return
        Objects.isNull(id) &&
            Objects.isNull(name) &&
            Objects.isNull(title) &&
            Objects.isNull(roles) &&
            Objects.isNull(email) &&
            Objects.isNull(password);
  }
}
