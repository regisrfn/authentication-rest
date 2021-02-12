package com.rufino.server.validation;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class ValidateEmail implements Predicate<String>{
    private static final Predicate<String> EMAIL_REGEX = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", Pattern.CASE_INSENSITIVE).asPredicate();

    @Override
    public boolean test(String email) {
        return EMAIL_REGEX.test(email);
    }
    
}