package org.skyight1.neny.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class SelectNeighborhoodsActivity extends Activity {
	public class ImageAdapter extends BaseAdapter {

		private final Context context;

		private final List<Integer> listOfResourceIds;

		private final List<Boolean> listOfSelectedNeighborhoods;

		public ImageAdapter(final Context aContext, final List<Integer> aListOfResourceIds, List<Boolean> aListOfSelectedNeighborhoods) {
			context = aContext;
			listOfResourceIds = aListOfResourceIds;
			listOfSelectedNeighborhoods = aListOfSelectedNeighborhoods;
		}

		@Override
		public int getCount() {
			return listOfResourceIds.size();
		}

		@Override
		public Object getItem(int position) {
			return listOfResourceIds.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(context);
				imageView.setAdjustViewBounds(true);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View aV) {
					final boolean newState = !listOfSelectedNeighborhoods.get(position);
					listOfSelectedNeighborhoods.set(position, newState);
					final Editor edit = preferences.edit();
					edit.putBoolean(String.valueOf(position), newState);
					edit.commit();
					imageView.setAlpha(listOfSelectedNeighborhoods.get(position) ? 255 : 64);
				}
			});
			imageView.setAlpha(listOfSelectedNeighborhoods.get(position) ? 255 : 64);
			imageView.setImageResource(listOfResourceIds.get(position));
			return imageView;
		}
	}

	private List<Boolean> listOfSelectedNeighborhoods = new ArrayList<Boolean>();

	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.neighborhoods_view);

		final GridView grid = (GridView) findViewById(R.id.neighbourhoodGrid);
		final List<Integer> neighborhoodImageResources =
				Arrays.asList(R.drawable.chelsea, R.drawable.east_harlem, R.drawable.harlem, R.drawable.gramercy, R.drawable.inwood, R.drawable.lower_eastside, R.drawable.upper_eastside, R.drawable.uws, R.drawable.wall_st);
		for (final int dummy : neighborhoodImageResources) {
			listOfSelectedNeighborhoods.add(false);
		}

		// load preferences
		listOfSelectedNeighborhoods = new ArrayList<Boolean>();
		for (int i = 0; i < neighborhoodImageResources.size(); i++) {
			listOfSelectedNeighborhoods.add(preferences.getBoolean(String.valueOf(i), true));
		}

		grid.setAdapter(new ImageAdapter(this, neighborhoodImageResources, listOfSelectedNeighborhoods));
	}
}
