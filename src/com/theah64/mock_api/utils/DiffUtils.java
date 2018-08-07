package com.theah64.mock_api.utils;

import com.theah64.mock_api.models.DiffView;
import com.theah64.mock_api.models.RouteUpdate;

import java.util.ArrayList;
import java.util.List;

public class DiffUtils {
    public static List<DiffView> getDiffViews(RouteUpdate oldRouteUpdate, RouteUpdate newRouteUpdate) {
        final List<DiffView> diffViews = new ArrayList<>();

        if (oldRouteUpdate != null && newRouteUpdate != null) {
            //Method
            if (!oldRouteUpdate.getMethod().equals(newRouteUpdate.getMethod())) {

                //Change in method
                diffViews.add(new DiffView(
                        "Method", "oldMethod",
                        "newMethod",
                        "methodDiffOutput",
                        newRouteUpdate.getMethod(),
                        oldRouteUpdate.getMethod()
                ));
            }


            //Params
            if (!oldRouteUpdate.getParams().equals(newRouteUpdate.getParams())) {

                diffViews.add(new DiffView(
                        "Parameters", "oldParams",
                        "newParams",
                        "paramsDiffOutput",
                        newRouteUpdate.getParams(),
                        oldRouteUpdate.getParams()
                ));

            }


            //Delay
            if (!oldRouteUpdate.getDelay().equals(newRouteUpdate.getDelay())) {

                diffViews.add(new DiffView(
                        "Delay", "oldDelay",
                        "newDelay",
                        "delayDiffOutput",
                        newRouteUpdate.getDelay(),
                        oldRouteUpdate.getDelay()
                ));

            }

            //Descriptions
            if (!oldRouteUpdate.getDescription().equals(newRouteUpdate.getDescription())) {

                diffViews.add(new DiffView(
                        "Description", "oldDescription",
                        "newDescription",
                        "descriptionDiffOutput",
                        newRouteUpdate.getDescription(),
                        oldRouteUpdate.getDescription()
                ));

            }


            //Default response
            if (!oldRouteUpdate.getDefaultResponse().equals(newRouteUpdate.getDefaultResponse())) {
                //Change in default response
                diffViews.add(new DiffView(
                        "Default response", "oldDefaultResponse",
                        "newDefaultResponse",
                        "defaultResponseDiffOutput",
                        newRouteUpdate.getDefaultResponse(),
                        oldRouteUpdate.getDefaultResponse()
                ));
            }

        }


        return diffViews;
    }
}
