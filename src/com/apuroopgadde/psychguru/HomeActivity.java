package com.apuroopgadde.psychguru;

import java.util.ArrayList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnItemSelectedListener{
	final String TAG="psychGuru";
	ArrayList<String> excTopics = new ArrayList<String>();
	RadioGroup rG_subTopic;
	TextView tV_subTopic;
	String selTopic=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
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
			editor.putInt("currQuestion",1);
			editor.putString("currentTopic", "none");
			editor.putString("currentSubTopic","Diagnostic Considerations");
			editor.putInt("currentScore", 0);
			editor.commit();
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
				editor.putString("currentSubTopic","Diagnostic Considerations");
				editor.commit();
			}
		});
		rB_mgmt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rB_diagCons.setChecked(false);
				SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(v.getContext());
				SharedPreferences.Editor editor = wmbPreference.edit();
				editor.putString("currentSubTopic","Management");
				editor.commit();
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
			tV_subTopic.setVisibility(View.VISIBLE);
			rG_subTopic.setVisibility(View.VISIBLE);
		}
		else
		{
			tV_subTopic.setVisibility(View.INVISIBLE);
			rG_subTopic.setVisibility(View.INVISIBLE);
		}
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(view.getContext());
		SharedPreferences.Editor editor = wmbPreference.edit();		
		editor.putString("currentTopic",selTopic);
		editor.commit();
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
				Intent showQuestions = new Intent(this,QuestionDisplayActivity.class);
				startActivityForResult(showQuestions,0);
			}

			//New activity showing questions
		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}




}
