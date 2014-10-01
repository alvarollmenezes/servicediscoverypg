package ufes.pg.wifi.p2p.dnssdhelper;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

public class WifiDirectManager extends BroadcastReceiver {

	private WifiP2pManager manager;
	private Channel channel;
	private WifiP2pDnsSdServiceRequest serviceRequest;
	private ConnectionInfoListener connectionInfoListener;
	private IntentFilter intentFilter;
	
	public IntentFilter getIntentFilter() {
		
		return intentFilter;
	}
	
	/**
	 * Adds the Actions for the WifiP2pManager to IntentFilter and stores Manager and Channel
	 * @param manager
	 * @param channel
	 * @param responseListener
	 * @param connListener
	 */
	public WifiDirectManager(WifiP2pManager manager, Channel channel,
			final DnsSdResponseListener responseListener, ConnectionInfoListener connListener){
		
		intentFilter = new IntentFilter();
		
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

		this.manager = manager;
		this.channel = channel;
		
		this.connectionInfoListener = connListener;
		
		setupDnsSdResponseListeners(responseListener);
		setupDnsSdServiceRequest();
	}

	
	/// DNS-SD Service helpers
	
	private void setupDnsSdResponseListeners(final DnsSdResponseListener listener) {
		
		/*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
		manager.setDnsSdResponseListeners(channel, 
				new DnsSdServiceResponseListener() {

			public void onDnsSdServiceAvailable(String instanceName,
					String registrationType, WifiP2pDevice srcDevice) {

				listener.onServiceAvailable(instanceName, registrationType, srcDevice);
			}
		}, new DnsSdTxtRecordListener() {

			public void onDnsSdTxtRecordAvailable(
					String fullDomainName, Map<String, String> record,
					WifiP2pDevice device) {

				listener.onTxtRecordAvailable(fullDomainName, record, device);
			}
		});
	}
	
	private void setupDnsSdServiceRequest() {

		if (serviceRequest == null){
			// After attaching listeners, create a service request for the type of service desired
			serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
			manager.addServiceRequest(channel, serviceRequest,
					new ActionListener() {

				public void onSuccess() {
					//				appendStatus("Added service discovery request");
				}

				public void onFailure(int reason) {
					//				appendStatus("Failed adding service discovery request");
				}
			});
		}
	}
	
	private void removeDnsSdServiceRequest() {
	
		if (serviceRequest != null)
			manager.removeServiceRequest(channel, serviceRequest,
					new ActionListener() {

				public void onSuccess() {
					serviceRequest = null;
				}

				public void onFailure(int arg0) {
					removeDnsSdServiceRequest();
				}
			});
	}

	public interface DnsSdResponseListener {

		public void onServiceAvailable(String instanceName, String registrationType, 
				WifiP2pDevice srcDevice);
		
		public void onTxtRecordAvailable(String fullDomainName, Map<String, String> record,
                WifiP2pDevice device);
	}
	
	public void addLocalService(String serviceInstance, String serviceRegType,
			Map<String, String> record) {

		WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(serviceInstance, 
				serviceRegType, record);

		manager.addLocalService(channel, serviceInfo, new ActionListener() {

			public void onSuccess() {
				
			}

			public void onFailure(int error) {
				
			}
		});
	}

	public void discoverServices(ActionListener listener) {

        // Initiate discover of the services specified by addServiceRequest
		manager.discoverServices(channel, listener);
    }
	
	public void onPause() {

		removeDnsSdServiceRequest();
	}

	public void onResume(){

		setupDnsSdServiceRequest();
	}
	
	/// WifiP2p helpers
	
	public void connect(String deviceAddress){
		
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		
		manager.connect(channel, config, new ActionListener() {
			
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * Disconnect all related to this WifiP2p Connection
	 */
	public void disconnect() {

		if (manager != null && channel != null) {
			manager.removeGroup(channel, new ActionListener() {
				
				public void onFailure(int reasonCode) {
					Log.d("WifiDirectManager", "Disconnect failed. Reason :" + reasonCode);
				}

				public void onSuccess() {
				}

			});
		}
	}

	/*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		Log.d("WifiDirectManager", action);
		if (action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
				&& manager != null){

			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(
					WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {

				// we are connected with the other device, request connection
				// info to find group owner IP
				manager.requestConnectionInfo(channel, connectionInfoListener);
			}
		}
	}

}
