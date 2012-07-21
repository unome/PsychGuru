package com.apuroopgadde.psychguru;

import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
	String selSubTopic=null;
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
			editor.putInt("currentTopic", -1);
			editor.putInt("currentSubTopic",-1);
			editor.putInt("currentScore", 0);
			editor.commit();
		}
		showContent();

	}

	private void showContent() {
		
		rG_subTopic = (RadioGroup)findViewById(R.id.rB_subTopic);
		tV_subTopic=(TextView)findViewById(R.id.tV_subTopic);
		rG_subTopic.setVisibility(View.INVISIBLE);
		tV_subTopic.setVisibility(View.INVISIBLE);
		Spinner sp_topics=(Spinner)findViewById(R.id.sp_topics);
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
	}

	public void onClickHandler(View v)
	{
		if(v.getId()==R.id.button_ok)
		{
			if(selTopic.equals("Click to select"))
			{
				Toast.makeText(v.getContext(), "Please select a topic",
						Toast.LENGTH_LONG).show();
			}
			if(!excTopics.contains(selTopic)&&selSubTopic==null)
			{
				Toast.makeText(v.getContext(), "Please select a sub topic",
						Toast.LENGTH_LONG).show();
			}
		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}




}
