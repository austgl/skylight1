package skylight1.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skylight1.ui.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FlickTestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Gallery gallery = (Gallery) getLayoutInflater().inflate(R.layout.test, null);

		List<Map<String, String>> values = new ArrayList<Map<String, String>>();
		Map<String, String> row1 = new HashMap<String, String>();
		row1.put("col1", "hello");
		values.add(row1);
		Map<String, String> row2 = new HashMap<String, String>();
		row2.put("col1", "world");
		values.add(row2);
		Map<String, String> row3 = new HashMap<String, String>();
		row3.put("col1", "here I am");
		values.add(row3);

		String[] colNames = { "col1" };
		int[] colsIds = { R.id.TextView01 };

		final SimpleAdapter simpleAdapter = new SimpleAdapter(this, values, R.layout.inner_view, colNames, colsIds) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final View view = super.getView(position, convertView, parent);
				final int backgroundDrawable;
				if (getCount() == 1) {
					backgroundDrawable = R.drawable.only_bg;
				} else if (position == 0) {
					backgroundDrawable = R.drawable.left_most_bg;
				} else if (position == getCount() - 1) {
					backgroundDrawable = R.drawable.right_most_bg;
				} else {
					backgroundDrawable = R.drawable.center_bg;
				}
				view.findViewById(R.id.DecoratedLayout).setBackgroundResource(backgroundDrawable);
				return view;
			}
		};

		simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				TextView textView = (TextView) view.findViewById(R.id.TextView01);
				textView.setText((String) data);
				return true;
			}
		});

		gallery.setAdapter(simpleAdapter);

		setContentView(gallery);
	}
}
