package org.skylight1.neny.android;

import java.util.List;

import org.skylight1.neny.android.database.dao.CuisinePreferences;
import org.skylight1.neny.android.database.dao.PreferencesDao;
import org.skylight1.neny.android.database.model.Cuisine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CuisineAdapter extends BaseAdapter {

	private Context mContext;
	private final List<Integer> listOfActiveResourceIds;

	private List<Integer> listOfInactiveResourceIds;

	private final List<Boolean> listOfSelectedCuisines;
	private final PreferencesDao preferencesDao;
	@Override
	public int getCount() {
		return Cuisine.values().length;
	}

	@Override
	public Object getItem(int position) {
		return (Object)listOfActiveResourceIds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		
		View cellView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			cellView = inflater.inflate(R.layout.image_text,parent,false);
			
		} else {
			cellView =  convertView;
		}
		TextView imageText = (TextView)cellView.findViewById(R.id.cuisine_image_text);
		imageText.setText(SelectCuisinesActivity.mapImagePositionsToEnums(position).getLabel());
		
		ImageView imageView = (ImageView)cellView.findViewById(R.id.cuisine_image);
		imageView.setAdjustViewBounds(true);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				ImageView clickedView = (ImageView) aV;
				final boolean newState = !listOfSelectedCuisines.get(position);
				listOfSelectedCuisines.set(position, newState);
				preferencesDao.setPreferences(SelectCuisinesActivity.mapImagePositionsToEnums(position).getLabel(), newState);
				final Boolean active = listOfSelectedCuisines.get(position);
				clickedView.setBackgroundResource(active ? R.drawable.active_button : R.drawable.inactive_button);
				clickedView.setImageResource(active ? listOfActiveResourceIds.get(position) : listOfInactiveResourceIds
						.get(position));
			}
		});
		final Boolean active = listOfSelectedCuisines.get(position);
		imageView.setBackgroundResource(active ? R.drawable.active_button : R.drawable.inactive_button);
		imageView.setImageResource(active ? listOfActiveResourceIds.get(position) : listOfInactiveResourceIds.get(position));
		
		return cellView;
	}
	
	public CuisineAdapter(final Context aContext,final List<Integer> aListOfActiveResourceIds, final List<Boolean> aListOfSelectedCuisines,
			List<Integer> aListOfInactiveResourceIds){
		
		this.mContext = aContext;
		this.listOfActiveResourceIds = aListOfActiveResourceIds;
		this.listOfInactiveResourceIds = aListOfInactiveResourceIds;
		this.listOfSelectedCuisines = aListOfSelectedCuisines;
		this.preferencesDao = new CuisinePreferences(mContext);
		
	}

}
