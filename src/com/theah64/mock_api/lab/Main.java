package com.theah64.mock_api.lab;

import com.theah64.mock_api.models.ParamResponse;
import org.json.JSONException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 4/1/18.
 */
public class Main {

    public static void main(String[] args) throws IOException, JSONException {

        String jsonResp = "Here's some text {10 == 10 ? trueVallGoesHere : falseValGoesHere} and some other text {9 > 2 ? trueVallGoesHere1 : falseValGoesHere1} and few other text";
        jsonResp = ConditionedResponse.generate(jsonResp);
        System.out.println("-------------------");
        System.out.println(jsonResp);
    }

    static class ConditionedResponse {

        private static final String CONDITIONED_PATTERN = "\\{(?<val1>[^=!><]+)\\s*(?<operator>==|!=|>|<|>=|<=)\\s*(?<val2>[^?]+)\\s*\\?\\s*(?<trueVal>[^:]+)\\s*:\\s*(?<falseVal>[^}]+)\\}";

        private static final String OPERATOR_EQUAL_TO = "==";
        private static final String OPERATOR_NOT_EQUAL_TO = "!=";
        private static final String OPERATOR_GREATER_THAN = ">";
        private static final String OPERATOR_GREATER_THAN_OR_EQUAL_TO = ">=";
        private static final String OPERATOR_LESS_THAN = "<";
        private static final String OPERATOR_LESS_THAN_OR_EQUAL_TO = "<=";

        static String generate(String jsonResp) {

            //Checking if conditioned response
            final Pattern pattern = Pattern.compile(CONDITIONED_PATTERN, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(jsonResp);


            StringBuffer sb = new StringBuffer();

            if (matcher.find()) {
                int i = 0;
                do {


                    //{10>3 ? young : old}

                    final String val1 = matcher.group("val1").trim();
                    final String val2 = matcher.group("val2").trim();
                    final String operator = matcher.group("operator").trim();
                    final String trueVal = matcher.group("trueVal").trim();
                    final String falseVal = matcher.group("falseVal").trim();

                    boolean result = false;
                    boolean isMatchFound = false;

                    //Checking if val1 and val2 are integer
                    if (isInteger(val1) && isInteger(val2)) {

                        isMatchFound = true;

                        final int val1Int = Integer.parseInt(val1);
                        final int val2Int = Integer.parseInt(val2);

                        result = (operator.equals(OPERATOR_EQUAL_TO) && val1Int == val2Int) ||
                                (operator.equals(OPERATOR_NOT_EQUAL_TO) && val1Int != val2Int) ||
                                (operator.equals(OPERATOR_GREATER_THAN) && val1Int > val2Int) ||
                                (operator.equals(OPERATOR_GREATER_THAN_OR_EQUAL_TO) && val1Int >= val2Int) ||
                                (operator.equals(OPERATOR_LESS_THAN) && val1Int < val2Int) ||
                                (operator.equals(OPERATOR_LESS_THAN_OR_EQUAL_TO) && val1Int <= val2Int);
                    } else if (operator.equals(OPERATOR_EQUAL_TO) || operator.equals(OPERATOR_NOT_EQUAL_TO)) {

                        isMatchFound = true;

                        //String values
                        result = (operator.equals(OPERATOR_EQUAL_TO) && val1.equals(val2)) ||
                                (operator.equals(OPERATOR_NOT_EQUAL_TO) && !val1.equals(val2));
                    }

                    System.out.println("------------------------------");
                    System.out.println("Val 1 : " + matcher.group("val1"));
                    System.out.println("Val 2 : " + matcher.group("val2"));
                    System.out.println("Operator : " + matcher.group("operator"));
                    System.out.println("True Val : " + matcher.group("trueVal"));
                    System.out.println("False Val : " + matcher.group("falseVal"));

                    if (isMatchFound) {
                        matcher.appendReplacement(sb, result ? trueVal : falseVal);
                    }

                } while (matcher.find());
            }

            return sb.length() == 0 ? jsonResp : sb.toString();
        }

        private static boolean isInteger(String val1) {
            try {
                //noinspection ResultOfMethodCallIgnored
                Integer.parseInt(val1);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }


}
