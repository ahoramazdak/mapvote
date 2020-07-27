package net.behpardaz.voting.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amin on 12/5/16.
 */

public class test {
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher("dshsldflhak 23423424");
        if(matcher.find())
            System.out.println(matcher.group());
    }
}
