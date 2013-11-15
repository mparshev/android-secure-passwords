package my.example.passwords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class GenericDialogFragment extends DialogFragment {
	
	private static final String DIALOG_ID = "dialog_id";
	private static final String DIALOG_TITLE = "dialog_title";
	private static final String DIALOG_LAYOUT = "dialog_layout";

	int mId;
	View mView;
	
	public static GenericDialogFragment newInstance(int id, int layout, String title) {
		GenericDialogFragment dialog = new GenericDialogFragment();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ID, id);
		args.putInt(DIALOG_LAYOUT, layout);
		args.putString(DIALOG_TITLE, title);
		dialog.setArguments(args);
		return dialog;
	}
	
	public interface GenericDialogFragmentListener {
		public boolean onDialogPositiveClick(int id, View view);
		public boolean onDialogNegativeClick(int id, View view);
	}
	
	GenericDialogFragmentListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (GenericDialogFragmentListener)activity;
		} catch(ClassCastException ex) {
			throw new ClassCastException("Activity must inplements GenericDialogFragmentListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mId = getArguments().getInt(DIALOG_ID);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getArguments().getString(DIALOG_TITLE));
		mView = getActivity().getLayoutInflater().inflate(getArguments().getInt(DIALOG_LAYOUT), null);
		builder.setView(mView);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mListener.onDialogPositiveClick(mId, mView)) 
					dismiss();
				
			}
		});
		
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mListener.onDialogNegativeClick(mId, mView)) 
					dismiss();
			}
		});
		
		return builder.create();
	}
	
}
