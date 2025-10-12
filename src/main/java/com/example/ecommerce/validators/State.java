package com.example.ecommerce.validators;

import jakarta.validation.Constraint;

/**
 * The State interface contains the message for failed validation on the nested 'State' property of
 * a Customer's embedded Address.
 */
@Constraint(validatedBy = StateValidator.class)
public @interface State {

  String message() default "State must be an officially recognized abbreviation for US State or the District of Columbia";
}
