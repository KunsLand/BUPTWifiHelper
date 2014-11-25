package cn.edu.bupt.wifihelper;

import java.util.ArrayList;

public interface WifiHelperInterface{
	public void processIndexPageLoading(int newProgress);
	public void processLoginGWProgress(int newProgress);
	public void processLoginGWFailed();
	public void processIpFetching(int newProgress);
	public void processIpInUse(ArrayList<String> ipList);
	public void processForceOfflineResponse(String message);
}
