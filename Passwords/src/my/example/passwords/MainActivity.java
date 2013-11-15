package my.example.passwords;

import my.example.passwords.GenericDialogFragment.GenericDialogFragmentListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity implements GenericDialogFragmentListener {
	
	public static final int DIALOG_PASSWORD = 0;
	public static final int DIALOG_SHUFFLE = 1;
	
	private static final String PREFS = "PasswordsPrefs";
	private static final String MASTER_SECRET = "master";
	
	String mMasterPassword = null;
	String mMasterSecret = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		SharedPreferences prefs = getSharedPreferences(PREFS, 0);
		mMasterSecret = prefs.getString(MASTER_SECRET, null);
		
		if(mMasterPassword == null) {
			int rTitleId = (mMasterSecret != null) 
					? R.string.app_name : R.string.new_master_password;
			GenericDialogFragment fragment = GenericDialogFragment
					.newInstance(DIALOG_PASSWORD, 
							R.layout.password_dialog, 
							getString(rTitleId));
			fragment.show(getSupportFragmentManager(), "master_password");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public String getMasterPassword() {
		return mMasterPassword;
	}

	@Override
	public boolean onDialogPositiveClick(int id, View view) {
		switch(id) {
		case DIALOG_PASSWORD:
			
			mMasterPassword = ((EditText)view.findViewById(R.id.master_password)).getText().toString();

			if(mMasterSecret != null) {
				if(!mMasterPassword.equals(Crypto.decrypt(mMasterSecret, mMasterPassword))) {
					finish();
					return true;
				}
			} else {
				SharedPreferences.Editor ed = getSharedPreferences(PREFS, 0).edit();
				ed.putString(MASTER_SECRET, Crypto.encrypt(mMasterPassword, mMasterPassword));
				ed.commit();
			}
			FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            tr.replace(R.id.content, new PasswordsListFragment());
            tr.commit();
			
			return true;
		case DIALOG_SHUFFLE:
			
			PasswordsFormFragment form = (PasswordsFormFragment)getSupportFragmentManager()
				.findFragmentByTag(PasswordsFormFragment.TAG);
			if(form != null) {
				form.shufflePassword(view);
			}
			return true;
		}
			
		return true;
	}

	@Override
	public boolean onDialogNegativeClick(int id, View view) {
		switch(id) {
		case DIALOG_PASSWORD: finish(); 
		}
		return true;
	}


}
