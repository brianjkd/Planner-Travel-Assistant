package team1028.plannertravelassistant;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Class to interface with ExpandableListView
 * Code from http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 */
class ExpandableListAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> listDataHeader; // Header titles
	// Child data in format of header title, child title
	private HashMap<String, List<String>> listDataChild;

	// Basic constructor
	public ExpandableListAdapter(Context context) {
		this.context = context;
		this.listDataHeader = new ArrayList<String>();
		this.listDataChild = new HashMap<String, List<String>>();
	}

	// Multi-input constructor
	public ExpandableListAdapter(Context context, List<String> listDataHeader,
	                             HashMap<String, List<String>> listDataChild) {
		this.context = context;
		this.listDataHeader = listDataHeader;
		this.listDataChild = listDataChild;
	}

	// Add group and return index added at TODO check for duplicates!
	public int addGroup(String name) {
		this.listDataHeader.add(name);
		return this.listDataHeader.indexOf(name);
	}

	// Add Strings to groups
	public void addChildren(int groupPos, List<String> children) {

		//get the group
		String key = this.listDataHeader.get(groupPos);
		this.listDataChild.put(key,children);
	}

	// Get event data
	@Override
	public Object getChild(int groupPos, int childPos) {
		return this.listDataChild.get(this.listDataHeader.get(groupPos)).get(childPos);
	}

	// Get ID for event data
	@Override
	public long getChildId(int groupPos, int childPos) {
		return childPos;
	}

	// Create view for event data
	@Override
	public View getChildView(int groupPos, final int childPos, boolean isLastChild,
	                         View convertView, ViewGroup parent) {
		final String childText = (String)getChild(groupPos, childPos);

		// Check for null input
		if (convertView == null) {
			LayoutInflater infalInflator = (LayoutInflater)this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflator.inflate(R.layout.list_item, null);
		}

		// Update view
		TextView txtListChild = (TextView)convertView.findViewById(R.id.lblListItem);
		if (childText != null && txtListChild != null) {
			txtListChild.setText(childText);
		}
		return convertView;
	}

	// Count details of event
	@Override
	public int getChildrenCount(int groupPos) {
		List<String> child = this.listDataChild.get(this.listDataHeader.get(groupPos));
		if (child != null) {
			return child.size();
		} else {
			return 0;
		}
	}

	// Get event from index
	@Override
	public Object getGroup(int groupPos) {
		return this.listDataHeader.get(groupPos);
	}

	// Count events
	@Override
	public int getGroupCount() {
		return this.listDataHeader.size();
	}

	// Get ID of the given event
	@Override
	public long getGroupId(int groupPos) {
		return groupPos;
	}

	// Create view for event
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

	// IDs of events are not stable
	@Override
	public boolean hasStableIds() {
		return false;
	}

	// Children can be selected
	@Override
	public boolean isChildSelectable(int groupPos, int childPos) {
		return true;
	}
}
