package org.skyight1.neny.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

		public int getCount() {
			return listOfResourceIds.size();
		}

		public Object getItem(int position) {
			return listOfResourceIds.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

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
					listOfSelectedNeighborhoods.set(position, !listOfSelectedNeighborhoods.get(position));
					imageView.setAlpha(listOfSelectedNeighborhoods.get(position) ? 255 : 64);
				}
			});
			imageView.setAlpha(listOfSelectedNeighborhoods.get(position) ? 255 : 64);
			imageView.setImageResource(listOfResourceIds.get(position));
			return imageView;
		}
	}

	private List<Boolean> listOfSelectedNeighborhoods = new ArrayList<Boolean>();

	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);

		setContentView(R.layout.neighborhoods_view);

		final GridView grid = (GridView) findViewById(R.id.neighbourhoodGrid);
		final List<Integer> neighborhoodImageResources =
				Arrays.asList(R.drawable.chelsea, R.drawable.east_harlem, R.drawable.harlem, R.drawable.gramercy, R.drawable.inwood, R.drawable.lower_eastside, R.drawable.upper_eastside, R.drawable.uws, R.drawable.wall_st);
		for (final int dummy : neighborhoodImageResources) {
			listOfSelectedNeighborhoods.add(false);
		}
		grid.setAdapter(new ImageAdapter(this, neighborhoodImageResources, listOfSelectedNeighborhoods));
	}
}
