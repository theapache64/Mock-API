package com.theah64.mock_api.utils;

import com.thedeanda.lorem.LoremIpsum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomResponseGenerator {


    //First name throw back
    static final LoremIpsum loremIpsum = LoremIpsum.getInstance();

    public static final RandomResponse[] randomResponses = new RandomResponse[]{

            //random name
            new RandomResponse("{randomName}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getName();
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


            new RandomResponse("{randomMaleName}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getNameMale();
                }
            },


            new RandomResponse("{randomFemaleName}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getNameFemale();
                }
            },


            new RandomResponse("{randomFirstName}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getFirstName();
                }
            },


            new RandomResponse("{randomLastName}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getLastName();
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
                    return loremIpsum.getWords(count);
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

        abstract String getValue(int count);
    }

    public static String generate(String jsonResp) {


        for (final RandomResponse randomResponse : randomResponses) {

            if (jsonResp.contains(randomResponse.getKey())) {

                final String splitter = randomResponse.getKey()
                        .replaceAll("\\{", "\\\\{")
                        .replaceAll("\\}", "\\\\}");

                System.out.println("SP:" + splitter);

                final String[] jsonRespArr = jsonResp.split(
                        splitter
                );

                System.out.println(jsonRespArr.length);
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jsonRespArr.length; i++) {
                    sb.append(jsonRespArr[i]).append(randomResponse.getValue(1));
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
                            sb.append(jsonRespArr[i]).append(data);

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