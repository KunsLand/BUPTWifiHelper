package cn.edu.bupt.wifihelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
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
		webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
		myWebView.addJavascriptInterface(this, "HTML_OUT");
		myWebView.setVisibility(View.INVISIBLE);
		myWebView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.v("AndroidWifiHelper", "Load complete: " + view.getUrl());
				if (url.endsWith("/nav_login")) submitForm();
				else if (url.endsWith("/LoginAction.action")) {
					myWebView.loadUrl("javascript:window.HTML_OUT.checkLoginStatus(document.getElementById('logout'))");
				} else if (url.endsWith("/nav_offLine")) {
                    Log.v("AndroidWifiHelper", "Try to abstract online IPs.");
					myWebView.loadUrl("javascript:window.HTML_OUT.processHTML(" +
							"'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'" +
							")");
				}
			}
			
		});
		myWebView.setWebChromeClient(new WebChromeClient(){

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				Log.i("AndroidWifiHelper", "JsAlert: " + message);
				result.confirm();
				if(!message.contains("成功")&&!message.contains("失败")) {
					if(processor!=null)
						processor.processWifiHelperStatusChanged(
								WifiHelperInterface.Status.UNKNOWN_ERROR);
					return true;
				}
				if(processor!=null)
					processor.processForceOfflineResponse(message.contains("成功"));
				return true;
			}

			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				String msg = consoleMessage.message();
				Log.v("AndroidWifiHelper", "Console message: " + msg);
				if(processor!=null && msg.contains("TypeError"))
					processor.processWifiHelperStatusChanged(
							WifiHelperInterface.Status.LOGIN_FAILED);
				return true;
			}
		});
	}

	@JavascriptInterface
	public void checkLoginStatus(String str){
		if(str==null||str.equals("null")){
			if(processor!=null)
				processor.processWifiHelperStatusChanged(
						WifiHelperInterface.Status.LOGIN_FAILED);
		}
		else checkOnlineIps();
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
        if(processor != null){
		    processor.processIpInUse(ipList);
        }
	}
	
	public void setProcessor(WifiHelperInterface processor){
		this.processor = processor;
	}
	
	public void loginGW(String account, String password){
        Log.v("AndroidWifiHelper", "Load login page.");
        if(processor!=null)
        	processor.processWifiHelperStatusChanged(
        			WifiHelperInterface.Status.LOADING_INDEX_PAGE);
		myWebView.loadUrl("http://gwself.bupt.edu.cn/nav_login");
        Log.v("AndroidWifiHelper", "Set account and password.");
		this.account = account;
		this.password = password;
	}
	
	public void checkOnlineIps(){
        Log.v("AndroidWifiHelper", "Try to load online-IPs page.");
        if(processor!=null)
        	processor.processWifiHelperStatusChanged(
        			WifiHelperInterface.Status.TRY_TO_FETCH_IP);
		myWebView.loadUrl("http://gwself.bupt.edu.cn/nav_offLine");
	}

    private void submitForm(){
        Log.v("AndroidWifiHelper", "Submit form to login.");
        if(processor!=null)
        	processor.processWifiHelperStatusChanged(
        			WifiHelperInterface.Status.TRY_TO_LOGIN);
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
