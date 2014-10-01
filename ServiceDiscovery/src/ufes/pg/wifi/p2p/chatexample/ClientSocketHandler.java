
package ufes.pg.wifi.p2p.chatexample;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import ufes.pg.wifi.p2p.chatexample.ChatInfo.ChatManagerReceiver;
import android.os.Handler;

public class ClientSocketHandler extends Thread {

	private ChatManagerReceiver chatManagerReceiver;
    private InetAddress serverAddress;
    private int serverPort;
    private Handler handler;
    
    private final int TIMEOUT = 5000;

    public ClientSocketHandler(ChatManagerReceiver chatManagerReceiver, 
    		InetAddress groupOwnerAddress, int serverPort, Handler handler) {
        
    	this.chatManagerReceiver = chatManagerReceiver;
    	this.serverAddress = groupOwnerAddress;
        this.serverPort = serverPort;
        this.handler = handler;
    }
    
    @Override
    public void run() {
        
    	Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(serverAddress.getHostAddress(), serverPort), TIMEOUT);
            
            ChatManager chat = new ChatManager(socket, handler);
            new Thread(chat).start();
            
            chatManagerReceiver.updateChatManager(chat);
            
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

}
