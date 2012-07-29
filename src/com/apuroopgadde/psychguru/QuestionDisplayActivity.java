package com.apuroopgadde.psychguru;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class QuestionDisplayActivity extends Activity{
	final String TAG="psychGuru";
	int questionId=0;
	private String answered = "false";
	ArrayList<String> checked = new ArrayList<String>();
	DbHelper dbhelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_display);
		dbhelper = new DbHelper(this);
		try {
			dbhelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		//sp_quesNo.setSelection(1); Use this to move the value in spinner
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		TableLayout tl = (TableLayout) findViewById(R.id.tL_choiceTable);
		tl.removeAllViews();
		Button doneButton=(Button)findViewById(R.id.button_checkAnswer);
		Button prevQuesButton=(Button)findViewById(R.id.button_prevQues);
		Button skipQuesButton=(Button)findViewById(R.id.button_skipQues);
		TextView scoreText=(TextView)findViewById(R.id.tV_scoreDisplay);
		scoreText.setText("Score:"+wmbPreference.getInt("currentScore",0));
		String question = "select * from "+dbhelper.qTable + " where "+dbhelper.rowId +" = "
				+wmbPreference.getInt("currQuestion", 1)+";";
		Cursor sqlIt = db.rawQuery(question, null);
		if(sqlIt.moveToNext())
		{
			TextView quesText = (TextView)findViewById(R.id.tV_quesDisplay);
			questionId = sqlIt.getInt(0);
			quesText.setText(questionId+":"+sqlIt.getString(1));
			answered=sqlIt.getString(2);
		}
		if(questionId==1)
			prevQuesButton.setVisibility(View.INVISIBLE);

		if(answered.equals("True"))
		{
			doneButton.setText("View Explanation");
			skipQuesButton.setText("Next Ques");
		}
		if(questionId==7)
			skipQuesButton.setText("View Score");
		sqlIt.close();
		String optionsQuery = "select * from "+dbhelper.oTable + " where "+dbhelper.qid+" = "
				+questionId;
		Cursor optionsIt = db.rawQuery(optionsQuery,null);
		while(optionsIt.moveToNext())
		{
			TableRow tbrow = new TableRow(this);
			tbrow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT
					,LayoutParams.WRAP_CONTENT));
			CheckBox cBox = new CheckBox(this);
			cBox.setTag("cB" + optionsIt.getString(2));
			if(answered.equals("True"))
			{

				if(optionsIt.getString(3).equals("True"))
					cBox.setChecked(true);
			}
			cBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(answered.equals("True"))
						return;
					TableRow currRow = (TableRow) v.getParent(); // getParent returns the parent
					ArrayList<View> allViews = new ArrayList<View>();
					allViews = currRow.getTouchables();
					TextView optionName = (TextView) (allViews.get(1));
					//Log.d(TAG,"option is"+optionName.getText());
					if(((CheckBox)v).isChecked())
						checked.add((String) optionName.getText());
					else
						checked.remove(checked.indexOf((String)optionName.getText()));
				}
			});
			tbrow.addView(cBox);
			TextView kName = new TextView(this);
			kName.setText(optionsIt.getString(2));
			kName.setTextSize(TypedValue.COMPLEX_UNIT_PX,this.getResources().getDimension(R.dimen.textSize));
			kName.setClickable(true);
			tbrow.addView(kName);
			tl.addView(tbrow);
		}
		db.close();
	}

	public void onClickHandler(View v)
	{
		if(v.getId()==R.id.iV_homeButton)
		{
			Intent showHome = new Intent(this,HomeActivity.class);
			startActivityForResult(showHome,0);
		}
		if(v.getId()==R.id.button_checkAnswer)
		{
			Intent showAnswer = new Intent(this,AnswerExplanationActivity.class);
			startActivityForResult(showAnswer,0);
		}
		if(v.getId()==R.id.button_skipQues)
		{

			final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
			int nextQuestionValue = wmbPreference.getInt("currQuestion",1)+1;
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currQuestion",nextQuestionValue);
			editor.commit();
			onCreate(null);

		}
		if(v.getId()==R.id.button_prevQues)
		{
			final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
			int prevQuestionValue = wmbPreference.getInt("currQuestion",1)-1;
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currQuestion",prevQuestionValue);
			editor.commit();
			onCreate(null);
		}
	}


}
