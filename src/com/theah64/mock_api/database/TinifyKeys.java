package com.theah64.mock_api.database;

import com.theah64.mock_api.models.TinifyKey;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TinifyKeys extends BaseTable<TinifyKey> {

    private static final TinifyKeys instance = new TinifyKeys();
    public static final String COLUMN_KEY = "_key";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USAGE = "_usage";

    private TinifyKeys() {
        super("tinify_keys");
    }

    public static TinifyKeys getInstance() {
        return instance;
    }

    @Override
    public boolean add(TinifyKey tKey) throws SQLException, QueryBuilderException {
        return new AddQueryBuilder.Builder(getTableName())
                .add(COLUMN_KEY, tKey.getKey())
                .add(COLUMN_EMAIL, tKey.getEmail())
                .add(COLUMN_USAGE, tKey.getUsage())
                .done();
    }

    public TinifyKey getLeastUsedKey() throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder<TinifyKey>(getTableName(), new SelectQueryBuilder.Callback<TinifyKey>() {
            @Override
            public TinifyKey getNode(ResultSet rs) throws SQLException {
                return new TinifyKey(
                        rs.getString(COLUMN_ID),
                        rs.getString(COLUMN_KEY),
                        rs.getString(COLUMN_EMAIL),
                        rs.getString(COLUMN_USAGE)
                );
            }
        }, new String[]{COLUMN_ID, COLUMN_KEY, COLUMN_EMAIL, COLUMN_USAGE},
                new String[]{COLUMN_IS_ACTIVE},
                new String[]{TRUE},
                1,
                COLUMN_USAGE
        ).get();
    }
}
