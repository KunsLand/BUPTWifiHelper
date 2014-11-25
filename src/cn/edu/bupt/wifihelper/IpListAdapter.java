package cn.edu.bupt.wifihelper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IpListAdapter extends ArrayAdapter<String>
	implements WifiHelperInterface {
	
	private ArrayList<String> ipList;
	private Activity context;
	private int resource, textViewResourceId;
	private AndroidWifiHelper helper;

	public IpListAdapter(Activity context, int resource, int textViewResourceId,
			ArrayList<String> ipList) {
		super(context, resource, textViewResourceId, ipList);
		this.context =context;
		this.resource = resource;
		this.textViewResourceId = textViewResourceId;
		this.ipList = ipList;
		this.helper = new AndroidWifiHelper(context);
		helper.setProcessor(this);
	}
	
	public void loginGW(final String account, final String password){
		context.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				helper.loginGW(account, password);
			}
		});
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Log.v("IpListAdapter", "process getView");
		if(convertView == null){
			Log.v("IpListAdapter", "reset null convertView");
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(resource, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.textView = (TextView)
					convertView.findViewById(textViewResourceId);
			viewHolder.textView.setText(ipList.get(position));
			convertView.setTag(viewHolder);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.textView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					helper.forceIpOffLine(ipList.remove(position));
					IpListAdapter.this.notifyDataSetChanged();
					v.performClick();
					break;
				default:
					break;
				}
				return true;
			}
		});
		return convertView;
	}
	
	static class ViewHolder{
		TextView textView;
	}

	@Override
	public void processIpInUse(ArrayList<String> ipList) {
		this.ipList.clear();
		this.ipList.addAll(ipList);
		context.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Log.v("IpListAdapter", "notify data change");
				IpListAdapter.this.notifyDataSetChanged();
			}});
	}
	
	@Override
	public void processForceOfflineResponse(String message) {
		new AlertDialog.Builder(context)
		.setMessage(message)
		.setPositiveButton("OK", null)
		.show();
		context.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				IpListAdapter.this.notifyDataSetChanged();
			}});
	}

	@Override
	public void processLoginGWProgress(int newProgress) {
		Log.v("IpListAdapter", "Logining: " + newProgress + "%");
	}

	@Override
	public void processLoginGWFailed() {
		new AlertDialog.Builder(context)
			.setMessage("Login failed.")
			.setPositiveButton("OK", null)
			.show();
	}

	@Override
	public void processIpFetching(int newProgress) {
		Log.v("IpListAdapter", "IpFeteching: " + newProgress + "%");
	}

	@Override
	public void processIndexPageLoading(int newProgress) {
		Log.v("IpListAdapter", "IndexPageLoading: " + newProgress + "%");
	}

}
