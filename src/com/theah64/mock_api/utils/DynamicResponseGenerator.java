package com.theah64.mock_api.utils;

import com.theah64.webengine.utils.RandomString;
import com.thedeanda.lorem.LoremIpsum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicResponseGenerator {

    private static final DateFormat dateWithTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


    //First name throw back
    private static final LoremIpsum loremIpsum = LoremIpsum.getInstance();

    public static LoremIpsum getLoremIpsum() {
        return loremIpsum;
    }

    public static final DynamicResponse[] randomResponses = new DynamicResponse[]{

            //random number
            new DynamicResponse("{randomNumber (\\d+)}") {
                @Override
                String getValue(String count) {
                    return RandomString.getRandomNumber(Integer.parseInt(count));
                }
            },

            //random name
            new DynamicResponse("{randomName}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getName();
                }
            },

            new DynamicResponse("{randomFirstName}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getFirstName();
                }
            },


            new DynamicResponse("{randomPhone}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getPhone();
                }
            },

            new DynamicResponse("{randomCity}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getCity();
                }
            },

            new DynamicResponse("{randomState}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getStateFull();
                }
            },


            new DynamicResponse("{randomCountry}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getCountry();
                }
            },


            new DynamicResponse("{randomZipCode}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getZipCode();
                }
            },

            new DynamicResponse("{randomURL}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getUrl();
                }
            },

            new DynamicResponse("{randomTitle (\\d+)}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getTitle(Integer.parseInt(count));
                }
            },

            new DynamicResponse("{randomWords (\\d+)}") {
                @Override
                String getValue(String count) {
                    return CodeGen.getFirstCharUppercase(loremIpsum.getWords(Integer.parseInt(count)));
                }
            },

            new DynamicResponse("{randomParas (\\d+)}") {
                @Override
                String getValue(String count) {
                    int intCount = Integer.parseInt(count);
                    return loremIpsum.getParagraphs(intCount, intCount);
                }
            },

            new DynamicResponse("{currentTimeMillis}") {
                @Override
                String getValue(String count) {
                    return String.valueOf(System.currentTimeMillis());
                }
            },

            new DynamicResponse("{currentDateTime}") {
                @Override
                String getValue(String count) {
                    return dateWithTimeFormat.format(new Date(System.currentTimeMillis()));
                }
            },

            new DynamicResponse("{currentDate}") {
                @Override
                String getValue(String count) {
                    return dateFormat.format(new Date(System.currentTimeMillis()));
                }
            },

            new DynamicResponse("{currentTime}") {
                @Override
                String getValue(String count) {
                    return timeFormat.format(new Date(System.currentTimeMillis()));
                }
            },

            new DynamicResponse("{SimpleDateFormat (.+)}") {
                @Override
                String getValue(String params) {
                    return new SimpleDateFormat(params).format(new Date(System.currentTimeMillis()));
                }
            },

            /*new DynamicResponse("{(.+)\\s*(==|===|!=|>|<|>=|<=)\\s*(.+)\\s*\\?\\s*(.+)\\:\\s*(.+)}") {
                @Override
                String getValue(String[] params) {

                    String val1 = params[0];
                    String operator = params[1];
                    String val2 = params[2];
                    String ifTrue = params[3];
                    String ifFalse = params[4];


                    return "matched!";
                }
            }*/


    };


    public abstract static class DynamicResponse {
        private final String key;


        DynamicResponse(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        abstract String getValue(String param);
    }

    public static String generate(String jsonResp) {


        for (final DynamicResponse randomResponse : randomResponses) {


            if (jsonResp.contains(randomResponse.getKey())) {


                //No param random response

                final String splitter = randomResponse.getKey()
                        .replaceAll("\\{", "\\\\{")
                        .replaceAll("\\}", "\\\\}");


                final String[] jsonRespArr = jsonResp.split(
                        splitter
                );


                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jsonRespArr.length; i++) {
                    final String aJsonRespArr = jsonRespArr[i];
                    final String data = randomResponse.getValue("1");
                    sb.append(aJsonRespArr);

                    if (i < (jsonRespArr.length - 1)) {
                        sb.append(data);
                    }
                }

                jsonResp = sb.toString();
            } else {

                //Param response
                String randomRegEx = randomResponse.getKey();
                randomRegEx = randomRegEx.replace("{", "\\{");
                randomRegEx = randomRegEx.replace("}", "\\}");

                final Pattern pattern = Pattern.compile(randomRegEx);
                final Matcher matcher = pattern.matcher(jsonResp);

                if (matcher.find()) {


                    if (matcher.groupCount() == 1) {
                        do {

                            final String param = matcher.group(1);
                            String newRandomRegEx = null;
                            try {
                                final int count = Integer.parseInt(param);
                                newRandomRegEx = randomRegEx.replace("(\\d+)", count + "");

                            } catch (NumberFormatException e) {
                                e.printStackTrace();

                                //Param is a string
                                newRandomRegEx = randomRegEx.replace("(.+)", param);
                                System.out.println("New random regex : " + newRandomRegEx);
                            }


                            //Regex matching
                            final String[] jsonRespArr = jsonResp.split(
                                    newRandomRegEx
                            );

                            final StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < jsonRespArr.length; i++) {

                                String data = randomResponse.getValue(param);
                                data = data.replace("\n", "\\n");
                                sb.append(jsonRespArr[i]);

                                if (i < (jsonRespArr.length - 1)) {
                                    sb.append(data);
                                }

                            }

                            jsonResp = sb.toString();

                        } while (matcher.find());
                    }

                }
            }


        }

        //random name


        return jsonResp;
    }


}
