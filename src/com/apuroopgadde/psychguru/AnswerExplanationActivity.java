package com.apuroopgadde.psychguru;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.ViewGroup;;

public class AnswerExplanationActivity extends Activity implements OnItemSelectedListener{
	protected static final String TAG = "psychguru";
	private int currQuestion;
	private int currQuestionId;
	private int totalNoOfQues;
	private int score=0;
	boolean alreadyAnswered=false;
	Button nextQues=null;
	TextView correctOrNot=null;
	TextView answerDetailsDisplay=null;
	TextView scoreText=null;
	DbHelper dbhelper;
	ArrayList<String> checked = new ArrayList<String>();
	ArrayList<String> answeredQues;
	SharedPreferences wmbPreference=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer_explanation);
		dbhelper = new DbHelper(this);
		Bundle answers = getIntent().getExtras();
		checked = answers.getStringArrayList("answers");
		answeredQues=answers.getStringArrayList("answeredQues");
		alreadyAnswered=answers.getBoolean("alreadyAnswered");
		correctOrNot=(TextView)findViewById(R.id.tV_correctOrNot);
		answerDetailsDisplay=(TextView)findViewById(R.id.tV_Explanation);
		showContent();
	}

	private void showContent() {
		wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		currQuestion=wmbPreference.getInt("currentQuestion",1);
		currQuestionId=wmbPreference.getInt("currentQuestionId",1);
		String titleText=wmbPreference.getString("currentTopic","null");
		totalNoOfQues=wmbPreference.getInt("totalQuestions",1);
		if(!wmbPreference.getString("currentSubTopic", "none").equals("none"))
		{
			titleText=titleText+"->";
			titleText=titleText+wmbPreference.getString("currentSubTopic","null");
		}
		this.setTitle(titleText);
		Spinner sp_quesNo=(Spinner)findViewById(R.id.sp_questionNo);
		final ArrayList<String> questionList=new ArrayList<String>();
		for(int i=1;i<=totalNoOfQues;++i)
		{
			questionList.add("Question "+i);
		}
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				R.layout.textview_for_spinner, questionList)
				{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View v = convertView;
				if (v == null) {
					Context mContext = this.getContext();
					LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.textview_for_spinner, null);
				} 
				TextView tv=(TextView) v.findViewById(R.id.spinnerTarget);
				tv.setText(questionList.get(position));
				if(answeredQues.get(position).equals("true"))
					tv.setTextColor(Color.GRAY);
				else
					tv.setTextColor(Color.BLACK);
				return v;
			}
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent){
				View v = convertView;
				if (v == null) {
					Context mContext = this.getContext();
					LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.textview_for_spinner, null);
				} 
				TextView tv=(TextView) v.findViewById(R.id.spinnerTarget);
				tv.setText(questionList.get(position));
				if(answeredQues.get(position).equals("true"))
					tv.setTextColor(Color.GRAY);
				else
					tv.setTextColor(Color.BLACK);
				return v;
			}

				}
		;
		sp_quesNo.setAdapter(spinnerAdapter);
		sp_quesNo.setSelection(currQuestion-1); //Use this to move the value in spinner
		sp_quesNo.setOnItemSelectedListener(this);
		if(currQuestion==totalNoOfQues)
		{
			nextQues = (Button)findViewById(R.id.button_nextQues);
			nextQues.setText("View Score");
		}
		scoreText=(TextView)findViewById(R.id.tV_scoreDisplay);
		scoreText.setText("Score:"+wmbPreference.getInt("currentScore",0));
		String explQuery="select * from "+dbhelper.aTable+" where "+dbhelper.qid+"="+currQuestionId;
		Cursor explIt=db.rawQuery(explQuery, null);
		if(explIt.moveToNext())
		{
			answerDetailsDisplay.setText(explIt.getString(2));
		}
		explIt.close();
		checkAnswer(db);
	}
	private void checkAnswer(SQLiteDatabase db) {
		if(!alreadyAnswered)
		{
			String answersQuery = "select * from "+dbhelper.oTable+" where "+dbhelper.qid+" = "+
					wmbPreference.getInt("currentQuestion", 1)+" AND "+dbhelper.correct+" = 'true';";
			Cursor answersIt = db.rawQuery(answersQuery, null);
			while(answersIt.moveToNext())
			{
				if(checked.contains(answersIt.getString(2)))
				{
					++score;
				}
			}

			if(answersIt.getCount()==checked.size()&&checked.size()==score)
			{
				correctOrNot.setText("You've got the correct answer");
			}
			else if(checked.size()!=score&&score>0)
			{
				correctOrNot.setText("Your answer is partially correct");
			}
			else
			{
				correctOrNot.setText("Sorry you've got the wrong answer");
			}
			answersIt.close();
			int updatedScore=wmbPreference.getInt("currentScore",0)+score;
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currentScore", updatedScore);
			scoreText.setText("Score:"+updatedScore);
			editor.commit();
			//Set the question to answered
			String setQuesAnswered="update "+dbhelper.qTable+" set "+dbhelper.quesAnswered+"='true' where "+
					dbhelper.rowId+" = "+wmbPreference.getInt("currentQuestionId", 1)+";";
			db.execSQL(setQuesAnswered);
			db.close();
		}
		else
		{
			correctOrNot.setText("Explanation to the answer is:");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MiscOperations.handleMenuItem(item, this);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		String selQuestion=parent.getItemAtPosition(position).toString();
		StringTokenizer st= new StringTokenizer(selQuestion);
		st.nextToken();
		int localCurrQuestion=Integer.parseInt(st.nextToken());
		if(currQuestion!=localCurrQuestion){
			currQuestion=localCurrQuestion;
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currentQuestion",currQuestion);
			editor.putInt("currentQuestionId",currQuestionId);
			editor.commit();
			Intent showQuestions = new Intent(this,QuestionDisplayActivity.class);
			showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
			startActivityForResult(showQuestions,0);
		}
	}

	public void onClickHandler(View v)
	{
		if(v.getId()==R.id.button_viewQues)
		{
			Intent showQuestions = new Intent(this,QuestionDisplayActivity.class);
			showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
			startActivityForResult(showQuestions,0);
		}
		if(v.getId()==R.id.button_nextQues)
		{
			if(currQuestion!=totalNoOfQues)
			{
				int nextQuestion=wmbPreference.getInt("currentQuestion",1)+1;
				SharedPreferences.Editor editor = wmbPreference.edit();
				editor.putInt("currentQuestion", nextQuestion);
				editor.commit();
				Intent showQuestions = new Intent(this,QuestionDisplayActivity.class);
				showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
				startActivityForResult(showQuestions,0);
			}
			else
			{
				//Display score
			}
		}
		if(v.getId()==R.id.iV_homeButton)
		{
			MiscOperations.backToHome(this);
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
