package ufes.pg.wifi.p2p.chatexample;

import java.util.HashMap;
import java.util.List;

import ufes.pg.wifi.p2p.chatexample.ChatInfo.ChatManagerReceiver;
import ufes.pg.wifi.p2p.servicediscovery.R;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatActivity extends ListActivity implements ChatManagerReceiver, Handler.Callback {

	private static final String instanceName = "Chat Example";
	private static final String registrationType = "_presence._tcp";
	private static final String myNick = android.os.Build.MODEL;
	private static final String myPort = "4545";
	
	private static final String RECORD_TXTVERS = "txtvers";
	private static final String RECORD_NICK = "nick";
	private static final String RECORD_PORT = "port.p2pj";
	private static final String RECORD_STATUS = "status";
	
	private ChatMessageAdapter listAdapter;
	private ChatManager chatManager;
	private Handler handler = new Handler(this);
	
	public static String getInstanceName(){
		return instanceName;
	}
	
	public static String getRegistrationType(){
		return registrationType;
	}
	
	public static HashMap<String, String> getRecords(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(RECORD_TXTVERS, "1");
        map.put(RECORD_NICK, myNick);
        map.put(RECORD_PORT, "4545");
        map.put(RECORD_STATUS, "avail");
        
		return map;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		setupInterface();
		
		setupChatEngine();
	}

	private void setupInterface() {
		
		listAdapter = new ChatMessageAdapter(this, android.R.id.text1);
		setListAdapter(listAdapter);
		
		findViewById(R.id.btnSend).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				btnSend_Click();
			}
		});
	}
	
	private void setupChatEngine() {

		Thread thread = new Thread();
		
		if (ChatInfo.isGroupOwner()) {
			thread = new GroupOwnerSocketHandler(this, Integer.parseInt(myPort), handler);
			thread.start();
		} else {
			int port = Integer.parseInt(ChatInfo.getRecords().get(RECORD_PORT));
			
			thread = new ClientSocketHandler(this, ChatInfo.getGroupOwnerAddress(), port, handler);
			thread.start();
		}
	}

	public void updateChatManager(ChatManager manager) {
		
		chatManager = manager;
	}
	
	public boolean handleMessage(Message msg) {
	
		byte[] readBuf = (byte[]) msg.obj;
		String readMessage = new String(readBuf, 0, msg.arg1);
		
		addMessage(readMessage);
		
		return true;
	}
	
	private void addMessage(String msg){
		
		listAdapter.add(msg);
		listAdapter.notifyDataSetChanged();
	}

	private void btnSend_Click() {

		TextView chatLine = (TextView) findViewById(R.id.txtChatLine);
		String msg = chatLine.getText().toString();
		
		if (chatManager != null){
			
			chatManager.write((myNick + ": " + msg).getBytes());
			chatLine.setText("");
			chatLine.clearFocus();
		}
		
		addMessage("Me: " + msg);
	}
	
	/**
	 * ArrayAdapter to manage chat messages.
     */
    public class ChatMessageAdapter extends ArrayAdapter<String> {

        List<String> messages = null;

        public ChatMessageAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                		Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_1, null);
            }
            String message = getItem(position);
            if (message != null && !message.isEmpty()) {
                TextView nameText = (TextView) v
                        .findViewById(android.R.id.text1);

                if (nameText != null) {
                    nameText.setText(message);
                    if (message.startsWith("Me: ")) {
                        nameText.setTextAppearance(getContext(),
                                R.style.normalText);
                    } else {
                        nameText.setTextAppearance(getContext(),
                                R.style.boldText);
                    }
                }
            }
            return v;
        }
    }
  
}
