package com.sota.clone.copyopgg.database

import java.sql.ResultSet

class RepositoryUtils {

    companion object {
        fun columnExistsInResultSet(rs: ResultSet, columnLabel: String): Boolean {
            for (i: Int in 1..rs.metaData.columnCount) {
                val columnName = rs.metaData.getColumnName(i)
                if (columnLabel == columnName) return true
            }

            return false
        }
    }

}