package com.example.ffmpeg_trial;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyDialogFragment extends DialogFragment{
	SharedPreferences sharedpreferences;
	TextView name, path, size, lastM;
    public MyDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_dialog, container);
        getDialog().setTitle("Properties");
        
        sharedpreferences = getActivity().getSharedPreferences("com.example.ffmpeg_trial", 
				Context.MODE_PRIVATE);
        String filePath = "";
        File file;
		if (sharedpreferences.contains("file")) {
			filePath = sharedpreferences.getString("file", "");
			file = new File(filePath);
			
			// GetTextviews
			name = (TextView) view.findViewById(R.id.name);
			path = (TextView) view.findViewById(R.id.path);
			size = (TextView) view.findViewById(R.id.size);
			lastM = (TextView) view.findViewById(R.id.lastM);
			
			// Set text
			name.append(file.getName());
			path.append(file.getParent());
		
			long sizeOf = file.length()/1024/1024; // convert from B to MB
			size.append(String.valueOf(sizeOf ) + " MB");

			Date lastModDate = new Date(file.lastModified());
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			lastM.append(String.valueOf(formatter.format(lastModDate)));
			
			
			
		}
		
		
		
		

        return view;
    }
}