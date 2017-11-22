package com.theah64.mock_api.test;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class MyClass {

    public static void main(String[] args) {

        //Reading sample file
        final File sampleFile = new File(System.getProperty("user.dir") + File.separator + "sample.json");
        final StringBuilder jsonData = new StringBuilder();
        try {
            final BufferedReader br = new BufferedReader(new FileReader(sampleFile));
            String line;
            while ((line = br.readLine()) != null) {
                jsonData.append(line).append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final JSONObject joResponse = new JSONObject(jsonData);
        final String pojoCode = CodeGen.getCode(joResponse);
        System.out.println(pojoCode);
    }

    static class CodeGen {

        public static String getCode(JSONObject joResponse) {

            final List<Class> classes = getClasses(joResponse);

            return null;
        }

        private static List<Class> getClasses(JSONObject joResponse) {

            return null;
        }
    }


    static class Class {

        private final List<Variable> variables;

        Class(List<Variable> variables) {
            this.variables = variables;
        }

    }

    static class Variable {

        private final String dataType, variableName;

        Variable(String dataType, String variableName) {
            this.dataType = dataType;
            this.variableName = variableName;
        }

        public String getDataType() {
            return dataType;
        }

        public String getVariableName() {
            return variableName;
        }
    }


}
