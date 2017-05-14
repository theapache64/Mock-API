package com.theah64.mock_api.database.tables;

import com.theah64.mock_api.models.Project;

/**
 * Created by theapache64 on 14/5/17.
 */
public class Projects extends BaseTable<Project> {

    private static final Projects instance = new Projects();

    private Projects() {
        super("projects");
    }

    public static Projects getInstance() {
        return instance;
    }
}
