package com.apuroopgadde.psychguru;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class MainList extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_page);
		ExpandableListView list = (ExpandableListView)findViewById(R.id.eL_mainList);
		list.setGroupIndicator(null);
		list.setChildIndicator(null);
		String[] titles = {"History Taking","Questionnaire"};
		String[] infoTopics = {"a1","a2"};
		String[] quesTopics = {"b1","b2","b3"};
		String[][] contents = {infoTopics,quesTopics};
		SimplerExpandableListAdapter adapter = new SimplerExpandableListAdapter(this,
				titles, contents);
		list.setAdapter(adapter);

	}
}


class SimplerExpandableListAdapter extends BaseExpandableListAdapter {
 private Context mContext;
 private String[][] mContents;
 private String[] mTitles;
 
 public SimplerExpandableListAdapter(Context context, String[] titles, String[][] contents) {
   super();
   if(titles.length != contents.length) {
     throw new IllegalArgumentException("Titles and Contents must be the same size.");
   }
   
   mContext = context;
   mContents = contents;
   mTitles = titles;
 }
 @Override
 public String getChild(int groupPosition, int childPosition) {
   return mContents[groupPosition][childPosition];
 }
 @Override
 public long getChildId(int groupPosition, int childPosition) {
   return 0;
 }
 @Override
 public View getChildView(int groupPosition, int childPosition,
     boolean isLastChild, View convertView, ViewGroup parent) {
   TextView row = (TextView)convertView;
   if(row == null) {
     row = new TextView(mContext);
   }
   row.setText(mContents[groupPosition][childPosition]);
   return row;
 }
 @Override
 public int getChildrenCount(int groupPosition) {
   return mContents[groupPosition].length;
 }
 @Override
 public String[] getGroup(int groupPosition) {
   return mContents[groupPosition];
 }
 @Override
 public int getGroupCount() {
   return mContents.length;
 }
 @Override
 public long getGroupId(int groupPosition) {
   return 0;
 }
 @Override
 public View getGroupView(int groupPosition, boolean isExpanded,
     View convertView, ViewGroup parent) {
	   TextView row = (TextView)convertView;
	   if(row == null) {
	     row = new TextView(mContext);
	   }
	   row.setText(mTitles[groupPosition]);
	   return row;
 }

 @Override
 public boolean hasStableIds() {
   return false;
 }

 @Override
 public boolean isChildSelectable(int groupPosition, int childPosition) {
   return true;
 }

}