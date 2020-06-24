package com.prokarma.oneclick.stacks.bo;

import com.prokarma.oneclick.stacks.service.MandatoryStackVariablesValidator;
import com.prokarma.oneclick.stacks.service.MandatoryStackVariablesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MandatoryStackVariablesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MandatoryStackVariablesValidation {

    String message() default "mandatory variables should not be blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
