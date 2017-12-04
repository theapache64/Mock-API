package com.theah64.mock_api.utils;

import com.thedeanda.lorem.LoremIpsum;

public class RandomResponseGenerator {


    //First name throw back
    static final LoremIpsum loremIpsum = LoremIpsum.getInstance();

    private static final RandomResponse[] randomResponses = new RandomResponse[]{

            //random name
            new RandomResponse("{randomName}") {
                @Override
                String getValue(int count) {
                    return loremIpsum.getName();
                }
            },


    };

    abstract static class RandomResponse {
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
                    sb.append(jsonRespArr[i]).append(randomResponse.getValue(0));
                }
                jsonResp = sb.toString();
            }
        }

        //random name


        return jsonResp;
    }


}
