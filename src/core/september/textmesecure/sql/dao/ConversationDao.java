package core.september.textmesecure.sql.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import core.september.textmesecure.sql.DaoSession;
import core.september.textmesecure.sql.models.Conversation;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class ConversationDao extends AbstractDao<Conversation, Long>{

	public static final String TABLENAME = "CONVERSATION";
	
	 public static class Properties {
	        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
	        public final static Property Pubkey = new Property(1, String.class, "pubkey", false, "pubkey");
	        public final static Property CreatedAtUnixTime = new Property(2, Long.class, "createdAtUnixTime", false, "createdAtUnixTime");
	    };

//	    private long id;
//		private String pubKey;
//		private long createdAtUnixTime;
	
	public ConversationDao(DaoConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
	}
	
//	public ConversationDao(DaoConfig config, DaoSession daoSession) {
//        super(config, daoSession);
//	}

	public ConversationDao(DaoConfig config, DaoSession daoSession) {
		super(config, daoSession);
	}

	 public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
	        String constraint = ifNotExists? "IF NOT EXISTS ": "";
	        db.execSQL("CREATE TABLE " + constraint + "'"+TABLENAME+"' (" + //
	                "'_id' INTEGER PRIMARY KEY ," + // 0: id
	                "'pubkey' TEXT NOT NULL ," + // 1: text
	                "'createdAtUnixTime' INTEGER);"); // 3: date
	    }

	    /** Drops the underlying database table. */
	    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
	        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'"+TABLENAME+"'";
	        db.execSQL(sql);
	    }

	    /** @inheritdoc */
	    @Override
	    protected void bindValues(SQLiteStatement stmt, Conversation entity) {
	        stmt.clearBindings();
	 
	        Long id = entity.getId();
	        if (id != null) {
	            stmt.bindLong(1, id);
	        }
	        stmt.bindString(2, entity.getPubKey());
	 
	        Long createdAt  = entity.getCreatedAtUnixTime();
	        if (createdAt != null) {
	            stmt.bindLong(3, createdAt);
	        }
	 
	       
	    }

	    /** @inheritdoc */
	    @Override
	    public Long readKey(Cursor cursor, int offset) {
	        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
	    }

	    /** @inheritdoc */
	    @Override
	    public Conversation readEntity(Cursor cursor, int offset) {
	    	Conversation entity = new Conversation( //
	            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), 
	            cursor.getString(offset + 1), // text
	            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2)) ;
	        return entity;
	    }
	     
	    /** @inheritdoc */
	    @Override
	    public void readEntity(Cursor cursor, Conversation entity, int offset) {
	        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
	        entity.setPubKey(cursor.getString(offset + 1));
	        entity.setCreatedAtUnixTime(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
	     }
	    
	    /** @inheritdoc */
	    @Override
	    protected Long updateKeyAfterInsert(Conversation entity, long rowId) {
	        entity.setId(rowId);
	        return rowId;
	    }
	    
	    /** @inheritdoc */
	    @Override
	    public Long getKey(Conversation entity) {
	        if(entity != null) {
	            return entity.getId();
	        } else {
	            return null;
	        }
	    }

	    /** @inheritdoc */
	    @Override
	    protected boolean isEntityUpdateable() {
	        return true;
	    }

}
