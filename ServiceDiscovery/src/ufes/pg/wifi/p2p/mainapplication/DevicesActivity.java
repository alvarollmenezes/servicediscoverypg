package ufes.pg.wifi.p2p.mainapplication;

import java.util.HashMap;
import java.util.Map;

import ufes.pg.wifi.p2p.chatexample.ChatActivity;
import ufes.pg.wifi.p2p.chatexample.ChatInfo;
import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectDevice;
import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectManager;
import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectService;
import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectManager.DnsSdResponseListener;
import ufes.pg.wifi.p2p.servicediscovery.R;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class DevicesActivity extends ListActivity implements DnsSdResponseListener,
				ConnectionInfoListener{

	public static WifiDirectManager wifi;
	
    private Menu optionsMenu;
    
    private Toast toastDiscovery;
    
    DevicesListAdapter listAdapter;
	
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devices);

		setupInterface();
		
		setupWifiDirect();
	}
	
	@SuppressLint("ShowToast")
	private void setupInterface() {
		
		listAdapter = new DevicesListAdapter(this, R.layout.list_item_devices);
		setListAdapter(listAdapter);
		
		toastDiscovery = Toast.makeText(getBaseContext(), 
				"Failed Requesting Service Discovery.", Toast.LENGTH_LONG);
		
		ListView lv = getListView();
		lv.setOnItemClickListener(listAdapter);
	}

	private void setupWifiDirect() {

		WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		Channel channel = manager.initialize(this, getMainLooper(), null);

		wifi = new WifiDirectManager(manager, channel, this, this);
		
		wifi.addLocalService(ChatActivity.getInstanceName(), ChatActivity.getRegistrationType(), 
				ChatActivity.getRecords());
		wifi.addLocalService("LaserWriter 8500", "_printer._tcp", null);
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		this.optionsMenu = menu;
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_devices, menu);
		boolean result = super.onCreateOptionsMenu(menu);
		
		refreshServices();
		
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		if (id == R.id.menuRefresh) {
			
			refreshServices();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onResume() {

		super.onResume();

		wifi.onResume();
		
		registerReceiver(wifi, wifi.getIntentFilter());
	}

	@Override
	protected void onPause() {

		super.onPause();

		wifi.onPause();
		
		unregisterReceiver(wifi);
	}

	/**
	 * Call service discovery and update refresh button in case of success
	 */
	private void refreshServices() {
		
		toastDiscovery.cancel();
		
		listAdapter.clear();
		listAdapter.notifyDataSetChanged();
		
		wifi.discoverServices(new ActionListener() {
			
			public void onSuccess() {
				
				setRefreshActionButtonState(true);
			}
			
			public void onFailure(int reason) {
				
				toastDiscovery.show();
			}
		});
	}

	public void setRefreshActionButtonState(final boolean refreshing) {
	    
		if (optionsMenu != null) {
	        final MenuItem refreshItem = optionsMenu
	            .findItem(R.id.menuRefresh);
	        if (refreshItem != null) {
	            if (refreshing) {
	                refreshItem.setActionView(R.layout.actionbar_refresh_progress);
	            } else {
	                refreshItem.setActionView(null);
	            }
	        }
	    }
	}
	
	public void onServiceAvailable(String instanceName,
			String registrationType, WifiP2pDevice srcDevice) {
		
		setRefreshActionButtonState(false);
	}

	public void onTxtRecordAvailable(String fullDomainName,
			Map<String, String> record, WifiP2pDevice device) {

		WifiDirectService service = new WifiDirectService(fullDomainName);
		
		service.record = (HashMap<String, String>) record;
		
		addItem(new WifiDirectDevice(device), service);
	}
	
	private void addItem(WifiDirectDevice device, WifiDirectService service) {

		int position = listAdapter.getPosition(device);

		if (position < 0){
			device.servicesList.add(service);
			listAdapter.add(device);
		}
		else {
			listAdapter.getItem(position).servicesList.add(service);
		}

		listAdapter.notifyDataSetChanged();
	}

	public void onConnectionInfoAvailable(WifiP2pInfo info) {

		ChatInfo.setGroupOwner(info.isGroupOwner);
		ChatInfo.setGroupOwnerAddress(info.groupOwnerAddress);

		Intent i = new Intent(getApplicationContext(), ChatActivity.class);
		startActivity(i);
	}
	
}
