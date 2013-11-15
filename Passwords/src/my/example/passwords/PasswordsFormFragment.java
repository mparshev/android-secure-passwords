package my.example.passwords;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

@SuppressWarnings("deprecation")
public class PasswordsFormFragment extends Fragment {
	
	public static final String TAG = PasswordsFormFragment.class.getName();

	MainActivity mActivity;

	Long mRowId;
	
	EditText mTitle;
	EditText mPath;
	EditText mLogin;
	EditText mPassword;
	
	boolean mShowPassword = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.passwords_form, container, false);
		
		mTitle = (EditText)view.findViewById(R.id.edit_title);
		mPath = (EditText)view.findViewById(R.id.edit_path);
		mLogin = (EditText)view.findViewById(R.id.edit_login);
		mPassword = (EditText)view.findViewById(R.id.edit_password);
		
		setHasOptionsMenu(true);
		
		Bundle args = getArguments();
		if(args != null) mRowId = args.getLong(Data._ID);
		
		if(mRowId != null) {
			Cursor cursor = getActivity().getContentResolver().query( 
					Uri.withAppendedPath(Data.URI, ""+mRowId), 
					null, null, null, null);
			if(cursor != null && cursor.moveToFirst()) {
				mTitle.setText(Data.getString(cursor, Data.TITLE));
				mPath.setText(Data.getString(cursor, Data.PATH));
				mLogin.setText(Data.getString(cursor, Data.LOGIN));
				mPassword.setText(Crypto.decrypt(
					Data.getString(cursor, Data.SECRET), 
					mActivity.getMasterPassword()));
			}
		}
		
		return view;
	}


	@Override
	public void onAttach(Activity activity) {
		try {
			mActivity = (MainActivity)activity;
		} catch(ClassCastException ex) {
			throw new ClassCastException(activity.toString() 
					+ " must be MainActivity");
		}
		super.onAttach(activity);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.passwords_form, menu);
		if(mRowId != null)
			menu.findItem(R.id.action_discard).setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_accept:
			ContentValues values = new ContentValues();
			values.put(Data.TITLE, mTitle.getText().toString());
			values.put(Data.PATH, mPath.getText().toString());
			values.put(Data.LOGIN, mLogin.getText().toString());
			values.put(Data.SECRET, 
					Crypto.encrypt(mPassword.getText().toString(), 
							mActivity.getMasterPassword()));
			if(mRowId == null)
				getActivity().getContentResolver().insert(Data.URI, values);
			else
				getActivity().getContentResolver().update(
						Uri.withAppendedPath(Data.URI, ""+mRowId), 
						values, null, null);
			getActivity().getSupportFragmentManager().popBackStack();
			return true;
		case R.id.action_show:
			mShowPassword = !mShowPassword;
			if(mShowPassword)
				mPassword.setTransformationMethod(null);
			else
				mPassword.setTransformationMethod(new PasswordTransformationMethod());
			return true;
		case R.id.action_shuffle:
			DialogFragment shuffleDialog = GenericDialogFragment.newInstance(MainActivity.DIALOG_SHUFFLE, R.layout.shuffle_dialog, getString(R.string.app_name));
			shuffleDialog.show(getActivity().getSupportFragmentManager(), "shuffle_dialog");
			return true;
		case R.id.action_discard:
			if(mRowId != null) {
				getActivity().getContentResolver().delete(Uri.withAppendedPath(Data.URI, ""+mRowId), null, null);
				getActivity().getSupportFragmentManager().popBackStack();
			}
			return true;
		case R.id.action_copy:
			ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Service.CLIPBOARD_SERVICE);
			clipboard.setText(mPassword.getText().toString());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_show).setChecked(mShowPassword);
		super.onPrepareOptionsMenu(menu);
	}


	public void shufflePassword(View view) {
		int length = Integer.parseInt(((EditText)view.findViewById(R.id.passwordLength)).getText().toString());
		String classes = "";
		if(((CheckBox)view.findViewById(R.id.useLowercase)).isChecked()) 
			classes += getResources().getString(R.string.lowercase) + " ";
		if(((CheckBox)view.findViewById(R.id.useUppercase)).isChecked()) 
			classes += getResources().getString(R.string.uppercase) + " ";
		if(((CheckBox)view.findViewById(R.id.useDigits)).isChecked()) 
			classes += getResources().getString(R.string.digits) + " ";
		if(((CheckBox)view.findViewById(R.id.useSymbols)).isChecked()) 
			classes += getResources().getString(R.string.symbols) + " ";
		mPassword.setText(Crypto.generate(length, classes.trim().split(" ")));

	}
	
}
