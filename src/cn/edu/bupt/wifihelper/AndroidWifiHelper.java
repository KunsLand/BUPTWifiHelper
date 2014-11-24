package cn.edu.bupt.wifihelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class AndroidWifiHelper {
	private WebView myWebView;
	private String account;
	private String password;
	private WifiHelperInterface processor;
	
	public AndroidWifiHelper(Activity activity){
		myWebView = new WebView(activity);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.addJavascriptInterface(this, "HTML_OUT");
		myWebView.setVisibility(View.INVISIBLE);
		myWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				if (url.endsWith("/nav_login")) submitForm();
				else if (url.endsWith("/LoginAction.action")) {
					checkIps();
				} else if (url.endsWith("/nav_offLine")) {
                    Log.v("AndroidWifiHelper", "Try to abstract online IPs.");
					myWebView.loadUrl("javascript:window.HTML_OUT.processHTML(" +
							"'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'" +
							")");
				}
			}
		});
	}

	@JavascriptInterface
	public void processHTML(String html){
		String IP_ADDRESS_PATTERN =
		        "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(html);
        ArrayList<String> ipList = new ArrayList<String>();
		while(matcher.find()) ipList.add(matcher.group());
        Log.i("AndroidWifiHelper", "Online IPs: " + ipList.toString());
        if(processor != null)
		    processor.processIpInUse(ipList);
	}
	
	public void setProcessor(WifiHelperInterface processor){
		this.processor = processor;
	}
	
	public void loginGW(String account, String password){
		this.account = account;
		this.password = password;
		myWebView.loadUrl("http://gwself.bupt.edu.cn/nav_login");
        Log.v("AndroidWifiHelper", "Set account and password, and load login page.");
	}
	
	public void checkIps(){
        Log.v("AndroidWifiHelper", "Try to load online-IPs page.");
		myWebView.loadUrl("http://gwself.bupt.edu.cn/nav_offLine");
	}

    private void submitForm(){
        Log.v("AndroidWifiHelper", "Submit form");
        myWebView
                .loadUrl("javascript:(function(){"
                        + "document.getElementById('account').value='" + account + "';"
                        + "document.getElementById('pass').value='" + password + "';"
                        + "var ins = document.getElementsByTagName('input');"
                        + "var evt = document.createEvent('HTMLEvents');"
                        + "evt.initEvent('click',true,true);"
                        + "ins[ins.length-1].dispatchEvent(evt);"
                        + "})()");
    }
	
	public void forceIpOffLine(String ip){
        Log.v("AndroidWifiHelper", "Force offline " + ip);
		myWebView
		.loadUrl("javascript:(function(){"
				+ "var els = document.getElementById('Maint1').getElementsByTagName('a');"
				+ "var evt = document.createEvent('HTMLEvents');"
				+ "evt.initEvent('click',true,true);"
				+ "for(var i=0;i<els.length;i++){"
				+ "if(els[i].parentNode.parentNode.innerText.indexOf('"
				+ ip + "')>-1)"
				+ "els[i].dispatchEvent(evt);" + "}"
				+ "})()");
		
	}
	
}
