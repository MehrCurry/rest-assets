package de.gzockoll.prototype.assets.util;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public abstract class ValidateableObject {
    private static final Validator VALIDATOR= Validation.buildDefaultValidatorFactory().getValidator();

    public Set<ConstraintViolation<ValidateableObject>> validationErrors() {
        return VALIDATOR.validate(this);
    }

    public boolean isValid() {
        return validationErrors().size()==0;
    }

    public void validate() throws ConstraintViolationException {
        Set<ConstraintViolation<ValidateableObject>> errors = validationErrors();
        if (errors.size()>0) {
            throw new ConstraintViolationException("Validation",errors);
        }

    }
}