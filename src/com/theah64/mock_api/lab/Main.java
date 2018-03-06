package com.theah64.mock_api.lab;

import org.json.JSONException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 4/1/18.
 */
public class Main {

    private static final String CONDITIONED_PATTERN = "\\{(?<val1>.+)\\s*(?<operator>==|===|!=|>|<|>=|<=)\\s*(?<val2>.+)\\s*\\?\\s*(?<trueVal>.+)\\:\\s*(?<falseVal>.+)\\}";

    public static void main(String[] args) throws IOException, JSONException {

        String jsonResp = "Here's some text {10 > 3 ? trueVallGoesHere : falseValGoesHere} and some other text {10 > 3 ? trueVallGoesHere : falseValGoesHere} and few other text";


        //Checking if conditioned response
        final Pattern pattern = Pattern.compile(CONDITIONED_PATTERN, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(jsonResp);

        if (matcher.find()) {
            do {


                //{10>3 ? young : old}
                System.out.println("------------------------------");
                System.out.println("Val 1 : " + matcher.group("val1"));
                System.out.println("Val 2 : " + matcher.group("val2"));
                System.out.println("Operator : " + matcher.group("operator"));
                System.out.println("True Val : " + matcher.group("trueVal"));
                System.out.println("False Val : " + matcher.group("falseVal"));



            } while (matcher.find());
        }


    }


}
