package com.rufino.server.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.rufino.server.constraints.EmailConstraint;

import org.springframework.util.StringUtils;

public class EmailValidator implements 
  ConstraintValidator<EmailConstraint, String> {

    @Override
    public void initialize(EmailConstraint email) {
    }

    @Override
    public boolean isValid(String email,
      ConstraintValidatorContext cxt) {
        return StringUtils.hasText(email) && new ValidateEmail().test(email);
    }

}