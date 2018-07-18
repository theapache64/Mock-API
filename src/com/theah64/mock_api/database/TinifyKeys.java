package com.theah64.mock_api.database;

import com.theah64.mock_api.models.TinifyKey;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class TinifyKeys extends BaseTable<TinifyKey> {

    public static final String COLUMN_KEY = "_key";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USAGE = "_usage";
    private static final TinifyKeys instance = new TinifyKeys();

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
        return new SelectQueryBuilder<TinifyKey>(getTableName(), rs -> new TinifyKey(
                rs.getString(COLUMN_ID),
                rs.getString(COLUMN_KEY),
                rs.getString(COLUMN_EMAIL),
                rs.getString(COLUMN_USAGE)
        ), new String[]{COLUMN_ID, COLUMN_KEY, COLUMN_EMAIL, COLUMN_USAGE},
                new String[]{COLUMN_IS_ACTIVE},
                new String[]{TRUE},
                "1",
                COLUMN_USAGE
        ).get();
    }

    @Override
    public List<TinifyKey> getAll() throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder.Builder<TinifyKey>(getTableName(), rs -> new TinifyKey(
                rs.getString(COLUMN_ID),
                rs.getString(COLUMN_KEY),
                rs.getString(COLUMN_EMAIL),
                rs.getString(COLUMN_USAGE)
        )).select(new String[]{COLUMN_ID, COLUMN_KEY, COLUMN_EMAIL, COLUMN_USAGE})
                .orderBy(COLUMN_USAGE)
                .build()
                .getAll();

    }
}
