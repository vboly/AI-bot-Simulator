package org.Simulator.Utilities;

import java.security.SecureRandom;

public class strings {
    static String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";     
    public static String Convert(Object val) {
        return String.valueOf(val);
    }   
    public static String random_character() {
        SecureRandom  Seed        = new SecureRandom();
        int num  = Seed.nextInt(characters.length());
        return Convert(characters.charAt(num));
    }
    public static String random_string(int length) {
        String newstring = "";
        for (int index = 0; index < length; index++) {
            StringBuilder Builder = new StringBuilder(newstring);
            Builder.append(random_character());
            newstring = Builder.toString();
        }
        return newstring;
    }
}
