package com.theah64.mock_api.database

import com.theah64.mock_api.models.Param
import com.theah64.mock_api.models.Route
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.Connection

import java.sql.SQLException
import java.util.ArrayList

/**
 * Created by theapache64 on 30/11/17.
 */
class Params private constructor() : BaseTable<Param>("params") {


    @Throws(SQLException::class)
    fun addParamsFromRoute(route: Route) {
        var error: String? = null
        val con = Connection.getConnection()

        try {
            addParams(con, route.id!!, route.params!!)
        } catch (e: SQLException) {
            e.printStackTrace()
            error = e.message
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }

        BaseTable.Companion.manageError(error)
    }

    @Throws(SQLException::class)
    fun addParams(con: java.sql.Connection, routeId: String, params: List<Param>) {
        //Move required params
        val reqInsQuery = "INSERT INTO params (name, route_id, is_required,data_type,default_value,description) VALUES (?,?,?,?,?,?);"
        val ps1 = con.prepareStatement(reqInsQuery)
        for (param in params) {
            ps1.setString(1, param.name)
            ps1.setString(2, routeId)
            ps1.setBoolean(3, param.isRequired)
            ps1.setString(4, param.dataType)
            ps1.setString(5, param.defaultValue)
            ps1.setString(6, param.description)
            ps1.executeUpdate()
        }


        ps1.close()
    }

    @Throws(SQLException::class)
    fun updateParams(con: java.sql.Connection, params: List<Param>) {
        //Move required params
        val updateQuery = "UPDATE params SET is_required = ? , data_type = ? ,  default_value = ? , description = ? WHERE route_id = ? AND name = ?"
        val ps1 = con.prepareStatement(updateQuery)
        for (param in params) {
            ps1.setBoolean(1, param.isRequired)
            ps1.setString(2, param.dataType)
            ps1.setString(3, param.defaultValue)
            ps1.setString(4, param.description)
            ps1.setString(5, param.routeId)
            ps1.setString(6, param.name)
            ps1.executeUpdate()


        }


        ps1.close()
    }

    override fun getAll(whereColumn: String, whereColumnValue: String): List<Param> {

        val con = Connection.getConnection()
        val params = ArrayList<Param>()
        val query = String.format("SELECT id, name ,route_id, is_required, data_type, default_value, description FROM params WHERE %s = ? ORDER BY is_required DESC", whereColumn)
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, whereColumnValue)
            val rs = ps.executeQuery()

            if (rs.first()) {

                do {
                    val id = rs.getString(BaseTable.Companion.COLUMN_ID)
                    val name = rs.getString(BaseTable.Companion.COLUMN_NAME)
                    val routeId = rs.getString(COLUMN_ROUTE_ID)
                    val defaultValue = rs.getString(COLUMN_DEFAULT_VALUE)
                    val dataType = rs.getString(COLUMN_DATA_TYPE)
                    val description = rs.getString(COLUMN_DESCRIPTION)
                    val isRequired = rs.getBoolean(COLUMN_IS_REQUIRED)

                    params.add(Param(id, name, routeId, dataType, defaultValue, description, isRequired))

                } while (rs.next())
            }

            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }
        return params
    }

    internal fun updateParamFromRoute(route: Route) {

        //Get new params
        val newParams = route.params

        //Get all params
        val oldParams = getAll(COLUMN_ROUTE_ID, route.id!!)
        val deletedParams = ArrayList<Param>()
        val addedParams = ArrayList<Param>()
        val updatedParams = ArrayList<Param>()

        if (oldParams.isEmpty() && !newParams!!.isEmpty()) {
            addedParams.addAll(newParams)
        } else if (newParams!!.isEmpty() && !oldParams.isEmpty()) {
            deletedParams.addAll(oldParams)
        } else {
            //finding deleted params
            for (oldParam in oldParams) {
                if (!newParams.contains(oldParam)) {
                    deletedParams.add(oldParam)
                }
            }

            //finding addedParmas
            for (newParam in newParams) {
                if (!oldParams.contains(newParam)) {
                    addedParams.add(newParam)
                } else {
                    updatedParams.add(newParam)
                }
            }
        }


        val con = Connection.getConnection()

        try {

            if (!deletedParams.isEmpty()) {

                //Delete params
                val delQuery = "DELETE FROM params WHERE route_id = ? AND name = ?;"
                val ps = con.prepareStatement(delQuery)
                for (delParam in deletedParams) {
                    ps.setString(1, route.id)
                    ps.setString(2, delParam.name)
                    ps.executeUpdate()
                }
                ps.close()
            }

            if (!addedParams.isEmpty()) {
                addParams(con, route.id!!, addedParams)
            }

            if (!updatedParams.isEmpty()) {
                updateParams(con, updatedParams)
            }


        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }


    }

    companion object {
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_ROUTE_ID = "route_id"
        val COLUMN_IS_REQUIRED = "is_required"
        val COLUMN_DEFAULT_VALUE = "default_value"
        val COLUMN_DESCRIPTION = "description"
        val COLUMN_DATA_TYPE = "data_type"
        val INSTANCE = Params()
    }


}
