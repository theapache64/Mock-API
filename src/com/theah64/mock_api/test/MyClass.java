package com.theah64.mock_api.test;

public class MyClass {

    public static void main(String[] args) {

       /* final List<Param> newParams = new ArrayList<>();
        newParams.add(new Param(null, "A", null, null, null, null, false));
        newParams.add(new Param(null, "B", null, null, null, null, false));
        newParams.add(new Param(null, "C", null, null, null, null, false));
        newParams.add(new Param(null, "F", null, null, null, null, false));

        final List<Param> oldParams = new ArrayList<>();
        oldParams.add(new Param(null, "B", null, null, null, null, false));
        oldParams.add(new Param(null, "D", null, null, null, null, false));
        oldParams.add(new Param(null, "F", null, null, null, null, false));
        oldParams.add(new Param(null, "E", null, null, null, null, false));

        //deletedParams should be D,E
        //addedParams should be A,C

        final List<Param> deletedParams = new ArrayList<>();
        final List<Param> addedParams = new ArrayList<>();
        final List<Param> updatedParams = new ArrayList<>();


        if (oldParams.isEmpty() && !newParams.isEmpty()) {
            addedParams.addAll(newParams);
        } else if (newParams.isEmpty() && !oldParams.isEmpty()) {
            deletedParams.addAll(oldParams);
        } else {
            //finding deleted params
            for (Param oldParam : oldParams) {
                if (!newParams.contains(oldParam)) {
                    deletedParams.add(oldParam);
                }
            }

            //finding addedParmas
            for (final Param newParam : newParams) {
                if (!oldParams.contains(newParam)) {
                    addedParams.add(newParam);
                } else {
                    updatedParams.add(newParam);
                }
            }

        }


        System.out.println("Deleted params: " + deletedParams);
        System.out.println("Added params: " + addedParams);
        System.out.println("Updated params: " + updatedParams);*/
    }


}
