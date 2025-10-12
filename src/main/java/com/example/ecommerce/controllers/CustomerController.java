package com.example.ecommerce.controllers;

import static com.example.ecommerce.constants.StringConstants.CONTEXT_CUSTOMERS;
import static com.example.ecommerce.constants.StringConstants.DELETE_REQUEST;
import static com.example.ecommerce.constants.StringConstants.POST_REQUEST;
import static com.example.ecommerce.constants.StringConstants.QUERY_REQUEST;
import static com.example.ecommerce.constants.StringConstants.UPDATE_REQUEST;

import com.example.ecommerce.models.Customer;
import com.example.ecommerce.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller takes methods from the CustomerServiceImpl class through CustomerService, and
 * uses these methods to manipulate Customer Objects/Entities. It handles requests about Customer
 * information, which the user can send via URL.
 */
@Tag(name = "Customer Controller", description = "Customer management API")
@RestController
@RequestMapping(CONTEXT_CUSTOMERS)
public class CustomerController {

  private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

  @Autowired
  private CustomerService customerService;

  /**
   * Queries Customers.
   *
   * @param customer the Customer(s) matching the user's supplied information.
   * @return a list of Customers, containing Objects which match the information supplied, or a list
   * of all Customers if the query is empty.
   */
  @GetMapping
  @Operation(summary = "Query Customers",
      description = "get all customers, or customers filtered according to custom query",
      responses = {
          @ApiResponse(responseCode = "200", description = "List of Customers")
      })
  public ResponseEntity<List<Customer>> queryCustomers(Customer customer) {
    logger.info(new Date() + QUERY_REQUEST + customer.toString());

    return new ResponseEntity<>(customerService.queryCustomers(customer), HttpStatus.OK);
  }

  /**
   * Retrieves the Customer tht has the given id.
   *
   * @param id the id of the Customer to be retrieved.
   * @return a Customer Object with the given id, if it exists.
   */
  @GetMapping("/{id}")
  @Operation(summary = "get Customer by customer id",
      parameters = {
          @Parameter(name = "id", required = true,
              description = "The id of the customer to be retrieved", allowEmptyValue = false),
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "Customer with identical id"),
          @ApiResponse(responseCode = "400", description = "Id must be positive"),
          @ApiResponse(responseCode = "404", description = "Customer with given id not found")
      })
  public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
    logger.info(new Date() + QUERY_REQUEST + "customer with id " + id);

    return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK);
  }

  /**
   * Saves a Customer Object with the data/state provided by the user to the database.
   *
   * @param customer the Customer to be saved.
   * @return the dta of the Customer that was saved to the database, if successful.
   */
  @PostMapping
  @Operation(summary = "post Customer", description = "Creates a customer from the request body",
      responses = {
          @ApiResponse(responseCode = "201", description = "Customer created"),
          @ApiResponse(responseCode = "400", description = "Invalid Customer data"),
          @ApiResponse(responseCode = "409", description = "Customer email already in use")
      })
  public ResponseEntity<Customer> postCustomer(@Valid @RequestBody Customer customer) {
    logger.info(new Date() + POST_REQUEST + "customer");

    return new ResponseEntity<Customer>(customerService.addCustomer(customer), HttpStatus.CREATED);
  }

  /**
   * Finds and replaces a Customer Object with another, based on the id and data/state supplied by
   * the user.
   *
   * @param id       the id of the Customer to be updated.
   * @param customer the Customer data which will replace the old Customer data.
   * @return the successfully updated Customer
   */
  @PutMapping("/{id}")
  @Operation(summary = "put Customer", description = "Updates a Customer based on the request body",
      responses = {
          @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid Customer data"),
          @ApiResponse(responseCode = "409", description = "Customer email already in use"),
          @ApiResponse(responseCode = "404", description = "Customer not found")
      })
  public ResponseEntity<Customer> updateCustomerById(@PathVariable Long id,
      @Valid @RequestBody Customer customer) {
    logger.info(new Date() + UPDATE_REQUEST + "customer with id " + id);

    return new ResponseEntity<Customer>(customerService.updateCustomerById(id, customer),
        HttpStatus.OK);
  }

  /**
   * Finds and deletes a Customer from the database with the id given by the user.
   *
   * @param id the id of the Customer to be deleted.
   * @return a deleted status, if the id exists in the database.
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "delete Customer", description = "Deletes a customer with matching id",
      responses = {
          @ApiResponse(responseCode = "204", description = "No Content. Customer deleted"),
          @ApiResponse(responseCode = "404", description = "Customer not found"),
          @ApiResponse(responseCode = "400", description = "id must be positive")
      })
  public ResponseEntity<Customer> deleteCustomerById(@PathVariable Long id) {
    logger.info(new Date() + DELETE_REQUEST + "customer with id " + id);

    customerService.deleteCustomerById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
