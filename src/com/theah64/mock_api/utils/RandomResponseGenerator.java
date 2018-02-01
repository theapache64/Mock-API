package com.theah64.mock_api.utils;

import com.theah64.webengine.utils.RandomString;
import com.thedeanda.lorem.LoremIpsum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomResponseGenerator {


    //First name throw back
    private static final LoremIpsum loremIpsum = LoremIpsum.getInstance();

    public static LoremIpsum getLoremIpsum() {
        return loremIpsum;
    }

    public static final RandomResponse[] randomResponses = new RandomResponse[]{


            //random number
            new RandomResponse("{randomNumber (\\d+)}") {
                @Override
                String getValue(int count) {
                    return RandomString.getRandomNumber(count);
                }
            },

            //random name
            new RandomResponse("{randomName}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getName();
                }
            },

            new RandomResponse("{randomFirstName}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getFirstName();
                }
            },


            new RandomResponse("{randomPhone}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getPhone();
                }
            },

            new RandomResponse("{randomCity}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getCity();
                }
            },

            new RandomResponse("{randomState}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getStateFull();
                }
            },


            new RandomResponse("{randomCountry}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getCountry();
                }
            },


            new RandomResponse("{randomZipCode}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getZipCode();
                }
            },

            new RandomResponse("{randomURL}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getUrl();
                }
            },

            new RandomResponse("{randomTitle (\\d+)}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getTitle(count);
                }
            },

            new RandomResponse("{randomWords (\\d+)}") {
                @Override
                String getValue(int count) {
                    return CodeGen.getFirstCharUppercase(loremIpsum.getWords(count));
                }
            },

            new RandomResponse("{randomParas (\\d+)}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getParagraphs(count, count);
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

        abstract String getValue(int param1);
    }

    public static String generate(String jsonResp) {


        for (final RandomResponse randomResponse : randomResponses) {

            if (jsonResp.contains(randomResponse.getKey())) {

                final String splitter = randomResponse.getKey()
                        .replaceAll("\\{", "\\\\{")
                        .replaceAll("\\}", "\\\\}");



                final String[] jsonRespArr = jsonResp.split(
                        splitter
                );


                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jsonRespArr.length; i++) {
                    final String aJsonRespArr = jsonRespArr[i];
                    final String data = randomResponse.getValue(1);
                    sb.append(aJsonRespArr);

                    if (i < (jsonRespArr.length - 1)) {
                        sb.append(data);
                    }
                }

                jsonResp = sb.toString();
            } else {

                String randomRegEx = randomResponse.getKey();
                randomRegEx = randomRegEx.replace("{", "\\{");
                randomRegEx = randomRegEx.replace("}", "\\}");

                final Pattern pattern = Pattern.compile(randomRegEx);
                final Matcher matcher = pattern.matcher(jsonResp);

                if (matcher.find()) {

                    do {
                        final int count = Integer.parseInt(matcher.group(1));
                        String newRandomRegEx = randomRegEx.replace("(\\d+)", count + "");

                        //Regex matching
                        final String[] jsonRespArr = jsonResp.split(
                                newRandomRegEx
                        );

                        final StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < jsonRespArr.length; i++) {

                            String data = randomResponse.getValue(count);
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
