package com.apuroopgadde.psychguru;

import java.io.IOException;
import java.util.ArrayList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnItemSelectedListener{
	final String TAG="psychGuru";
	DbHelper dbhelper;
	//Array list containing the topics that do not have subTopics
	ArrayList<String> excTopics = new ArrayList<String>();
	RadioGroup rG_subTopic;
	TextView tV_subTopic;
	String selTopic=null;
	String selSubTopic=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		dbhelper = new DbHelper(this);
		try {
			dbhelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		excTopics.add("Psychopharmacology");
		excTopics.add("Psychotherapy");
		excTopics.add("Aids during clerkship");
		excTopics.add("Click to select");
		final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
		if (isFirstRun)
		{
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putBoolean("FIRSTRUN", false);
			editor.putInt("currentQuestion",1);
			editor.putInt("currentQuestionId",1);
			editor.putInt("topicIdinDb", 0);
			editor.putInt("totalQuestions",0);
			editor.putString("currentTopic", "none");
			editor.putString("currentSubTopic","none");
			editor.putInt("currentScore", 0);
			editor.commit();
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.about_dialog);
			dialog.setTitle("About");
			final Button next_button=(Button)dialog.findViewById(R.id.button_next);
			next_button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(next_button.getText().equals("Next"))
					{
						TextView tv_about=(TextView)dialog.findViewById(R.id.tV_about);
						tv_about.setText(getString(R.string.about_app));
						next_button.setText("Done");
					}
					else if(next_button.getText().equals("Done"))
					{
						dialog.dismiss();
					}
				}
			});
			dialog.show();
		}
		showContent();

	}

	private void showContent() {
		rG_subTopic = (RadioGroup)findViewById(R.id.rB_subTopic);
		final RadioButton rB_diagCons=(RadioButton)findViewById(R.id.rB_diagCons);
		final RadioButton rB_mgmt=(RadioButton)findViewById(R.id.rB_management);
		rB_diagCons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rB_mgmt.setChecked(false);
				SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(v.getContext());
				SharedPreferences.Editor editor = wmbPreference.edit();
				selSubTopic="Diagnostic considerations";

			}
		});
		rB_mgmt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rB_diagCons.setChecked(false);
				SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(v.getContext());
				SharedPreferences.Editor editor = wmbPreference.edit();
				selSubTopic="Management";
			}
		});
		tV_subTopic=(TextView)findViewById(R.id.tV_subTopic);
		rG_subTopic.setVisibility(View.INVISIBLE);
		tV_subTopic.setVisibility(View.INVISIBLE);
		Spinner sp_topics=(Spinner)findViewById(R.id.sp_topics);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.topics,R.layout.textview_for_spinner);	
		sp_topics.setAdapter(adapter);
		sp_topics.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		selTopic = parent.getItemAtPosition(position).toString();
		if(!excTopics.contains(selTopic))
		{
			selSubTopic="Diagnostic considerations";
			tV_subTopic.setVisibility(View.VISIBLE);
			rG_subTopic.setVisibility(View.VISIBLE);
		}
		else
		{
			selSubTopic="none";
			tV_subTopic.setVisibility(View.INVISIBLE);
			rG_subTopic.setVisibility(View.INVISIBLE);
		}
		resetRadioButtons();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void resetRadioButtons() {
		RadioButton rB_diagCons=(RadioButton)findViewById(R.id.rB_diagCons);
		RadioButton rB_mgmt=(RadioButton)findViewById(R.id.rB_management);
		rB_diagCons.setChecked(true);
		rB_mgmt.setChecked(false);
	}

	public void onClickHandler(View v)
	{
		if(v.getId()==R.id.button_ok)
		{
			if(selTopic.equals("Click to select"))
			{
				Toast.makeText(v.getContext(), "Please select a topic",
						Toast.LENGTH_LONG).show();
				return;
			}
			else{
				SQLiteDatabase db = dbhelper.getReadableDatabase();
				String sqlQuery= "select * from "+dbhelper.topicsTable+" where "+dbhelper.topic+"=\""+selTopic+"\" AND "+
						dbhelper.subTopic+"=\""+selSubTopic+"\"";
				Cursor sqlCur=db.rawQuery(sqlQuery, null);
				if(sqlCur.moveToNext())
				{
					SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
					SharedPreferences.Editor editor = wmbPreference.edit();	
					editor.putString("currentSubTopic",selSubTopic);
					editor.putString("currentTopic",selTopic);
					editor.putInt("topicIdinDb",sqlCur.getInt(0));
					editor.putInt("totalQuestions",sqlCur.getInt(3));
					editor.putInt("currentScore",sqlCur.getInt(4));
					editor.putInt("currentQuestion",sqlCur.getInt(5));
					editor.commit();
					sqlQuery="select * from "+dbhelper.qTable+" where "+dbhelper.topicId+"=\""+sqlCur.getInt(0)+"\"";
					sqlCur.close();
					sqlCur=db.rawQuery(sqlQuery, null);
					ArrayList<String> answeredQues= new ArrayList<String>();
					while(sqlCur.moveToNext())
					{
						if(sqlCur.getString(3).equals("false"))
							answeredQues.add("false");
						else
							answeredQues.add("true");
					}
					//Create an array to store the answered question numbers
					Intent showQuestions = new Intent(this,QuestionDisplayActivity.class);
					showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
					startActivityForResult(showQuestions,0);
				}

			}
		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1,1,Menu.FIRST,"About");
		menu.add(1,2,Menu.FIRST+1,"Settings");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MiscOperations.handleMenuItem(item, this);
		return super.onOptionsItemSelected(item);
	}
}
