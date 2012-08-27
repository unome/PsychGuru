package com.apuroopgadde.psychguru;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MiscOperations {
	
	public static void handleMenuItem(MenuItem item,Context context)
	{
		final Context localContext=context;	
		switch(item.getItemId())
		{
		case 1:
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.about_dialog);
			dialog.setTitle("About");
			final Button next_button=(Button)dialog.findViewById(R.id.button_next);
			next_button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(next_button.getText().equals("Next"))
					{
						TextView tv_about=(TextView)dialog.findViewById(R.id.tV_about);
						tv_about.setText(localContext.getString(R.string.about_app));
						next_button.setText("Done");
					}
					else if(next_button.getText().equals("Done"))
					{
						dialog.dismiss();
					}
				}
			});
			dialog.show();
		case 2:
			//show settings
		}
	}
	
	public static void backToHome(Context context)
	{
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(context);
		DbHelper dbhelper=new DbHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String setQuesAnswered="update "+dbhelper.topicsTable+" set "+dbhelper.topicScore+"="+
				wmbPreference.getInt("currentScore",1)+","+dbhelper.currentQues+"="+
				wmbPreference.getInt("currentQuestion",1)+" where "+
				dbhelper.rowId+" = "+wmbPreference.getInt("topicIdinDb", 1)+";";
		db.execSQL(setQuesAnswered);
		db.close();
		Intent homeIntent=new Intent(context,HomeActivity.class);
		context.startActivity(homeIntent);
		//update score and currentques
		
	}
}
