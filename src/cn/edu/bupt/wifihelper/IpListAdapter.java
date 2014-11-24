package cn.edu.bupt.wifihelper;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
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
	
	public void loginGW(String account, String password){
		helper.loginGW(account, password);
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
			convertView.setTag(viewHolder);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.textView.setText(ipList.get(position));
		viewHolder.textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				helper.forceIpOffLine(ipList.remove(position));
				IpListAdapter.this.notifyDataSetChanged();
				helper.checkIps();
			}
		});
		return convertView;
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
	public void processHistoryInfo() {
	}
	
	static class ViewHolder{
		TextView textView;
	}

}
