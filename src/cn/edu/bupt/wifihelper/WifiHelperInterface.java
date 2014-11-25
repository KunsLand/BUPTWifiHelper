package cn.edu.bupt.wifihelper;

import java.util.ArrayList;

public interface WifiHelperInterface{
	public void processIpInUse(ArrayList<String> ipList);
	public void processForceOfflineResponse(String message);
	public void processIpListEmpty();
	public void processPageLoadingProgress(int newProgress);
	public void processLoginGWFailed();
}
