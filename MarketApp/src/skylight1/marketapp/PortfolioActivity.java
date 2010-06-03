package skylight1.marketapp;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: May 20, 2010
 * Time: 7:57:45 PM
 */
public class PortfolioActivity extends ListActivity {

//    private static final String[] DATA = {"AAPL", "GOOG"};
    static List<PortfolioItem> portfolioItems = new ArrayList<PortfolioItem>();

    private static class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Bitmap mIcon1;
        private Bitmap mIcon2;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            // Icons bound to the rows.
            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
            mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return portfolioItems.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_icon_text, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.tickerTextView = (TextView) convertView.findViewById(R.id.ticker);
                holder.avgPriceTextView = (TextView) convertView.findViewById(R.id.avgPrice);
                holder.numberOfSharesTextView = (TextView) convertView.findViewById(R.id.numberOfShares);
                holder.currentPriceTextView = (TextView) convertView.findViewById(R.id.currentPrice);
//                holder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            final PortfolioItem item = portfolioItems.get(position);
            holder.tickerTextView.setText( item.getTicker());
            holder.avgPriceTextView.setText( item.getAveragePriceStr());
            holder.numberOfSharesTextView.setText( item.getNumberOfSharesAsStr());
            holder.currentPriceTextView.setText( item.getCurrentPriceStr());
            
            holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 : mIcon2);

            return convertView;
        }

        static class ViewHolder {
            TextView tickerTextView;
            TextView avgPriceTextView;
            TextView numberOfSharesTextView;
            TextView currentPriceTextView;
            ImageView icon;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.portfolio);
        portfolioItems.add(new PortfolioItem("AAPL", 202.0f, 1000, 250.10f));
        portfolioItems.add(new PortfolioItem("GOOG", 20.0f, 10000, 50.10f));
        portfolioItems.add(new PortfolioItem("MSFT", 20.1f, 50000, 250.10f));
        portfolioItems.add(new PortfolioItem("IBM", 22.0f, 100000, 19.21f));
        portfolioItems.add(new PortfolioItem("C", 3.74f, 100, 3.78f));
        portfolioItems.add(new PortfolioItem("JDSU", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("ADBE", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("PALM", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("F", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("GM", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("JNPR", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("AMD", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("INTC", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("GE", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("RIMM", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("WEC", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("DELL", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("SY", 10.78f, 9999, 12.54f));

        setListAdapter(new EfficientAdapter(this));

    }

}
