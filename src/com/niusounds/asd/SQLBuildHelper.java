package com.niusounds.asd;

import java.util.Arrays;
import java.util.Date;

public class SQLBuildHelper {

	public static String getSQLType(Class<?> type) {
		if (type == int.class || type == Integer.class || type == long.class || type == Long.class || type == Date.class || type == boolean.class || type == Boolean.class || type == short.class || type == Short.class || type == byte.class || type == Byte.class) {
			return "INTEGER";
		} else if (type == float.class || type == double.class) {
			return "REAL";
		} else if (type == String.class || type.isEnum()) {
			return "TEXT";
		} else if (type == byte[].class) {
			return "BLOB";
		} else {
			return "";
		}
	}

	public static String getPrimaryKeySQL(PrimaryKey annotation) {
		StringBuilder sb = new StringBuilder(" PRIMARY KEY");

		if (annotation.conflict() != Conflict.NONE) {
			sb.append(" ON CONFLICT ").append(annotation.conflict().name());
		}

		if (annotation.autoIncrement()) {
			sb.append(" AUTOINCREMENT");
		}

		return sb.toString();
	}

	public static String getNotNullSQL(NotNull annotation) {
		StringBuilder sb = new StringBuilder(" NOT NULL");

		if (annotation.conflict() != Conflict.NONE) {
			sb.append(" ON CONFLICT ").append(annotation.conflict().name());
		}

		return sb.toString();
	}

	public static String getUniqueSQL(Unique annotation) {
		StringBuilder sb = new StringBuilder(" UNIQUE");

		if (annotation.conflict() != Conflict.NONE) {
			sb.append(" ON CONFLICT ").append(annotation.conflict().name());
		}

		return sb.toString();
	}

	public static String getCheckSQL(Check annotation) {
		return " CHECK (" + annotation.expr() + ")";
	}

	public static String getDefaultSQL(Default annotation) {
		return " DEFAULT " + annotation.value();
	}

	public static String getCollateSQL(Collate annotation) {
		return " COLLATE " + annotation.value();
	}

	public static String getTablePrimaryKeySQL(TablePrimaryKey annotation) {
		String indexedColumn = Arrays.toString(annotation.indexedColumn());
		StringBuilder sb = new StringBuilder(" PRIMARY KEY (").append(indexedColumn.substring(1, indexedColumn.length() - 1)).append(")");

		if (annotation.conflict() != Conflict.NONE) {
			sb.append(" ON CONFLICT ").append(annotation.conflict().name());
		}

		return sb.toString();
	}

	public static String getTableUniqueSQL(TableUnique annotation) {
		String indexedColumn = Arrays.toString(annotation.indexedColumn());
		StringBuilder sb = new StringBuilder(" UNIQUE (").append(indexedColumn.substring(1, indexedColumn.length() - 1)).append(")");

		if (annotation.conflict() != Conflict.NONE) {
			sb.append(" ON CONFLICT ").append(annotation.conflict().name());
		}

		return sb.toString();
	}
}
