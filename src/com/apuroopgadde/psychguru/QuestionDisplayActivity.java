package com.apuroopgadde.psychguru;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class QuestionDisplayActivity extends Activity{
	final String TAG="psychGuru";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_display);
		showContent();
	}

	private void showContent() {
		final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		String titleText=wmbPreference.getString("currentTopic","null");
		titleText=titleText+"->";
		titleText=titleText+wmbPreference.getString("currentSubTopic","null");
		Log.d(TAG,titleText);
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

	public void onClickHandler(View v)
	{
		if(v.getId()==R.id.iV_homeButton)
		{
			Intent showHome = new Intent(this,HomeActivity.class);
			startActivityForResult(showHome,0);
		}
	}


}
