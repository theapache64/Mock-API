package com.theah64.mock_api.utils;

import com.theah64.webengine.utils.RandomString;
import com.thedeanda.lorem.LoremIpsum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomResponseGenerator {

    private static final DateFormat dateWithTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


    //First name throw back
    private static final LoremIpsum loremIpsum = LoremIpsum.getInstance();

    public static LoremIpsum getLoremIpsum() {
        return loremIpsum;
    }

    public static final RandomResponse[] randomResponses = new RandomResponse[]{

            //random number
            new RandomResponse("{randomNumber (\\d+)}") {
                @Override
                String getValue(String count) {
                    return RandomString.getRandomNumber(Integer.parseInt(count));
                }
            },

            //random name
            new RandomResponse("{randomName}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getName();
                }
            },

            new RandomResponse("{randomFirstName}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getFirstName();
                }
            },


            new RandomResponse("{randomPhone}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getPhone();
                }
            },

            new RandomResponse("{randomCity}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getCity();
                }
            },

            new RandomResponse("{randomState}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getStateFull();
                }
            },


            new RandomResponse("{randomCountry}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getCountry();
                }
            },


            new RandomResponse("{randomZipCode}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getZipCode();
                }
            },

            new RandomResponse("{randomURL}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getUrl();
                }
            },

            new RandomResponse("{randomTitle (\\d+)}") {
                @Override
                String getValue(String count) {
                    return loremIpsum.getTitle(Integer.parseInt(count));
                }
            },

            new RandomResponse("{randomWords (\\d+)}") {
                @Override
                String getValue(String count) {
                    return CodeGen.getFirstCharUppercase(loremIpsum.getWords(Integer.parseInt(count)));
                }
            },

            new RandomResponse("{randomParas (\\d+)}") {
                @Override
                String getValue(String count) {
                    int intCount = Integer.parseInt(count);
                    return loremIpsum.getParagraphs(intCount, intCount);
                }
            },

            new RandomResponse("{currentTimeMillis}") {
                @Override
                String getValue(String count) {
                    return String.valueOf(System.currentTimeMillis());
                }
            },

            new RandomResponse("{currentDateTime}") {
                @Override
                String getValue(String count) {
                    return dateWithTimeFormat.format(new Date(System.currentTimeMillis()));
                }
            },

            new RandomResponse("{currentDate}") {
                @Override
                String getValue(String count) {
                    return dateFormat.format(new Date(System.currentTimeMillis()));
                }
            },

            new RandomResponse("{currentTime}") {
                @Override
                String getValue(String count) {
                    return timeFormat.format(new Date(System.currentTimeMillis()));
                }
            },

            new RandomResponse("{SimpleDateFormat (.+)}") {
                @Override
                String getValue(String param1) {
                    return new SimpleDateFormat(param1).format(new Date(System.currentTimeMillis()));
                }
            }
    };


    public abstract static class RandomResponse {
        private final String key;


        RandomResponse(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        abstract String getValue(String param1);
    }

    public static String generate(String jsonResp) {


        for (final RandomResponse randomResponse : randomResponses) {

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


                    do {

                        final String param1 = matcher.group(1);
                        String newRandomRegEx = null;
                        try {
                            final int count = Integer.parseInt(param1);
                            newRandomRegEx = randomRegEx.replace("(\\d+)", count + "");

                        } catch (NumberFormatException e) {
                            e.printStackTrace();

                            //Param is a string
                            newRandomRegEx = randomRegEx.replace("(.+)", param1);
                            System.out.println("New random regex : " + newRandomRegEx);
                        }


                        //Regex matching
                        final String[] jsonRespArr = jsonResp.split(
                                newRandomRegEx
                        );

                        final StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < jsonRespArr.length; i++) {

                            String data = randomResponse.getValue(param1);
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

        //random name


        return jsonResp;
    }


}
