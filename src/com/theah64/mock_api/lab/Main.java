package com.theah64.mock_api.lab;

import org.json.JSONException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 4/1/18.
 */
public class Main {

    private static final String CONDITIONED_PATTERN = "\\{(?<val1>\\w+)\\s*(?<operator>==|===|!=|>|<|>=|<=)\\s*(?<val2>\\w+)\\s*\\?\\s*(?<trueVal>\\w+)\\s*:\\s*(?<falseVal>\\w+)\\}";

    public static void main(String[] args) throws IOException, JSONException {

        String jsonResp = "Here's some text {10 > 3 ? trueVallGoesHere : falseValGoesHere} and some other text {9 > 2 ? trueVallGoesHere1 : falseValGoesHere1} and few other text";


        //Checking if conditioned response
        final Pattern pattern = Pattern.compile(CONDITIONED_PATTERN, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(jsonResp);

        String a = null;
        StringBuffer sb = new StringBuffer();

        if (matcher.find()) {
            int i = 0;
            do {


                //{10>3 ? young : old}
                System.out.println("------------------------------");
                System.out.println("Val 1 : " + matcher.group("val1"));
                System.out.println("Val 2 : " + matcher.group("val2"));
                System.out.println("Operator : " + matcher.group("operator"));
                System.out.println("True Val : " + matcher.group("trueVal"));
                System.out.println("False Val : " + matcher.group("falseVal"));

                matcher.appendReplacement(sb, "ok" + (++i));

            } while (matcher.find());
        }

        System.out.println(sb);


    }


}
