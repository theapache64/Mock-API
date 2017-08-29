package com.theah64.mock_api.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyClass {


    public static void main(String[] args) {


        final int gridCount = 3;

        final List<String> categories = new ArrayList<>();
        categories.add("Category 1");
        categories.add("Category 2");
        categories.add("Category 3");
        categories.add("Category 4");
        categories.add("Category 5");
        categories.add("Category 6");
        categories.add("Category 7");
        categories.add("Category 8");
        categories.add("Category 9");
        categories.add("Category 10");

        List<List<String>> partitions = new LinkedList<>();
        for (int i = 0; i < categories.size(); i += gridCount) {
            partitions.add(categories.subList(i,
                    Math.min(i + gridCount, categories.size())));
        }

        System.out.println(partitions);
    }

}
