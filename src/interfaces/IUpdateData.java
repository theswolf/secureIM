package interfaces;
import core.september.textmesecure.types.FriendInfo;


public interface IUpdateData {
	public void updateData(FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}
