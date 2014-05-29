package com.example.ffmpeg_trial;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class FragmentOutput extends ListFragment {
	
	ArrayAdapter<String> adapter;
	ListView lv;
	int itemPosition = -1; // position of the selected view
	View sel_lv = null; // saves the selected view, passed to popup window
	String sel_file_text = ""; // text of seected item
	String fileRoot = ""; // keep track of the root of the videos
	SharedPreferences sharedpreferences;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Get directory and populate list
		File dir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_MOVIES), "/ffmpeg/");
		File[] filesArray = dir.listFiles();
		ArrayList<String> filesList = new ArrayList<String>();
		for (File file : filesArray) {
			String path = file.getName();
			fileRoot = file.getParent();
			filesList.add(path);
		}
		// adpater for listview, will contain paths of files
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, filesList);
		setListAdapter(adapter);
		
		//registerForContextMenu(getListView()); //link listview to context menu
		
		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			   public boolean onItemLongClick (AdapterView<?> parent, View view, int position, long id) {
			     System.out.println("Long click");
			     getActivity().startActionMode(modeCallBack);
			     itemPosition = position;
			     view.setTag(itemPosition);
			     view.setSelected(true);
			     sel_lv = view;
			     return true;
			   }
			});
		lv = getListView();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Open file with available apps
		String filePath = l.getItemAtPosition(position).toString();
		File file = new File(fileRoot + "/" + filePath);
	    MimeTypeMap map = MimeTypeMap.getSingleton();
	    String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
	    String type = map.getMimeTypeFromExtension(ext);

	    if (type == null)
	        type = "*/*";

	    Intent intent = new Intent(Intent.ACTION_VIEW);
	    Uri data = Uri.fromFile(file);

	    intent.setDataAndType(data, type);

	    startActivity(intent);
	}
	
	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}*/
	
	private ActionMode.Callback modeCallBack = new ActionMode.Callback() {
		
		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.setTitle("Options");
			mode.getMenuInflater().inflate(R.menu.context_menu, menu);
			return true;
		}
		
		// Called each time the action mode is shown. Always called after onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		// Called when the user exits the action mode
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int id = item.getItemId();
			
			//AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			switch (id) {
			case R.id.delete: {
				// remove from device
				if(itemPosition != -1) {
					String selection = adapter.getItem(itemPosition);
					// remove from phone
					deleteVideo(selection);
					// remove from list and update
					adapter.remove(selection);
					adapter.notifyDataSetChanged();
				}
				mode.finish();
				return true;
			}
			case R.id.info: {
				sel_file_text = adapter.getItem(itemPosition);
				sharedpreferences = getActivity().getSharedPreferences("com.example.ffmpeg_trial", Context.MODE_PRIVATE);
				Editor editor = sharedpreferences.edit();
				editor.putString("file", fileRoot + "/" +sel_file_text);
				editor.commit();
				new MyDialogFragment().show(getFragmentManager(), "MyDialog");
				mode.finish();
				return true;
			}
			default:
				return false;
			}
		}
		
		// Called when the user selects a contextual menu item
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mode = null;
		}

	};
	
	private void deleteVideo(String path) {
		File file = new File(path);
		if(file.exists()) 
			file.delete();
	}
}