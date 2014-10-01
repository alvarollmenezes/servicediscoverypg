package ufes.pg.wifi.p2p.dnssdhelper;

import java.util.ArrayList;

import android.net.wifi.p2p.WifiP2pDevice;

public class WifiDirectDevice extends WifiP2pDevice {

	public ArrayList<WifiDirectService> servicesList = 
			new ArrayList<WifiDirectService>();
	
	public WifiDirectDevice(){
		super();
	}
	
	public WifiDirectDevice(WifiP2pDevice device){
		super(device);
	}
}
