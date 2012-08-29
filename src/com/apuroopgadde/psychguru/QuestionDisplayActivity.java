package com.apuroopgadde.psychguru;

import java.util.ArrayList;
import java.util.StringTokenizer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

public class QuestionDisplayActivity extends Activity implements OnItemSelectedListener{
	final String TAG="psychGuru";
	int questionId=0;
	int totalNoOfQues=0;
	int currQuestion=0;
	ArrayList<String> answeredQues;
	private String answered = "false";
	ArrayList<String> checked = new ArrayList<String>();
	DbHelper dbhelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_display);
		dbhelper = new DbHelper(this);
		Bundle extra = getIntent().getExtras();
		answeredQues=extra.getStringArrayList("answeredQues");
		showContent();
	}

	private void showContent() {
		final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		totalNoOfQues=wmbPreference.getInt("totalQuestions",0);
		currQuestion=wmbPreference.getInt("currentQuestion",1);
		Log.d(TAG,"currQues:"+currQuestion);
		String titleText=wmbPreference.getString("currentTopic","null");
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
		TableLayout tl = (TableLayout) findViewById(R.id.tL_choiceTable);
		tl.removeAllViews();
		Button doneButton=(Button)findViewById(R.id.button_checkAnswer);
		Button prevQuesButton=(Button)findViewById(R.id.button_prevQues);
		Button skipQuesButton=(Button)findViewById(R.id.button_skipQues);
		TextView scoreText=(TextView)findViewById(R.id.tV_scoreDisplay);
		scoreText.setText("Score:"+wmbPreference.getInt("currentScore",0)+"");
		//get all the questions with topicID;
		String questionQuery="select * from "+dbhelper.qTable + " where "+dbhelper.topicId+"="+
				wmbPreference.getInt("topicIdinDb",0)+"";
		Cursor sqlIt = db.rawQuery(questionQuery, null);
		if(sqlIt.moveToNext())
		{
			//move to current question by setting the offset
			sqlIt.move(currQuestion-1);
			questionId=sqlIt.getInt(0);
			TextView quesText = (TextView)findViewById(R.id.tV_quesDisplay);
			Log.d(TAG,"question:"+sqlIt.getString(2));
			quesText.setText(sqlIt.getString(2));
			answered=sqlIt.getString(3);
		}
		if(currQuestion==1)
			prevQuesButton.setVisibility(View.INVISIBLE);

		if(answered.equals("true"))
		{
			doneButton.setText("View Explanation");
			skipQuesButton.setText("Next Ques");
		}
		if(currQuestion==totalNoOfQues){
			skipQuesButton.setText("View Score");
		}
		//Get the option from optiontable with _id from qtable as the questionID
		String optionsQuery = "select * from "+dbhelper.oTable + " where "+dbhelper.qid+" =\" "
				+sqlIt.getInt(0)+"\"";
		sqlIt.close();
		Cursor optionsIt = db.rawQuery(optionsQuery,null);
		while(optionsIt.moveToNext())
		{
			TableRow tbrow = new TableRow(this);
			tbrow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT
					,LayoutParams.WRAP_CONTENT));
			CheckBox cBox = new CheckBox(this);
			cBox.setTag("cB" + optionsIt.getString(2));
			if(answered.equals("true"))
			{

				if(optionsIt.getString(3).equals("true"))
					cBox.setChecked(true);
			}
			cBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(answered.equals("true"))
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
		optionsIt.close();
		db.close();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		String selQuestion=parent.getItemAtPosition(position).toString();
		StringTokenizer st= new StringTokenizer(selQuestion);
		st.nextToken();
		int localCurrQuestion=Integer.parseInt(st.nextToken());
		if(currQuestion!=localCurrQuestion){
			currQuestion=localCurrQuestion;
			final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currentQuestion",currQuestion);
			editor.commit();
			Intent showQuestions = new Intent(this,QuestionDisplayActivity.class);
			showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
			startActivityForResult(showQuestions,0);
		}
	}

	public void onClickHandler(View v)
	{
		if(v.getId()==R.id.iV_homeButton)
		{
			MiscOperations.backToHome(this);
		}
		if(v.getId()==R.id.button_checkAnswer)
		{
			final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currentQuestionId", questionId);
			editor.commit();
			if(answered.equals("true"))
			{
				Intent showAnswer = new Intent(QuestionDisplayActivity.this,
						AnswerExplanationActivity.class);
				showAnswer.putExtra("alreadyAnswered",true);
				showAnswer.putStringArrayListExtra("answeredQues", answeredQues);
				startActivityForResult(showAnswer, 0);
			}
			else{
				if(checked.size()==0)
				{
					Toast.makeText(v.getContext(), "Please select atleast one option",
							Toast.LENGTH_LONG).show();
					return;
				}
				answeredQues.set(currQuestion-1,"true");
				Intent showAnswer = new Intent(QuestionDisplayActivity.this,
						AnswerExplanationActivity.class);
				showAnswer.putExtra("alreadyAnswered",false);
				showAnswer.putStringArrayListExtra("answers",checked);
				showAnswer.putStringArrayListExtra("answeredQues", answeredQues);
				startActivityForResult(showAnswer, 0);
			}
		}
		if(v.getId()==R.id.button_skipQues)
		{
			if(currQuestion==totalNoOfQues)
			{
				//start intent to view score
			}
			else
			{
				final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
				int nextQuestionValue = wmbPreference.getInt("currentQuestion",1)+1;
				SharedPreferences.Editor editor = wmbPreference.edit();
				editor.putInt("currentQuestion",nextQuestionValue);
				editor.commit();
				Intent showQuestions = new Intent(this,QuestionDisplayActivity.class);
				showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
				startActivityForResult(showQuestions,0);
			}

		}
		if(v.getId()==R.id.button_prevQues)
		{
			final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
			int prevQuestionValue = wmbPreference.getInt("currentQuestion",1)-1;
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currentQuestion",prevQuestionValue);
			editor.commit();
			Intent showQuestions = new Intent(this,QuestionDisplayActivity.class);
			showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
			startActivityForResult(showQuestions,0);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
