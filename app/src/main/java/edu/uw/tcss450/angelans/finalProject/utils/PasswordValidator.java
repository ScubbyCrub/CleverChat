package edu.uw.tcss450.angelans.finalProject.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface PasswordValidator  extends Function<String, Optional<PasswordValidator.ValidationResult>> {

    static final int MAX_CHAR_INPUT_LENGTH = 255;

    //Password length more than 7
    static PasswordValidator checkPWLength() {
        return checkPWLength(7);
    }

    static PasswordValidator checkPWLength(int length) {
        return password ->
                Optional.of(password.length() > length
                        && password.length() <= MAX_CHAR_INPUT_LENGTH ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_INVALID_LENGTH);
    }

    //Contain at least 1 digit
    static PasswordValidator checkPwdDigit() {
        return password ->
                Optional.of(checkStringContains(password, Character::isDigit) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_MISSING_DIGIT);
    }

    //Contain at least 1 uppercase letter
    static PasswordValidator checkPwdUpperCase() {
        return password ->
                Optional.of(checkStringContains(password, Character::isUpperCase) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_MISSING_UPPER);
    }

    //Contain at least 1 lowercase letter
    static PasswordValidator checkPwdLowerCase() {
        return password ->
                Optional.of(checkStringContains(password, Character::isLowerCase) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_MISSING_LOWER);
    }

    //Contain 1 special character
    static PasswordValidator checkPwdSpecialChar() {
        return checkPwdSpecialChar("@#$%&*!?");
    }

    static PasswordValidator checkPwdSpecialChar(String specialChars) {
        return password ->
                Optional.of(checkStringContains(password,
                        c -> specialChars.contains(Character.toString((char) c))) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_MISSING_SPECIAL);
    }

    // Checks if an input only contains letters, spaces, or hyphens as chars.
    static PasswordValidator checkPwdOnlyHasLettersSpacesHyphens() {
        return password ->
                Optional.of(hasLettersSpacesHyphens(password) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_INCLUDES_EXCLUDED);
    }

    // Helper method for checkPwdOnlyHasLettersSpacesHyphens()
    static boolean hasLettersSpacesHyphens(String thePassword) {
        boolean checkResult = true;
        for (int i = 0; i < thePassword.length(); i++) {
            char currentChar = thePassword.charAt(i);
            if (currentChar < 32                            // 32 = space
                || (currentChar > 32 && currentChar < 45)   // 45 = hyphen
                || (currentChar > 45 && currentChar < 65)   // 65-90 = A-Z
                || (currentChar > 90 && currentChar < 97)   // 97-122 = a-z
                || (currentChar > 122)
            ) { // If any char falls in illegal range, set to return false
                checkResult = false;
                break;
            }
        }
        return checkResult;
    }

    // Checks if an input only contains letters, numbers, hyphens, underscores, periods, @ signs
    static PasswordValidator checkPwdOnlyHasLettersNumbersHyphensUnderscoresPeriodsAtSign() {
        return password ->
                Optional.of(hasLettersNumbersHyphensUnderscoresPeriodsAtSign(password) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_INCLUDES_EXCLUDED);
    }

    // Helper method for checkPwdOnlyHasLettersNumbersHyphensUnderscoresPeriodsAtSign()
    static boolean hasLettersNumbersHyphensUnderscoresPeriodsAtSign(String thePassword) {
        boolean checkResult = true;
        for (int i = 0; i < thePassword.length(); i++) {
            char currentChar = thePassword.charAt(i);
            if (currentChar < 45                                // 45-46 = hyphen, period
                    || (currentChar > 46 && currentChar < 48)   // 48-57 = numbers
                    || (currentChar > 57 && currentChar < 64)   // 64-90 = @ sign, A-Z
                    || (currentChar > 90 && currentChar < 95)   // 95 = underscore
                    || (currentChar > 95 && currentChar < 97)   // 97-122 = a-z
                    || (currentChar > 122)
            ) { // If any char falls in illegal range, set to return false
                checkResult = false;
                break;
            }
        }
        return checkResult;
    }

    //Check if the password contain any of the characters found in excludeChars
    static PasswordValidator checkPwdDoNotInclude(String excludeChars) {
        return password ->
                Optional.of(!checkStringContains(password, //NOTE the !
                        c -> excludeChars.contains(Character.toString((char) c))) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_INCLUDES_EXCLUDED);
    }

    //No white space available
    static PasswordValidator checkExcludeWhiteSpace() {
        return password ->
                Optional.of(!checkStringContains(password, //NOTE the !
                        Character::isWhitespace) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_INCLUDES_WHITESPACE);
    }

    //Predicate test
    static PasswordValidator checkClientPredicate(Predicate<String> theTest) {
        return password ->
                Optional.of(theTest.test(password) ?
                        ValidationResult.SUCCESS : ValidationResult.PWD_CLIENT_ERROR);
    }

    //Contain at least 1 char
    static boolean checkStringContains(String check, IntPredicate test) {
        return check.chars().filter(test).count() > 0;
    }

    //this password = other (re-enter) password

    default PasswordValidator and(PasswordValidator other) {
        return password -> this.apply(password)
                .flatMap(result -> result == ValidationResult.SUCCESS ?
                        other.apply(password) : Optional.of(result));
    }

    default PasswordValidator or(PasswordValidator other) {
        return password -> this.apply(password)
                .flatMap(result -> result == ValidationResult.SUCCESS ?
                        Optional.of(result): other.apply(password));
    }

    default void processResult(Optional<ValidationResult> result,
                               Runnable onSuccess,
                               Consumer<ValidationResult> onError) {
        if (result.isPresent()) {
            if (result.get() == ValidationResult.SUCCESS) {
                onSuccess.run();
            } else {
                onError.accept(result.get());
            }
        } else {
            throw new IllegalStateException("Nothing to process");
        }
    }

    enum ValidationResult {
        SUCCESS,
        PWD_INVALID_LENGTH,
        PWD_MISSING_DIGIT,
        PWD_MISSING_UPPER,
        PWD_MISSING_LOWER,
        PWD_MISSING_SPECIAL,
        PWD_INCLUDES_EXCLUDED,
        PWD_INCLUDES_WHITESPACE,
        PWD_CLIENT_ERROR
    }
}
