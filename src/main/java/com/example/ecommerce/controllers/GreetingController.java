package com.example.ecommerce.controllers;

import static com.example.ecommerce.constants.StringConstants.CONTEXT_GREETINGS;
import static com.example.ecommerce.constants.StringConstants.DELETE_REQUEST;
import static com.example.ecommerce.constants.StringConstants.POST_REQUEST;
import static com.example.ecommerce.constants.StringConstants.QUERY_REQUEST;
import static com.example.ecommerce.constants.StringConstants.UPDATE_REQUEST;

import com.example.ecommerce.exceptions.BadDataResponse;
import com.example.ecommerce.models.Greeting;
import com.example.ecommerce.services.GreetingService;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 * This controller takes methods from the GreetingServiceImpl class through GreetingService, and
 * uses these methods to manipulate Greeting Objects/Entities. It handles requests about Greeting
 * information, which the user can send via URL.
 */
@RestController
@RequestMapping(CONTEXT_GREETINGS)
public class GreetingController {

  private final Logger logger = LoggerFactory.getLogger(GreetingController.class);

  @Autowired
  private GreetingService greetingService;

  /**
   * Queries Greetings.
   *
   * @param greeting the Greeting(s) matching the user's supplied information.
   * @return a list of Greetings, containing Objects which match the information supplied, or a list
   * of all Greetings if the query is empty.
   */
  @GetMapping
  public ResponseEntity<List<Greeting>> queryGreetings(Greeting greeting) {
    logger.info(new Date() + QUERY_REQUEST + greeting.toString());

    return new ResponseEntity<>(greetingService.queryGreetings(greeting), HttpStatus.OK);
  }

  /**
   * Retrieves the Greeting tht has the given id.
   *
   * @param id the id of the Greeting to be retrieved.
   * @return a Greeting Object with the given id, if it exists.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Greeting> getGreetingById(@PathVariable Long id) {
    logger.info(new Date() + QUERY_REQUEST + "greeting with id " + id);

    return new ResponseEntity<>(greetingService.getGreetingById(id), HttpStatus.OK);
  }

  /**
   * Saves a Greeting Object with the data/state provided by the user to the database.
   *
   * @param greeting the Greeting to be saved.
   * @return the dta of the Greeting that was saved to the database, if successful.
   */
  @PostMapping
  public ResponseEntity<Greeting> postGreeting(@Valid @RequestBody Greeting greeting) {
    logger.info(new Date() + POST_REQUEST + "greeting");

    String pattern = "[^A-Za-z\\s\\?!.]";
    Pattern notAGreeting = Pattern.compile(pattern);
    Matcher matcher = notAGreeting.matcher(greeting.getText());
    if (matcher.find()) {
      throw new BadDataResponse("Greeting text must only contain letters and punctuation");
    }
    return new ResponseEntity<Greeting>(greetingService.postGreeting(greeting), HttpStatus.CREATED);
  }

  /**
   * Finds and replaces a Greeting Object with another, based on the id and data/state supplied by
   * the user.
   *
   * @param id       the id of the Greeting to be updated.
   * @param greeting the Greeting data which will replace the old Greeting data.
   * @return the successfully updated Greeting
   */
  @PutMapping("/{id}")
  public ResponseEntity<Greeting> updateGreetingById(@PathVariable Long id,
      @Valid @RequestBody Greeting greeting) {
    logger.info(new Date() + UPDATE_REQUEST + "greeting with id " + id);

    return new ResponseEntity<Greeting>(greetingService.updateGreetingById(id, greeting),
        HttpStatus.OK);
  }

  /**
   * Finds and deletes a Greeting from the database with the id given by the user.
   *
   * @param id the id of the Greeting to be deleted.
   * @return a deleted status, if the id exists in the database.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Greeting> deleteGreetingById(@PathVariable Long id) {
    logger.info(new Date() + DELETE_REQUEST + "greeting with id " + id);

    greetingService.deleteGreetingById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
