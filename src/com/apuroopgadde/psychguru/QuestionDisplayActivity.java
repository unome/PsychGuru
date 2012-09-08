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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

public class QuestionDisplayActivity extends Activity implements OnItemSelectedListener,OnClickListener{
	final String TAG="psychGuru";
	int questionId=0;
	int totalNoOfQues=0;
	int currQuestion=0;
	ArrayList<String> answeredQues;
	private String answered = "false";
	ArrayList<String> checked = new ArrayList<String>();
	DbHelper dbhelper;
	DisplayMetrics dm = getResources().getDisplayMetrics();
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	int REL_SWIPE_MIN_DISTANCE = (int)(SWIPE_MIN_DISTANCE * dm.densityDpi / 160.0f);
	int REL_SWIPE_MAX_OFF_PATH = (int)(SWIPE_MAX_OFF_PATH * dm.densityDpi / 160.0f);
	int REL_SWIPE_THRESHOLD_VELOCITY = (int)(SWIPE_THRESHOLD_VELOCITY * dm.densityDpi / 160.0f);
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_display);
		dbhelper = new DbHelper(this);
		Bundle extra = getIntent().getExtras();
		answeredQues=extra.getStringArrayList("answeredQues");
		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		context=this;
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
		sp_quesNo.setOnClickListener(this); 
		sp_quesNo.setOnTouchListener(gestureListener);
		TableLayout tl = (TableLayout) findViewById(R.id.tL_choiceTable);
		tl.setOnClickListener(this); 
		tl.setOnTouchListener(gestureListener);
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
			quesText.setText(sqlIt.getString(2));
			quesText.setOnClickListener(this); 
			quesText.setOnTouchListener(gestureListener);
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
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if(e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) 
				{
					nextQuestion();
				}  
				else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY)
				{
					prevQuestion();
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		private void prevQuestion() {
			if(currQuestion!=1)
			{
			final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences( QuestionDisplayActivity.this);
			int prevQuestionValue = wmbPreference.getInt("currentQuestion",1)-1;
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("currentQuestion",prevQuestionValue);
			editor.commit();
			Intent showQuestions = new Intent( QuestionDisplayActivity.this,QuestionDisplayActivity.class);
			showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
			startActivityForResult(showQuestions,0);
			}
		}

		private void nextQuestion() {
			if(currQuestion!=totalNoOfQues)
			{
				final SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences( QuestionDisplayActivity.this);
				int prevQuestionValue = wmbPreference.getInt("currentQuestion",1)-1;
				SharedPreferences.Editor editor = wmbPreference.edit();
				editor.putInt("currentQuestion",prevQuestionValue);
				editor.commit();
				Intent showQuestions = new Intent( QuestionDisplayActivity.this,QuestionDisplayActivity.class);
				showQuestions.putStringArrayListExtra("answeredQues",answeredQues);
				startActivityForResult(showQuestions,0);
			}
		}

	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
