package cn.edu.bupt.wifihelper;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class DemoApp extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listView = (ListView) findViewById(R.id.listview);
		ArrayList<String> ipList = new ArrayList<String>();
		IpListAdapter adapter = new IpListAdapter(this, R.layout.item, R.id.ip_text, ipList);
		listView.setAdapter(adapter);
		adapter.loginGW("", "");
	}
}
