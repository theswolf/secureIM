package core.september.textmesecure.services;

import java.util.List;

import android.content.Context;

import com.niusounds.asd.SQLiteDAO;

import core.september.textmesecure.algo.O9Cypher;
import core.september.textmesecure.algo.O9Message;
import core.september.textmesecure.sql.models.KeyRepo;
import core.september.textmesecure.sql.models.User;

public class O9KeyController {

	private static O9KeyController controller;
	private static Context context;
	private O9KeyController() {
		
	}
	public static O9KeyController getInstance(Context context) {
		if(controller == null) {
			controller = new O9KeyController();
		}
		O9KeyController.context = context;
		return controller;
	}
	public KeyRepo getByFriendLogin(String actualFriendLogin) {
		SQLiteDAO keyDao = SQLiteDAO.getInstance(context, KeyRepo.class);
		List<KeyRepo> repoResult = keyDao.get(KeyRepo.class, "TTL>? and friendLogin=?","0",actualFriendLogin);
		return repoResult != null && repoResult.size() > 0 ? repoResult.get(0) : null;
	}
	public String processTextMessage(String messageString, String actualFriendLogin) {
		KeyRepo repo = getByFriendLogin(actualFriendLogin);
		String cipheredText = O9Cypher.getInstance().crypt(repo.getMyPrivateKey(), repo.getFriendKey(), messageString);
		O9Message o9Message = new O9Message();
		o9Message.setType(O9Message.Type.MESSAGE);
		o9Message.setEncryptedMessage(cipheredText);
		o9Message.setMyPublicKey(repo.getMyPublicKey());
		o9Message.setFriendPublicKey(repo.getFriendKey());
		return o9Message.toString();
	}
	
	public String processExchangeMessage(String myPublicKey) {
		O9Message o9Message = new O9Message();
		o9Message.setType(O9Message.Type.KEY_EXCHANGE);
		o9Message.setMyPublicKey(myPublicKey);
		return o9Message.toString();
	}
	
	public String generatePublicKey(String actualFriendLogin) {
		KeyRepo repo = O9Cypher.getInstance().generateKey(actualFriendLogin);
		 SQLiteDAO dao = SQLiteDAO.getInstance(context, User.class);
	     User me = dao.get(User.class).get(0);
	     
	    switch (me.getSubscriptionType()) {
		case BASIC:
			repo.setTTL(50);
			break;
		case PAID:
			repo.setTTL(10);
			break;
		case VIP:
			repo.setTTL(1);
			break;
		default:
			break;
		}
	    repo.setFriendLogin(actualFriendLogin);
	    dao = SQLiteDAO.getInstance(context, KeyRepo.class);
	    dao.insert(repo);
	    return repo.getMyPublicKey();
	}
	public void decreaseTTL(String actualFriendLogin) {
		KeyRepo repo = getByFriendLogin(actualFriendLogin);
		repo.setTTL(repo.getTTL()-1);
		SQLiteDAO keyDao = SQLiteDAO.getInstance(context, KeyRepo.class);
		keyDao.update(repo, "_id=?", ""+repo.get_id());
	}
}
