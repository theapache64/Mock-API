package com.theah64.mock_api.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MyClass {

    private static final String IPHONE_FLIPKART = "https://www.flipkart.com/apple-iphone-7-plus-black-128-gb/p/itmen6dasgrskmyh?pid=MOBEMK62XSANTWGZ&srno=b_1_2&otracker=browse&lid=LSTMOBEMK62XSANTWGZ0SIVZ6";


    public static void main(String[] args) throws IOException {

        final URL url = new URL(IPHONE_FLIPKART);
        final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        final StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        System.out.println(sb);
    }
}
