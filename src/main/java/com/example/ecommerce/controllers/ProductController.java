package com.example.ecommerce.controllers;

import static com.example.ecommerce.constants.StringConstants.CONTEXT_PRODUCTS;
import static com.example.ecommerce.constants.StringConstants.DELETE_REQUEST;
import static com.example.ecommerce.constants.StringConstants.POST_REQUEST;
import static com.example.ecommerce.constants.StringConstants.QUERY_REQUEST;
import static com.example.ecommerce.constants.StringConstants.UPDATE_REQUEST;

import com.example.ecommerce.models.Product;
import com.example.ecommerce.services.ProductService;
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
 * This controller takes methods from the ProductServiceImpl class through ProductService, and uses
 * these methods to manipulate Product Objects/Entities. It handles requests about Product
 * information, which the user can send via URL.
 */
@Tag(name = "Product Controller", description = "Product management API")
@RestController
@RequestMapping(CONTEXT_PRODUCTS)
public class ProductController {

  private final Logger logger = LoggerFactory.getLogger(ProductController.class);

  @Autowired
  private ProductService productService;

  /**
   * Queries Products.
   *
   * @param product the Product(s) matching the user's supplied information.
   * @return a list of Products, containing Objects which match the information supplied, or a list
   * of all Products if the query is empty.
   */
  @GetMapping
  @Operation(summary = "Query Products",
      description = "get all products, or products filtered according to custom query",
      responses = {
          @ApiResponse(responseCode = "200", description = "List of Products")
      })
  public ResponseEntity<List<Product>> queryProducts(Product product) {
    logger.info(new Date() + QUERY_REQUEST + product.toString());

    return new ResponseEntity<>(productService.queryProducts(product), HttpStatus.OK);
  }

  /**
   * Retrieves the Product tht has the given id.
   *
   * @param id the id of the Product to be retrieved.
   * @return a Product Object with the given id, if it exists.
   */
  @GetMapping("/{id}")
  @Operation(summary = "get Product by product id",
      parameters = {
          @Parameter(name = "id", required = true,
              description = "The id of the product to be retrieved", allowEmptyValue = false),
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "Product with identical id"),
          @ApiResponse(responseCode = "400", description = "Id must be positive"),
          @ApiResponse(responseCode = "404", description = "Product with given id not found")
      })
  public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    logger.info(new Date() + QUERY_REQUEST + "product with id " + id);

    return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
  }

  /**
   * Saves a Product Object with the data/state provided by the user to the database.
   *
   * @param product the Product to be saved.
   * @return the dta of the Product that was saved to the database, if successful.
   */
  @PostMapping
  @Operation(summary = "post Product", description = "Creates a product from the request body",
      responses = {
          @ApiResponse(responseCode = "201", description = "Product created"),
          @ApiResponse(responseCode = "400", description = "Invalid Product data"),
          @ApiResponse(responseCode = "409", description = "Product sku already in use")
      })
  public ResponseEntity<Product> postProduct(@Valid @RequestBody Product product) {
    logger.info(new Date() + POST_REQUEST + "product");

    return new ResponseEntity<Product>(productService.addProduct(product), HttpStatus.CREATED);
  }

  /**
   * Finds and replaces a Product Object with another, based on the id and data/state supplied by
   * the user.
   *
   * @param id      the id of the Product to be updated.
   * @param product the Product data which will replace the old Product data.
   * @return the successfully updated Product
   */
  @PutMapping("/{id}")
  @Operation(summary = "put Product", description = "Updates a Product based on the request body",
      responses = {
          @ApiResponse(responseCode = "200", description = "Product updated successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid Product data"),
          @ApiResponse(responseCode = "409", description = "Product sku already in use"),
          @ApiResponse(responseCode = "404", description = "Product not found")
      })
  public ResponseEntity<Product> updateProductById(@PathVariable Long id,
      @Valid @RequestBody Product product) {
    logger.info(new Date() + UPDATE_REQUEST + "product with id " + id);

    return new ResponseEntity<Product>(productService.updateProductById(id, product),
        HttpStatus.OK);
  }

  /**
   * Finds and deletes a Product from the database with the id given by the user.
   *
   * @param id the id of the Product to be deleted.
   * @return a deleted status, if the id exists in the database.
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "delete Product", description = "Deletes a product with matching id",
      responses = {
          @ApiResponse(responseCode = "204", description = "No Content. Product deleted"),
          @ApiResponse(responseCode = "404", description = "Product not found"),
          @ApiResponse(responseCode = "400", description = "id must be positive")
      })
  public ResponseEntity<Product> deleteProductById(@PathVariable Long id) {
    logger.info(new Date() + DELETE_REQUEST + "product with id " + id);

    productService.deleteProductById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
