package com.theah64.mock_api.utils;

import com.theah64.mock_api.models.Project;

public class ActivityCodeGen {
    public static final String KEY_ROUTE_ID = "route_id";
    public static final String KEY_API_KEY = "api_key";

    public static ActivityCode generate(String apiKey, String routeId) {


        return null;
    }


    public class ActivityCode {
        private final String projectName, routeName;

        public ActivityCode(String projectName, String routeName) {
            this.projectName = projectName;
            this.routeName = routeName;
        }

        public String getProjectName() {
            return projectName;
        }

        public String getRouteName() {
            return routeName;
        }
    }
}
