package ufes.pg.wifi.p2p.dnssdhelper;

import java.util.HashMap;

public class WifiDirectService {
	
	private String instanceName = null;
	private String registrationType = null;
	
	public HashMap<String, String> record;
	
	public WifiDirectService(String fullDomainName){
		String[] fullDomain = fullDomainName.split("\\.");
		
		if (fullDomain.length >= 2){
			instanceName = fullDomain[0];
			registrationType = fullDomain[1];
		}
	}
	
	public String getInstanceName(){
		return instanceName;
	}
	
	public String getRegistrationType(){
		return registrationType;
	}
}
