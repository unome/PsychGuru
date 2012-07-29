package com.apuroopgadde.psychguru;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AnswerExplanationActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer_explanation);
		showContent();
	}

	private void showContent() {
		final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		String titleText=wmbPreference.getString("currentTopic","null");
		titleText=titleText+"->";
		titleText=titleText+wmbPreference.getString("currentSubTopic","null");
		this.setTitle(titleText);
		Spinner sp_quesNo=(Spinner)findViewById(R.id.sp_questionNo);
		ArrayList<String> questionList=new ArrayList<String>();
		for(int i=1;i<=20;++i)
		{
			questionList.add("Question "+i);
		}
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				R.layout.textview_for_spinner, questionList);
		sp_quesNo.setAdapter(spinnerAdapter);
		
	}

}
