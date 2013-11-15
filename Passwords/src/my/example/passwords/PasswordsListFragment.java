package my.example.passwords;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class PasswordsListFragment extends ListFragment 
	implements LoaderCallbacks<Cursor> {

	SimpleCursorAdapter mAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		setHasOptionsMenu(true);
		
		mAdapter = new SimpleCursorAdapter(
				getActivity(), android.R.layout.simple_list_item_1, null, 
				new String[] { Data.TITLE }, 
				new int[] { android.R.id.text1 }, 0);
		
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		
		super.onActivityCreated(savedInstanceState);
	}

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		Fragment fragment = new PasswordsFormFragment();
		Bundle args = new Bundle();
		args.putLong(Data._ID, id);
		fragment.setArguments(args);


		FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
		tr.replace(R.id.content, fragment, PasswordsFormFragment.TAG);
		tr.addToBackStack(null);
		tr.commit();

		super.onListItemClick(l, v, position, id);
	
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.passwords_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_new:
			Fragment fragment = new PasswordsFormFragment();
			FragmentTransaction tr = getActivity().getSupportFragmentManager().beginTransaction();
			tr.replace(R.id.content, fragment, PasswordsFormFragment.TAG);
			tr.addToBackStack(null);
			tr.commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), Data.URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	
	
}
