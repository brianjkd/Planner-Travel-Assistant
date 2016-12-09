package team1028.plannertravelassistant;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Class to interface with ExpandableListView
 * Code from http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 * Created by Maddy on 12/9/2016.
 */
class ExpandableListAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> listDataHeader; // Header titles
	// Child data in format of header title, child title
	private HashMap<String, List<String>> listDataChild;

	// Basic constructor
	public ExpandableListAdapter(Context context, List<String> listDataHeader,
	                             HashMap<String, List<String>> listDataChild) {
		this.context = context;
		this.listDataHeader = listDataHeader;
		this.listDataChild = listDataChild;
	}

	// Get child
	@Override
	public Object getChild(int groupPos, int childPos) {
		return this.listDataChild.get(this.listDataHeader.get(groupPos)).get(childPos);
	}

	@Override
	public long getChildId(int groupPos, int childPos) {
		return childPos;
	}

	@Override
	public View getChildView(int groupPos, final int childPos, boolean isLastChild,
	                         View convertView, ViewGroup parent) {
		final String childText = (String)getChild(groupPos, childPos);

		// Check for null input
		if (convertView == null) {
			LayoutInflater infalInflator = (LayoutInflater)this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflator.inflate(R.layout.list_group, null);
		}

		// Update view
		TextView txtListChild = (TextView)convertView.findViewById(R.id.lblListItem);
		txtListChild.setText(childText);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPos) {
		return this.listDataChild.get(this.listDataHeader.get(groupPos)).size();
	}

	@Override
	public Object getGroup(int groupPos) {
		return this.listDataHeader.get(groupPos);
	}

	@Override
	public int getGroupCount() {
		return this.listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPos) {
		return groupPos;
	}

	@Override
	public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {
		String headerTitle = (String)getGroup(groupPos);

		// Check for null input
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater)this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		TextView lblListHeader = (TextView)convertView
				.findViewById(R.id.lblListHeader);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPos, int childPos) {
		return true;
	}
}
