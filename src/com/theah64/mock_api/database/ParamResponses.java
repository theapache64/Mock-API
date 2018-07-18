package com.theah64.mock_api.database;

import com.theah64.mock_api.models.ParamResponse;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ParamResponses extends BaseTable<ParamResponse> {

    public static final String COLUMN_ROUTE_ID = "route_id";
    private static final ParamResponses instance = new ParamResponses();
    private static final String COLUMN_PARAM_ID = "param_id";
    private static final String COLUMN_PARAM_VALUE = "param_value";
    private static final String COLUMN_RESPONSE_ID = "response_id";
    private static final String COLUMN_REL_OPT = "rel_opt";

    private ParamResponses() {
        super("param_responses");
    }

    public static ParamResponses getInstance() {
        return instance;
    }

    @Override
    public List<ParamResponse> getAll(String whereColumn, String whereColumnValue) throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder<ParamResponse>(getTableName(), new SelectQueryBuilder.Callback<ParamResponse>() {
            @Override
            public ParamResponse getNode(ResultSet rs) throws SQLException {
                return new ParamResponse(
                        rs.getString("id"),
                        rs.getString("route_id"),
                        rs.getString("param_id"),
                        rs.getString("param_value"),
                        rs.getString("response_id"),
                        rs.getString("rel_opt")
                );
            }
        }, new String[]{
                COLUMN_ID,
                COLUMN_ROUTE_ID,
                COLUMN_PARAM_ID,
                COLUMN_PARAM_VALUE,
                COLUMN_RESPONSE_ID,
                COLUMN_REL_OPT
        }, new String[]{whereColumn}, new String[]{whereColumnValue}, SelectQueryBuilder.UNLIMITED, COLUMN_ID + " DESC").getAll();
    }

    /**
     * route_id,param_id,param_value, response_id, rel_opt
     *
     * @return
     * @throws SQLException
     * @throws QueryBuilderException
     */
    @Override
    public boolean add(ParamResponse p) throws SQLException, QueryBuilderException {
        return new AddQueryBuilder.Builder(getTableName())
                .add(COLUMN_ROUTE_ID, p.getRouteId())
                .add(COLUMN_PARAM_ID, p.getParamId())
                .add(COLUMN_PARAM_VALUE, p.getParamValue())
                .add(COLUMN_RESPONSE_ID, p.getResponseId())
                .add(COLUMN_REL_OPT, p.getRelOpt())
                .done();
    }
}
