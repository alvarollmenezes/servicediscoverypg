package ufes.pg.wifi.p2p.chatexample;

import java.net.InetAddress;
import java.util.HashMap;

public class ChatInfo {

	private static boolean isGroupOwner;
	private static InetAddress groupOwnerAddress;
	private static HashMap<String, String> records;
	
	public static boolean isGroupOwner() {
		return isGroupOwner;
	}
	public static void setGroupOwner(boolean isGroupOwner) {
		ChatInfo.isGroupOwner = isGroupOwner;
	}

	public static InetAddress getGroupOwnerAddress() {
		return groupOwnerAddress;
	}
	public static void setGroupOwnerAddress(InetAddress groupOwnerAddress) {
		ChatInfo.groupOwnerAddress = groupOwnerAddress;
	}

	public static HashMap<String, String> getRecords() {
		return records;
	}	
	public static void setRecords(HashMap<String, String> records) {
		ChatInfo.records = records;
	}

	public interface ChatManagerReceiver {
		
		public void updateChatManager(ChatManager manager);
	}
	
}
