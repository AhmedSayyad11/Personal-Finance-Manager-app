package edu.birzeit.courseproject.utils;


import android.util.Patterns;

public class Validators {

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidName(String s) {
        if (s == null) return false;
        int n = s.trim().length();
        return n >= 3 && n <= 10;
    }

    // 6–12 chars, includes 1 digit, 1 lowercase, 1 uppercase
    public static boolean isValidPassword(String p) {
        if (p == null) return false;
        String pass = p.trim();
        if (pass.length() < 6 || pass.length() > 12) return false;

        boolean hasDigit = false, hasLower = false, hasUpper = false;

        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            if (Character.isDigit(c)) hasDigit = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
        }
        return hasDigit && hasLower && hasUpper;
    }
}
