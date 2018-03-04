package com.theah64.mock_api.lab;

import org.json.JSONException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 4/1/18.
 */
public class Main {

    private static final String CONDITIONED_PATTERN = "(\\{(?:.+)\\s*(?:==|===|!=|>|<|>=|<=)\\s*(?:.+)\\s*\\?\\s*(?:.+)\\:\\s*(?:.+)\\})";

    public static void main(String[] args) throws IOException, JSONException {

        String jsonResp = "{ \"error\": false, \"message\": \"Bridgett Carroll 52\", \"dyn\":\"She is {10>3 ? trueVal : falseVal}\",  \"dyn\":\"She is {10>3 ? trueVal : falseVal}\",\"data\": {} }";


        //Checking if conditioned response
        final Pattern pattern = Pattern.compile(CONDITIONED_PATTERN, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(jsonResp);

        if (matcher.find()) {
            do {


                //{10>3 ? young : old}
                System.out.println(matcher.group());


            } while (matcher.find());
        }


    }


}
