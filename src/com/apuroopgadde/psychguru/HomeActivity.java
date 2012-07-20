package com.apuroopgadde.psychguru;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class HomeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		showContent();

	}

	private void showContent() {
		RadioGroup rB_subTopic = (RadioGroup)findViewById(R.id.rB_subTopic);
		TextView tV_subTopic=(TextView)findViewById(R.id.tV_subTopic);
		rB_subTopic.setVisibility(View.INVISIBLE);
		tV_subTopic.setVisibility(View.INVISIBLE);
		Spinner sp_topics=(Spinner)findViewById(R.id.sp_topics);
		
	}




}
