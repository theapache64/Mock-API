package com.theah64.mock_api.lab

import org.json.JSONException

import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by theapache64 on 4/1/18.
 */
object Main {

    @Throws(IOException::class, JSONException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val jsonResp = "{ \"error\": false, \"message\": \"ok `10 > 3 ? trueVal : falseVal` ok \", \"`8 > 4 ? trueVal : falseVal`\": {} }"
        println(jsonResp)
        println(ConditionedResponse.generate(jsonResp))

        /*jsonResp = ConditionedResponse.generate(jsonResp);
        System.out.println("-------------------");
        System.out.println(jsonResp);*/
    }

    object ConditionedResponse {

        private val CONDITIONED_PATTERN = "`(?<val1>[^=!><]+)\\s*(?<operator>==|!=|>|<|>=|<=)\\s*(?<val2>[^?]+)\\s*\\?\\s*(?<trueVal>[^:]+)\\s*:\\s*(?<falseVal>[^`]+)`"
        private val OPERATOR_EQUAL_TO = "=="
        private val OPERATOR_NOT_EQUAL_TO = "!="
        private val OPERATOR_GREATER_THAN = ">"
        private val OPERATOR_GREATER_THAN_OR_EQUAL_TO = ">="
        private val OPERATOR_LESS_THAN = "<"
        private val OPERATOR_LESS_THAN_OR_EQUAL_TO = "<="

        fun generate(jsonResp: String): String {

            val stringBuilder = StringBuilder()
            val arr = jsonResp.split(CONDITIONED_PATTERN.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()


            //Checking if conditioned response
            val pattern = Pattern.compile(CONDITIONED_PATTERN, Pattern.MULTILINE)
            val matcher = pattern.matcher(jsonResp)


            val sb = StringBuilder()

            if (matcher.find()) {
                var i = 0
                do {


                    //{10>3 ? young : old}

                    val val1 = matcher.group("val1").trim { it <= ' ' }
                    val val2 = matcher.group("val2").trim { it <= ' ' }
                    val operator = matcher.group("operator").trim { it <= ' ' }
                    val trueVal = matcher.group("trueVal").trim { it <= ' ' }
                    val falseVal = matcher.group("falseVal").trim { it <= ' ' }

                    var result = false
                    var isMatchFound = false

                    //Checking if val1 and val2 are integer
                    if (isInteger(val1) && isInteger(val2)) {

                        isMatchFound = true

                        val val1Int = Integer.parseInt(val1)
                        val val2Int = Integer.parseInt(val2)

                        result = operator == OPERATOR_EQUAL_TO && val1Int == val2Int ||
                                operator == OPERATOR_NOT_EQUAL_TO && val1Int != val2Int ||
                                operator == OPERATOR_GREATER_THAN && val1Int > val2Int ||
                                operator == OPERATOR_GREATER_THAN_OR_EQUAL_TO && val1Int >= val2Int ||
                                operator == OPERATOR_LESS_THAN && val1Int < val2Int ||
                                operator == OPERATOR_LESS_THAN_OR_EQUAL_TO && val1Int <= val2Int
                    } else if (operator == OPERATOR_EQUAL_TO || operator == OPERATOR_NOT_EQUAL_TO) {

                        isMatchFound = true

                        //String values
                        result = operator == OPERATOR_EQUAL_TO && val1 == val2 || operator == OPERATOR_NOT_EQUAL_TO && val1 != val2
                    }

                    println("------------------------------")
                    println("Val 1 : " + matcher.group("val1"))
                    println("Val 2 : " + matcher.group("val2"))
                    println("Operator : " + matcher.group("operator"))
                    println("True Val : " + matcher.group("trueVal"))
                    println("False Val : " + matcher.group("falseVal"))

                    if (isMatchFound) {
                        sb.append(arr[i++]).append(if (result) trueVal else falseVal)
                    }

                } while (matcher.find())
            }

            return if (sb.length == 0) jsonResp else sb.append(arr[arr.size - 1]).toString()
        }

        private fun isInteger(val1: String): Boolean {
            try {

                Integer.parseInt(val1)
                return true
            } catch (e: NumberFormatException) {
                return false
            }

        }
    }


}
