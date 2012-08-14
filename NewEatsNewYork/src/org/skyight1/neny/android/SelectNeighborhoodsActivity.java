package org.skyight1.neny.android;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class SelectNeighborhoodsActivity extends Activity {
	public class ImageAdapter extends BaseAdapter {

		private final Context context;

		private final List<Integer> listOfResourceIds;

		public ImageAdapter(final Context aContext, final List<Integer> aListOfResourceIds) {
			context = aContext;
			listOfResourceIds = aListOfResourceIds;
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
//				imageView.setLayoutParams(new GridView.LayoutParams(200, 150));
//				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//				imageView.setPadding(0, 0, 0, 0);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(listOfResourceIds.get(position));
			return imageView;
		}
	}

	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);

		setContentView(R.layout.neighborhoods_view);

		final GridView grid = (GridView) findViewById(R.id.neighbourhoodGrid);
		grid.setAdapter(new ImageAdapter(this, Arrays
				.asList(R.drawable.chelsea, R.drawable.east_harlem, R.drawable.harlem, R.drawable.gramercy, R.drawable.inwood, R.drawable.lower_eastside, R.drawable.upper_eastside, R.drawable.uws, R.drawable.wall_st)));
	}
}
