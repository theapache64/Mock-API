package com.theah64.mock_api.lab

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList

object CodeGen2 {

    @JvmStatic
    fun main(args: Array<String>) {

        val jsonString = StringBuilder()
        val jsonFile = File("sample.json")
        val mainClassName = "Sample"
        val isRetrofitModel = true

        val variables = ArrayList<Class.Variable>()
        variables.add(Class.Variable("error", "boolean"))
        variables.add(Class.Variable("message", "String"))
        variables.add(Class.Variable("data", "Data"))

        val mainClass = Class(mainClassName, null, variables)
        val pojoBuilder = StringBuilder()

        //Package name
        pojoBuilder.append("package com.glowsis.glowmetric.api.responses;\n\n")

        //Serialized name
        pojoBuilder.append("import com.google.gson.annotations.SerializedName;\n\n")

        //Signature
        pojoBuilder.append("//Developed using mock api\n\n")

        //CodeGen2 class signature
        pojoBuilder.append("public class ").append(mainClass.name).append(" {\n\n")

        val constructorVariables = StringBuilder()
        val constructorInitializer = StringBuilder()
        val getterBuilder = StringBuilder()

        //Building variables
        for (variable in variables) {

            if (isRetrofitModel) {
                //Retrofit model support
                pojoBuilder.append(String.format("\t@SerializedName(\"%s\")\n", variable.name))
            }

            constructorVariables.append(String.format("%s %s,", variable.dataType, variable.name))
            constructorInitializer.append(String.format("\n\t\tthis.%s = %s;", variable.name, variable.name))


            //Declaration
            pojoBuilder.append(String.format("\tprivate final %s %s;\n\n", variable.dataType, variable.name))


        }


        //CodeGen2 class constructor
        pojoBuilder
                .append("\tpublic ")
                .append(mainClass.name)
                .append("(")
                .append(constructorVariables.substring(0, constructorVariables.length - 1))
                .append("){")
                .append(constructorInitializer).append("\n\t}")


        save(pojoBuilder.toString())
    }

    private fun save(data: String) {
        //Finally writing it to a file
        val javaFile = File("Sample.java")
        try {
            val bw = BufferedWriter(FileWriter(javaFile))
            bw.write(data)
            bw.flush()
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    internal class Class(
            val name: String,
            val classes: List<Class>?,
            val variables: List<Variable>
    ) {
        internal class Variable(
                val name: String,
                val dataType: String
        )
    }


}
