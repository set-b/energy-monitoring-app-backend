package com.example.ecommerce.constants;

/**
 * This class contains constants used for mapping endpoints and relaying error messages. They are
 * used in the Controllers and Service Implementation classes.
 */
public class StringConstants {

  public static final String REQUIRED_FIELD = "is a required field";
  public static final String QUERY_REQUEST = "Query request received for ";
  public static final String GET_REQUEST_ENERGY = "Get request received for ";
  public static final String NOT_FOUND = "Could not locate resource: ";
  public static final String VALIDATION_ERROR = "Validation error";
  public static final String UPDATE_REQUEST = "Put request received for ";
  public static final String DELETE_REQUEST = "Delete request received for ";
  public static final String POST_REQUEST = "Post request received for ";
  public static final String BAD_DATA = "Bad data";
  public static final String SERVER_ERROR = "Server error";
  public static final String UNEXPECTED_ERROR = "Unexpected server error";

  public static final String RESIDENT = "resident";
  public static final String COMMUNITY_HOUSING = "resident";
  public static final String CARPORT= "carport";


  public static final String ENERGY_DATA = "all energy data";

  public static final String CONFLICT = "Conflict";

  //endpoint constants
  public static final String CONTEXT_GREETINGS = "/greetings";
  public static final String CONTEXT_ENERGY = "/energy_data";
  public static final String CONTEXT_CUSTOMERS = "/customers";
  public static final String CONTEXT_ORDERS = "/orders";
  public static final String CONTEXT_PRODUCTS = "/products";
  public static final String CONTEXT_USERS = "/users";

  //roles
  public static final String EMPLOYEE = "employee";
  public static final String ADMIN = "admin";
}
