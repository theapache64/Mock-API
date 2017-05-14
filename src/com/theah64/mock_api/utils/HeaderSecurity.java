package com.theah64.mock_api.utils;


import com.theah64.mock_api.database.Projects;

/**
 * Created by shifar on 31/12/15.
 */
public final class HeaderSecurity {

    public static final String KEY_AUTHORIZATION = "Authorization";
    private static final String REASON_API_KEY_MISSING = "API key is missing";
    private static final String REASON_INVALID_API_KEY = "Invalid API key";
    private final String authorization;
    private String projectId;

    public HeaderSecurity(final String authorization) throws AuthorizationException {
        //Collecting header from passed request
        this.authorization = authorization;
        isAuthorized();
    }

    /**
     * Used to identify if passed API-KEY has a valid victim.
     */
    private void isAuthorized() throws AuthorizationException {

        if (this.authorization == null) {
            //No api key passed along with request
            throw new AuthorizationException("Unauthorized access");
        }

        final Projects projects = Projects.getInstance();
        this.projectId = projects.get(Projects.COLUMN_API_KEY, this.authorization, Projects.COLUMN_ID, true);
        if (this.projectId == null) {
            throw new AuthorizationException("Invalid API KEY: " + this.authorization);
        }

    }

    public String getProjectId() {
        return this.projectId;
    }

    public String getFailureReason() {
        return this.authorization == null ? REASON_API_KEY_MISSING : REASON_INVALID_API_KEY;
    }

    public class AuthorizationException extends Exception {
        public AuthorizationException(String message) {
            super(message);
        }
    }
}
