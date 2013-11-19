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
		o9Message.setSenderPublicKey(repo.getMyPublicKey());
		o9Message.setReceiverPublicKey(repo.getFriendKey());
		return o9Message.toString();
	}
	
	public String processExchangeMessage(String myPublicKey) {
		O9Message o9Message = new O9Message();
		o9Message.setType(O9Message.Type.KEY_EXCHANGE);
		o9Message.setSenderPublicKey(myPublicKey);
		return o9Message.toString();
	}
	
	public String processAcceptMessage(O9Message o9Message,String myNewPublicKey) {
		o9Message.setType(O9Message.Type.KEY_ACCEPT);
		o9Message.setReceiverPublicKey(o9Message.getSenderPublicKey());
		o9Message.setSenderPublicKey(myNewPublicKey);
		return o9Message.toString();
	}
	
	public String generatePublicKey(String actualFriendLogin,String friendKey) {
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
	    if(friendKey!= null) {
	    	repo.setFriendKey(friendKey);
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
	public void updateKeyPair(String myKey, String friendKey, String friendLogin) {
		SQLiteDAO keyDao = SQLiteDAO.getInstance(context, KeyRepo.class);
		List<KeyRepo> repoResults = keyDao.get(KeyRepo.class, "myPublicKey=? and friendLogin=?",myKey,friendLogin);
		for(KeyRepo repoResult: repoResults) {
			repoResult.setFriendKey(friendKey);
			keyDao.update(repoResult, "myPublicKey=? and friendLogin=?", myKey,friendLogin);
		}
		
	}
}
