package ufes.pg.wifi.p2p.mainapplication;

import ufes.pg.wifi.p2p.chatexample.ChatInfo;
import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectDevice;
import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectManager;
import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectService;
import ufes.pg.wifi.p2p.servicediscovery.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ServicesActivity extends ListActivity {

	public static WifiDirectDevice device;
	
	private WifiDirectManager wifi;
	
	private ServicesListAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_services);
		
		wifi = DevicesActivity.wifi;
		//wifi.setConnectionInfoListener(this);
		
		setupInterface();
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
	
	private void setupInterface() {
		
		setTitle(device.deviceName + " services");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		listAdapter = new ServicesListAdapter(this, R.layout.list_item_services);
		setListAdapter(listAdapter);
		listAdapter.addAll(device.servicesList);
		listAdapter.notifyDataSetChanged();
		
		ListView lv = getListView();
		lv.setOnItemClickListener(listAdapter);
	}

	public void connectChat(WifiDirectService service) {

		wifi.connect(device.deviceAddress);
		
		ChatInfo.setRecords(service.record);
	}
	
}
