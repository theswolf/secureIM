package core.september.textmesecure.sql;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import core.september.textmesecure.sql.dao.ConversationDao;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

public class DaoSession extends AbstractDaoSession {

	private final DaoConfig conversationDaoConfig;

	private final ConversationDao conversationDao;

	public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
	daoConfigMap) {
		super(db);

		conversationDaoConfig = daoConfigMap.get(ConversationDao.class).clone();
		conversationDaoConfig.initIdentityScope(type);

		conversationDao = new ConversationDao(conversationDaoConfig, this);

		registerDao(conversationDao.getClass(), conversationDao);
	}

	protected void registerDao(Class clazz, AbstractDao dao ) {
		super.registerDao(clazz, dao);
	}

	public void clear() {
		conversationDaoConfig.getIdentityScope().clear();
	}

	public ConversationDao getConversationDao() {
		return conversationDao;
	}


}
