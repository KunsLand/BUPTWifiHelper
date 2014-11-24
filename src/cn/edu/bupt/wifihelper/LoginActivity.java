package cn.edu.bupt.wifihelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	public static final String SHARED_PREFERENCE_NAME = "LoginInfo";
	public static final String SHARED_KEY_ACCOUNT = "account";
	public static final String SHARED_KEY_PASSWORD = "password";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
		SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
		EditText account = (EditText) findViewById(R.id.account);
		account.setText(sp.getString(SHARED_KEY_ACCOUNT, ""));
		EditText password = (EditText) findViewById(R.id.password);
		password.setText(sp.getString(SHARED_KEY_PASSWORD, ""));
	}
	public void doLogin(View v){
		Intent intent = new Intent(v.getContext(), IpListManagerActivity.class);
		String account = ((EditText)findViewById(R.id.account)).getText().toString(),
				password = ((EditText) findViewById(R.id.password)).getText().toString();
		intent.putExtra(SHARED_KEY_ACCOUNT, account);
		intent.putExtra(SHARED_KEY_PASSWORD,	password);

		SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
		Editor editor = sp.edit();
		editor.putString(SHARED_KEY_ACCOUNT, account);
		editor.putString(SHARED_KEY_PASSWORD, password);
		editor.commit();
	}
}
