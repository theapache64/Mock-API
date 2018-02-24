package com.theah64.mock_api.lab;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 4/1/18.
 */
public class Main {

    public static void main(String[] args) throws IOException, JSONException {

        final StringBuilder jsonString = new StringBuilder();
        final BufferedReader br = new BufferedReader(new FileReader(new File("sample.json")));
        String line = null;
        while ((line = br.readLine()) != null) {
            jsonString.append(line).append("\n");
        }
        br.close();

        final Matcher pattern = Pattern.compile("\\{SimpleDateFormat (.+)\\}").matcher(jsonString);
        if (pattern.find()) {
            System.out.println(pattern.group(1));
        }
    }


}
