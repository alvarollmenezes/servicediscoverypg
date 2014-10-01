package ufes.pg.wifi.p2p.mainapplication;

import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectDevice;
import ufes.pg.wifi.p2p.servicediscovery.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DevicesListAdapter extends ArrayAdapter<WifiDirectDevice> 
		implements OnItemClickListener {

	private Activity activity;
	private int resource;
	
	public DevicesListAdapter(Activity activity, int resource) {
		
		super(activity, resource);
		
		this.activity = activity;
		this.resource = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		View v = convertView;
		
		if (v == null){
			LayoutInflater vi = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(resource, null);
		}
		
		WifiDirectDevice device = getItem(position);
		if (device != null){
			TextView name = (TextView) v.findViewById(R.id.device_name);
			name.setText(device.deviceName);

			TextView servicesCount = (TextView) v.findViewById(R.id.services_count);
			servicesCount.setText(device.servicesList.size() + " services");
		}
		
		return v;
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		ServicesActivity.device = getItem(position);
		
		Intent i = new Intent(activity.getApplicationContext(), ServicesActivity.class);		
		activity.startActivity(i);
	}
	
}
