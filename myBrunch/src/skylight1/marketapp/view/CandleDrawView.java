package skylight1.marketapp.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import java.io.*;

//import android.content.Context;

public class CandleDrawView extends View{
	private HashMap candleMap;
	private ShapeDrawable mDrawable1, mDrawable2, mDrawable;
	private int x, y, width,height;
    public CandleDrawView(Context context) {
        super(context);
        x = 10;
        y = 200;
        width = 10;
        height = 100;

        mDrawable1 = new ShapeDrawable();//new OvalShape());
        mDrawable1.getPaint().setColor(0xff74AC23);
        mDrawable1.setBounds(x, y, x + width, y + height);
        mDrawable2 = new ShapeDrawable();//new OvalShape());
        mDrawable2.getPaint().setColor(0xFFFF0000);
        mDrawable2.setBounds(x+15, y+5, x + 15+ width, y +5+height);
        
    }
    public List<CandleData> getDummyCandleData(){	
    	List candleDataList = new ArrayList<CandleData>();
    	
    	String[] candleStr={"7/1/2009	105	106.27	104.73	104.84",	
    	"6/30/2009	105.69	106.03	103.81	104.42",	
    	"6/29/2009	105.99	106.18	105.16	105.83",	
    	"6/26/2009	106.5	106.5	105.05	105.68",	
    	"6/25/2009	103.7	106.79	103.51	106.06",
    	"6/24/2009	105.39	106.48	103.72	104.15",	
    	"6/23/2009	104.75	104.87	103.79	104.44",	
    	"6/22/2009	105.18	105.88	104.23	104.52",
    	"6/19/2009	106.31	106.65	105.5	105.89",
    	"6/18/2009	106.93	107.53	106.12	106.33"};	
    	for (int i=0;i<10;i++){
    		 String[] result = candleStr[i].split("\t");
    		 CandleData candleData = new CandleData();
    	     candleData.setDate(result[0]);
    	     candleData.setOpen(Float.valueOf(result[1]));
    	     candleData.setHigh(Float.valueOf(result[2]));
    	     candleData.setLow(Float.valueOf(result[3]));
    	     candleData.setClose(Float.valueOf(result[4]));
    	     if (candleData.getClose()>candleData.getOpen()){
    	    	 candleData.setBull(true);
    	     }
    	     candleDataList.add(candleData);
    	}
    	return candleDataList;
    }
   
    protected void onDraw(Canvas canvas) {
    	mDrawable1.draw(canvas);
    	canvas.drawLine(x+width/2, y-30, x+width/2,y+height+40,mDrawable1.getPaint());
    	mDrawable2.draw(canvas);
    	canvas.drawLine(x+15+width/2, y-20, x+15+width/2,y+height+40,mDrawable2.getPaint());
    }
   
}