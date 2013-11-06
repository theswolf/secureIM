package com.niusounds.asd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * First call method is SQLiteDAO.getInstance().<br/>
 * Arguments are same as SQLiteOpenHelper and Persistent classes.<br/>
 * Please read official document of SQLiteOpenHelper.
 *
 * @author Yuya Matsuo
 *
 */
public class SQLiteDAO extends SQLiteOpenHelper {
	private static SQLiteDAO instance; // Singleton instance
	private Class<?>[]       classes; // persistent classes

	public static interface Transaction {

		/**
		 * Define transaction. return true if committed, return false if rollbacked.
		 *
		 * @param dao
		 *            reference of SQLiteDAO
		 * @return true:commit,false rollback
		 */
		public boolean execute(SQLiteDAO dao);
	}

	/**
	 * Create ContentValues object for INSERT or UPDATE
	 *
	 * @param o
	 * @return ContentValues
	 */
	private static ContentValues createContentValues(Object o) {
		try {
			ContentValues values = new ContentValues();
			for (Field f : getPersistenceFields(o.getClass())) {
				// AUTOINCREMENTフィールドは対象外
				if (f.isAnnotationPresent(PrimaryKey.class) && f.getAnnotation(PrimaryKey.class).autoIncrement()) continue;

				// Defaultフィールドがnullなら対象外
				if (f.isAnnotationPresent(Default.class) && f.get(o) == null) continue;

				Class<?> type = f.getType();
				String name = f.getName();

				if (type == int.class) {
					values.put(name, f.getInt(o));
				} else if (type == long.class) {
					values.put(name, f.getLong(o));
				} else if (type == float.class) {
					values.put(name, f.getFloat(o));
				} else if (type == double.class) {
					values.put(name, f.getDouble(o));
				} else if (type == String.class) {
					values.put(name, (String) f.get(o));
				} else if (type == byte[].class) {
					values.put(name, (byte[]) f.get(o));
				} else if (type == boolean.class) {
					values.put(name, f.getBoolean(o));
				} else if (type == short.class) {
					values.put(name, f.getShort(o));
				} else if (type == Date.class) {
					Date d = (Date) f.get(o);
					values.put(name, d != null ? d.getTime() : null);
				} else if (type.isEnum()) {
					Enum<?> e = (Enum<?>) f.get(o);
					values.put(name, e != null ? e.name() : null);
				}
			}
			return values;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * get default database name = context's class simple name.
	 *
	 * @param context
	 * @return
	 */
	private static String getDefaultDatabaseName(Context context) {
		return context.getClass().getSimpleName();
	}

	/**
	 * Get [Persitent] Annotated fields from classs.
	 *
	 * @param clz
	 * @return
	 */
	private static Set<Field> getPersistenceFields(Class<?> clz) {
		Set<Field> fields = new HashSet<Field>();
		for (Field f : clz.getDeclaredFields()) {
			if (f.isAnnotationPresent(Persistent.class)) {
				f.setAccessible(true);
				fields.add(f);
			}
		}
		return fields;
	}

	/**
	 * Get table name from class.
	 *
	 * @param clz
	 * @return table name
	 */
	public static String getTableName(Class<?> clz) {
		if (clz.isAnnotationPresent(TableName.class)) {
			return clz.getAnnotation(TableName.class).value();
		} else {
			return clz.getSimpleName();
		}
	}

	/**
	 * Get singleton instance with default database name, CursorFactory, and version.
	 *
	 * @param context
	 * @param classes
	 * @return singleton instance
	 */
	public static SQLiteDAO getInstance(Context context, Class<?>... classes) {
		return getInstance(context, getDefaultDatabaseName(context), null, 1, classes);
	}

	/**
	 * Get singleton instance with specified version and default database name and CursorFactory.
	 *
	 * @param context
	 * @param version
	 * @param classes
	 * @return singleton instance
	 */
	public static SQLiteDAO getInstance(Context context, int version, Class<?>... classes) {
		return getInstance(context, getDefaultDatabaseName(context), null, version, classes);
	}

	/**
	 * Get singleton instance with specified database name and default version and CursorFactory.
	 *
	 * @param context
	 * @param name
	 * @param classes
	 * @return singleton instance
	 */
	public static SQLiteDAO getInstance(Context context, String name, Class<?>... classes) {
		return getInstance(context, name, null, 1, classes);
	}

	/**
	 * Get singleton instance with specified database name and version and default CursorFactory.
	 *
	 * @param context
	 * @param name
	 * @param version
	 * @param classes
	 * @return singleton instance
	 */
	public static SQLiteDAO getInstance(Context context, String name, int version, Class<?>... classes) {
		return getInstance(context, name, null, version, classes);
	}

	/**
	 * Get singleton instance with specified database name, CursorFactory and version.
	 *
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @param classes
	 * @return singleton instance
	 */
	public static SQLiteDAO getInstance(Context context, String name, CursorFactory factory, int version, Class<?>... classes) {
		if (instance == null) {
			instance = new SQLiteDAO(context, name, factory, version, classes);
		}
		return instance;
	}

	/**
	 * private constructor
	 *
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @param classes
	 */
	private SQLiteDAO(Context context, String name, CursorFactory factory, int version, Class<?>... classes) {
		super(context, name, factory, version);
		this.classes = classes;
	}

	/**
	 * Create table for persistent class.
	 *
	 * @param db
	 * @param clz
	 */
	private void createTable(SQLiteDatabase db, Class<?> clz) {
		String tableName = getTableName(clz);
		StringBuilder columnDefs = new StringBuilder();

		// 永続化対象フィールドからカラム定義SQLを生成
		for (Field field : getPersistenceFields(clz)) {
			columnDefs.append(",").append(field.getName());

			Class<?> type = field.getType();

			// フィールドの型に対応するSQL型をカラム名の後ろに付ける
			columnDefs.append(" ").append(SQLBuildHelper.getSQLType(type));

			// 主キー
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				PrimaryKey annotation = field.getAnnotation(PrimaryKey.class);
				columnDefs.append(SQLBuildHelper.getPrimaryKeySQL(annotation));
			}

			// NOT NULL
			if (field.isAnnotationPresent(NotNull.class)) {
				NotNull annotation = field.getAnnotation(NotNull.class);
				columnDefs.append(SQLBuildHelper.getNotNullSQL(annotation));
			}

			// UNIQUE
			if (field.isAnnotationPresent(Unique.class)) {
				Unique annotation = field.getAnnotation(Unique.class);
				columnDefs.append(SQLBuildHelper.getUniqueSQL(annotation));
			}

			// CHECK
			if (field.isAnnotationPresent(Check.class)) {
				Check annotation = field.getAnnotation(Check.class);
				columnDefs.append(SQLBuildHelper.getCheckSQL(annotation));
			}

			// DEFAULT
			if (field.isAnnotationPresent(Default.class)) {
				Default annotation = field.getAnnotation(Default.class);
				columnDefs.append(SQLBuildHelper.getDefaultSQL(annotation));
			}

			// COLLATE
			if (field.isAnnotationPresent(Collate.class)) {
				Collate annotation = field.getAnnotation(Collate.class);
				columnDefs.append(SQLBuildHelper.getCollateSQL(annotation));
			}
		}

		// テーブル制約
		StringBuilder tableConstraints = new StringBuilder();

		// 主キー
		if (clz.isAnnotationPresent(TablePrimaryKey.class)) {
			TablePrimaryKey annotation = clz.getAnnotation(TablePrimaryKey.class);
			tableConstraints.append(",").append(SQLBuildHelper.getTablePrimaryKeySQL(annotation));
		}

		// UNIQUE
		if (clz.isAnnotationPresent(TableUnique.class)) {
			TableUnique annotation = clz.getAnnotation(TableUnique.class);
			tableConstraints.append(",").append(SQLBuildHelper.getTableUniqueSQL(annotation));
		}

		// CREATE TABLE query
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName);
		sb.append(" (");
		sb.append(columnDefs.substring(1));
		sb.append(tableConstraints.toString());
		sb.append(" )");
		String sql = sb.toString();
		Logger.log(sql);
		db.execSQL(sql);
	}

	/**
	 * Delete records with specified where clause.
	 *
	 * @param clz
	 * @param whereClause
	 * @param whereArgs
	 * @return affected row count
	 */
	public int delete(Class<?> clz, String whereClause, String... whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		return delete(db, clz, whereClause, whereArgs);
	}

	/**
	 * Delete records with specified where clause.
	 *
	 * @param db
	 * @param clz
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	private int delete(SQLiteDatabase db, Class<?> clz, String whereClause, String... whereArgs) {
		return db.delete(getTableName(clz), whereClause, whereArgs);
	}

	/**
	 * Drop table.
	 *
	 * @param db
	 * @param clz
	 */
	private void dropTable(SQLiteDatabase db, Class<?> clz) {
		String tableName = getTableName(clz);
		db.execSQL("DROP TABLE IF EXISTS " + tableName);
	}

	/**
	 * Get all records.
	 *
	 * @param clz
	 * @return
	 */
	public <T> List<T> get(Class<T> clz) {
		return get(clz, null, null, null, null, null, -1);
	}

	/**
	 * Get all records until limit.
	 *
	 * @param clz
	 * @param limit
	 * @return
	 */
	public <T> List<T> get(Class<T> clz, int limit) {
		return get(clz, null, null, null, null, null, limit);
	}

	/**
	 * Get records with WHERE clause until limit.
	 *
	 * @param clz
	 * @param limit
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public <T> List<T> get(Class<T> clz, int limit, String selection, String... selectionArgs) {
		return get(clz, selection, selectionArgs, null, null, null, limit);
	}

	/**
	 * Get records with WHERE clause.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public <T> List<T> get(Class<T> clz, String selection, String... selectionArgs) {
		return get(clz, selection, selectionArgs, null, null, null, -1);
	}

	/**
	 * Get records with WHERE clause until limit.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param limit
	 * @return
	 */
	public <T> List<T> get(Class<T> clz, String selection, String[] selectionArgs, int limit) {
		return get(clz, selection, selectionArgs, null, null, null, limit);
	}

	/**
	 * Get records with WHERE clause and ORDER BY.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @return
	 */
	public <T> List<T> get(Class<T> clz, String selection, String[] selectionArgs, String orderBy) {
		return get(clz, selection, selectionArgs, null, null, orderBy, -1);
	}

	/**
	 * Get records with WHERE clause and ORDER BY until limit.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public <T> List<T> get(Class<T> clz, String selection, String[] selectionArgs, String orderBy, int limit) {
		return get(clz, selection, selectionArgs, null, null, orderBy, limit);
	}

	/**
	 * Get records with WHERE clause, GROUP BY, HAVING and ORDER BY clauses.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public <T> List<T> get(Class<T> clz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return get(clz, selection, selectionArgs, groupBy, having, orderBy, -1);
	}

	/**
	 * Get records with WHERE clause, GROUP BY, HAVING and ORDER BY clauses until limit.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public <T> List<T> get(Class<T> clz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, int limit) {
		SQLiteDatabase db = getWritableDatabase();
		return get(db, clz, null, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * Get records with WHERE clause, GROUP BY, HAVING and ORDER BY clauses until limit.
	 *
	 * @param db
	 * @param clz
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> List<T> get(SQLiteDatabase db, Class<T> clz, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, int limit) {
		Cursor c = query(db, clz, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		List<T> result = new ArrayList<T>();
		try {
			while (c.moveToNext()) {
				T o = clz.newInstance();
				for (Field f : getPersistenceFields(clz)) {

					// フィールド名がCursorに含まれていて、NULLでない場合は、フィールドに値をセット
					int idx = c.getColumnIndex(f.getName());
					if (idx > -1) {
						Class<?> type = f.getType();
						if (!c.isNull(idx)) {
							if (type == int.class) {
								f.set(o, c.getInt(idx));
							} else if (type == long.class) {
								f.set(o, c.getLong(idx));
							} else if (type == float.class) {
								f.set(o, c.getFloat(idx));
							} else if (type == double.class) {
								f.set(o, c.getDouble(idx));
							} else if (type == String.class) {
								f.set(o, c.getString(idx));
							} else if (type == byte[].class) {
								f.set(o, c.getBlob(idx));
							} else if (type == boolean.class) {
								f.set(o, c.getInt(idx) == 1);
							} else if (type == short.class) {
								f.set(o, c.getShort(idx));
							} else if (type == Date.class) {
								f.set(o, new Date(c.getLong(idx)));
							} else if (type.isEnum()) {
								f.set(o, Enum.valueOf((Class<? extends Enum>) type, c.getString(idx)));
							}
						} else {
							// NULL相当の値を設定する
							if (type == int.class || type == long.class || type == float.class || type == double.class || type == short.class) {
								f.set(o, 0);
							} else if (!type.isPrimitive()) {
								f.set(o, null);
							}
						}
					}
				}
				result.add(o);
			}
			return result;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return null;
	}

	/**
	 * INSERT all objects.
	 *
	 * @param list
	 * @return
	 */
	public List<Long> insertAll(Iterable<?> list) {
		SQLiteDatabase db = getWritableDatabase();
		List<Long> result = new ArrayList<Long>();
		for (Object o : list) {
			result.add(insert(db, o));
		}
		return result;
	}

	/**
	 * INSERT all objects.
	 *
	 * @param list
	 * @param returnResult
	 *            true:return all INSERT results with List<Long>, false: return null
	 * @return
	 */
	public List<Long> insertAll(Iterable<?> list, boolean returnResult) {
		if (returnResult) {
			return insertAll(list);
		} else {
			SQLiteDatabase db = getWritableDatabase();
			for (Object o : list) {
				insert(db, o);
			}
			return null;
		}
	}

	/**
	 * INSERT object.
	 *
	 * @param o
	 * @return auto generated ID
	 */
	public long insert(Object o) {
		SQLiteDatabase db = getWritableDatabase();
		return insert(db, o);
	}

	/**
	 * INSERT object.
	 *
	 * @param db
	 * @param o
	 * @return auto generated ID
	 */
	private long insert(SQLiteDatabase db, Object o) {
		ContentValues values = createContentValues(o);
		if (values != null) {
			return db.insert(getTableName(o.getClass()), null, values);
		} else {
			return -1;
		}
	}

	/**
	 * Create tables.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		if (classes != null) {
			for (Class<?> c : classes) {
				createTable(db, c);
			}
		}
	}

	/**
	 * Drop all tables and recreate tables.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO future task. I want to implement auto scheme update.
		if (classes != null) {
			for (Class<?> c : classes) {
				dropTable(db, c);
				createTable(db, c);
			}
		}
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @return
	 */
	public Cursor query(Class<?> clz) {
		return query(clz, null, null, null, null, null, null, -1);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param limit
	 * @return
	 */
	public Cursor query(Class<?> clz, int limit) {
		return query(clz, null, null, null, null, null, null, limit);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param limit
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public Cursor query(Class<?> clz, int limit, String selection, String... selectionArgs) {
		return query(clz, null, selection, selectionArgs, null, null, null, limit);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public Cursor query(Class<?> clz, String selection, String... selectionArgs) {
		return query(clz, null, selection, selectionArgs, null, null, null, -1);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param limit
	 * @return
	 */
	public Cursor query(Class<?> clz, String selection, String[] selectionArgs, int limit) {
		return query(clz, null, selection, selectionArgs, null, null, null, limit);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @return
	 */
	public Cursor query(Class<?> clz, String selection, String[] selectionArgs, String orderBy) {
		return query(clz, null, selection, selectionArgs, null, null, orderBy, -1);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public Cursor query(Class<?> clz, String selection, String[] selectionArgs, String orderBy, int limit) {
		return query(clz, null, selection, selectionArgs, null, null, orderBy, limit);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor query(Class<?> clz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return query(clz, null, selection, selectionArgs, groupBy, having, orderBy, -1);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public Cursor query(Class<?> clz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, int limit) {
		return query(clz, null, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor query(Class<?> clz, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return query(clz, columns, selection, selectionArgs, groupBy, having, orderBy, -1);
	}

	/**
	 * Get cursor.
	 *
	 * @param clz
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public Cursor query(Class<?> clz, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, int limit) {
		SQLiteDatabase db = getWritableDatabase();
		return query(db, clz, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * Get cursor.
	 *
	 * @param db
	 * @param clz
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	private Cursor query(SQLiteDatabase db, Class<?> clz, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, int limit) {
		if (limit >= 0) {
			return db.query(getTableName(clz), columns, selection, selectionArgs, groupBy, having, orderBy, Integer.toString(limit));
		} else {
			return db.query(getTableName(clz), columns, selection, selectionArgs, groupBy, having, orderBy);
		}
	}

	/**
	 * Execute as one transaction. Transaction is executed as synchronizely.
	 *
	 * @param t
	 */
	public void transaction(Transaction t) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			if (t.execute(this)) {
				db.setTransactionSuccessful();
			}
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * UPDATE record with WHERE clause.
	 *
	 * @param o
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int update(Object o, String whereClause, String... whereArgs) {
		SQLiteDatabase db = getWritableDatabase();
		return update(db, o, whereClause, whereArgs);
	}

	/**
	 * UPDATE record with WHERE clause.
	 *
	 * @param db
	 * @param o
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	private int update(SQLiteDatabase db, Object o, String whereClause, String... whereArgs) {
		ContentValues values = createContentValues(o);
		if (values != null) {
			return db.update(getTableName(o.getClass()), values, whereClause, whereArgs);
		} else {
			return -1;
		}
	}

	/**
	 * Execute SQL.
	 *
	 * @param sql
	 */
	public void executeSQL(String sql) {
		getWritableDatabase().execSQL(sql);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @return
	 */
	public <T> int count(Class<T> clz) {
		return count(clz, null, null, null, null, null, -1);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @param limit
	 * @return
	 */
	public <T> int count(Class<T> clz, int limit) {
		return count(clz, null, null, null, null, null, limit);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @param limit
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public <T> int count(Class<T> clz, int limit, String selection, String... selectionArgs) {
		return count(clz, selection, selectionArgs, null, null, null, limit);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public <T> int count(Class<T> clz, String selection, String... selectionArgs) {
		return count(clz, selection, selectionArgs, null, null, null, -1);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param limit
	 * @return
	 */
	public <T> int count(Class<T> clz, String selection, String[] selectionArgs, int limit) {
		return count(clz, selection, selectionArgs, null, null, null, limit);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @return
	 */
	public <T> int count(Class<T> clz, String selection, String[] selectionArgs, String orderBy) {
		return count(clz, selection, selectionArgs, null, null, orderBy, -1);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public <T> int count(Class<T> clz, String selection, String[] selectionArgs, String orderBy, int limit) {
		return count(clz, selection, selectionArgs, null, null, orderBy, limit);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public <T> int count(Class<T> clz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return count(clz, selection, selectionArgs, groupBy, having, orderBy, -1);
	}

	/**
	 * Get record count.
	 *
	 * @param clz
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public <T> int count(Class<T> clz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, int limit) {
		SQLiteDatabase db = getWritableDatabase();
		return count(db, clz, null, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * Get record count.
	 *
	 * @param db
	 * @param clz
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	private <T> int count(SQLiteDatabase db, Class<T> clz, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, int limit) {
		Cursor c = query(db, clz, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		int count = c.getCount();
		c.close();
		return count;
	}
}
