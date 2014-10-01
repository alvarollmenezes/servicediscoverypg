
package ufes.pg.wifi.p2p.chatexample;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ufes.pg.wifi.p2p.chatexample.ChatInfo.ChatManagerReceiver;
import android.os.Handler;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */
public class GroupOwnerSocketHandler extends Thread {

    ServerSocket socket = null;
    private ChatManagerReceiver chatManagerReceiver;
    private Handler handler;
    
    private final int THREAD_COUNT = 10;

    public GroupOwnerSocketHandler(ChatManagerReceiver chatManagerReceiver, int port,
    		Handler handler) {
    	
    	try {
    		socket = new ServerSocket(port);
    		this.chatManagerReceiver = chatManagerReceiver;
    		this.handler = handler;

    	} catch (IOException e) {
    		e.printStackTrace();
    		pool.shutdownNow();
    	}
    }

    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void run() {
        
    	while (true) {
            try {
                // A blocking operation. Initiate a ChatManager instance when
                // there is a new connection
            	ChatManager chat = new ChatManager(socket.accept(), handler);
                pool.execute(chat);
                
                chatManagerReceiver.updateChatManager(chat);

            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }

}
