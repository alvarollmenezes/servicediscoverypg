
package ufes.pg.wifi.p2p.chatexample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;

/**
 * Handles reading and writing of messages with socket buffers. Uses a Handler
 * to post messages to UI thread for UI updates.
 */
public class ChatManager implements Runnable {

    private Socket socket = null;
    private Handler handler;
    
    public ChatManager(Socket socket, Handler handler) {
        
    	this.socket = socket;
        this.handler = handler;
    }

    private InputStream iStream;
    private OutputStream oStream;

    public void run() {
        
    	try {

            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    // Read from the InputStream
                    bytes = iStream.read(buffer);
                    if (bytes == -1) {
                        break;
                    }

                    // Send the obtained bytes to the UI Activity
                    handler.obtainMessage(-1, bytes, -1, buffer).sendToTarget();
                    
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] buffer) {

    	try {
    		oStream.write(buffer);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

}
