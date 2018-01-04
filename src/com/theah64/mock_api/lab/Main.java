package com.theah64.mock_api.lab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        final StringBuilder jsonString = new StringBuilder();
        final File jsonFile = new File("sample.json");
        final String mainClassName = "Sample";

        final List<Class.Variable> variables = new ArrayList<>();
        variables.add(new Class.Variable("error", "boolean"));
        variables.add(new Class.Variable("message", "String"));
        variables.add(new Class.Variable("data", "Data"));

        final Class c = new Class(mainClassName, null, variables);
        final StringBuilder pojoBuilder = new StringBuilder();

        //Package name
        pojoBuilder.append("package com.glowsis.glowmetric.api.responses;\n\n");

        //Serialized name
        pojoBuilder.append("import com.google.gson.annotations.SerializedName;\n\n");

        //Signature
        pojoBuilder.append("//Developed using mock api\n\n");

        //Main class signature
        pojoBuilder.append("public class ").append(mainClassName).append(" {");


        save(pojoBuilder.toString());
    }

    private static void save(String data) {
        //Finally writing it to a file
        final File javaFile = new File("Sample.java");
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(javaFile));
            bw.write(data);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Class {

        private final String name;
        private final List<Class> classes;
        private final List<Variable> variables;

        Class(String name, List<Class> classes, List<Variable> variables) {
            this.name = name;
            this.classes = classes;
            this.variables = variables;
        }

        public List<Class> getClasses() {
            return classes;
        }

        public List<Variable> getVariables() {
            return variables;
        }

        static class Variable {

            private final String name, dataType;

            Variable(String name, String dataType) {
                this.name = name;
                this.dataType = dataType;
            }

            public String getName() {
                return name;
            }

            public String getDataType() {
                return dataType;
            }
        }
    }


}
