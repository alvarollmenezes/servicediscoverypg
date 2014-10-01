package ufes.pg.wifi.p2p.mainapplication;

import ufes.pg.wifi.p2p.chatexample.ChatActivity;
import ufes.pg.wifi.p2p.dnssdhelper.WifiDirectService;
import ufes.pg.wifi.p2p.servicediscovery.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ServicesListAdapter extends ArrayAdapter<WifiDirectService>
		implements OnItemClickListener{

	private ServicesActivity activity;
	private int resource;
	
	public ServicesListAdapter(ServicesActivity activity, int resource) {
		
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

		WifiDirectService service = getItem(position);
		if (service != null){
			TextView name = (TextView) v.findViewById(R.id.service_name);
			name.setText(service.getInstanceName());
		}

		return v;
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	
		WifiDirectService service = getItem(position);
		
		if (service.getInstanceName().equalsIgnoreCase(ChatActivity.getInstanceName()))
		{
			activity.connectChat(service);
		}
	}
}
