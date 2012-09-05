package com.apuroopgadde.psychguru;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class MainList extends Activity
{
	final String TAG="psychGuru";
	String[] titles = {"History Taking","Questionnaire"};
	String[] infoTopics = {"a1","a2"};
	String[] quesTopics = {"b1","b2","b3"};
	final String[][] contents = {infoTopics,quesTopics};
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_page);
		ExpandableListView list = (ExpandableListView)findViewById(R.id.eL_mainList);
		SimplerExpandableListAdapter adapter = new SimplerExpandableListAdapter(this,
				titles, contents,list);
		list.setAdapter(adapter);

	}

}


class SimplerExpandableListAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private String[][] mContents;
	private String[] mTitles;
	ExpandableListView mList;
	int lastExpandedGroup=-1;
	public SimplerExpandableListAdapter(Context context, String[] titles, String[][] contents,ExpandableListView list) {
		super();
		if(titles.length != contents.length) {
			throw new IllegalArgumentException("Titles and Contents must be the same size.");
		}

		mContext = context;
		mContents = contents;
		mTitles = titles;
		mList=list;
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
		if (convertView == null) {
			LayoutInflater inflater =  (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.child_row, null);
		}

		TextView tvPlayerName = (TextView) convertView.findViewById(R.id.tvPlayerName);
		tvPlayerName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tvSubTopic=(TextView)v;
				tvSubTopic.setTextColor(Color.BLUE);
			}
		});
		tvPlayerName.setText(mContents[groupPosition][childPosition]);
		return convertView;
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
		if (convertView == null) {
			LayoutInflater inflater =  (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.group_row, null);
		}

		TextView tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
		tvGroupName.setText(mTitles[groupPosition]);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);

	}
	@Override
	public void onGroupExpanded(int groupPosition) {
		// TODO Auto-generated method stub
		if(groupPosition != lastExpandedGroup){
			mList.collapseGroup(lastExpandedGroup);
		}
			super.onGroupExpanded(groupPosition);
	        lastExpandedGroup = groupPosition;

		}


	}