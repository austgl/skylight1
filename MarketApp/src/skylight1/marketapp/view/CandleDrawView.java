package skylight1.marketapp.view;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.Surface;
import android.view.View;
import java.io.*;

import skylight1.marketapp.EquityTimeSeries;
import skylight1.marketapp.feed.YahooEquityPricingInformationFeed;

// TODO: Auto-generated Javadoc
/**
 * The Class CandleDrawView.
 */
public class CandleDrawView extends View{
	
	String ticker;
	
	/** The max high. */
	double maxHigh;
	
	/** The min low. */
	double minLow;
	
	/** The Constant CFACTOR. */
	static final double CFACTOR =0.7;  
	
	/** The Constant MARGIN1. */
	static final int MARGIN1=40;
	
	/** The Constant MARGIN2. */
	static final int MARGIN2=30;
	
	/** The Constant MARGIN3. */
	static final int MARGIN3=20;
	
	/** The Constant NUMPRICE. */
	static final int NUMPRICE=10;
    
    /**
     * Instantiates a new candle draw view.
     * 
     * @param context the context
     */
    public CandleDrawView(Context context,String ticker) {
        super(context);
        this.ticker=ticker;
    }
    
    /**
     * Gets the max high.
     * 
     * @return the max high
     */
    public double getMaxHigh() {
		return maxHigh;
	}
	
	/**
	 * Sets the max high.
	 * 
	 * @param maxHigh the new max high
	 */
	public void setMaxHigh(double maxHigh) {
		this.maxHigh = maxHigh;
	}
	
	/**
	 * Gets the min low.
	 * 
	 * @return the min low
	 */
	public double getMinLow() {
		return minLow;
	}
	
	/**
	 * Sets the min low.
	 * 
	 * @param minLow the new min low
	 */
	public void setMinLow(double minLow) {
		this.minLow = minLow;
	}
	
	/**
	 * Gets the candle sticks.
	 * 
	 * @param candleDataList the candle data list
	 * @param rect the rect
	 * 
	 * @return the candle sticks
	 */
	public List<CandleStick> getCandleSticks(List<EquityTimeSeries> candleDataList, Rect rect){
    	List<CandleStick> candleStickList = new ArrayList<CandleStick>();
    
    	setMaxHigh(Collections.max(candleDataList, new highSort()).getHigh());
    	setMinLow(Collections.min(candleDataList, new lowSort()).getLow());
    	int num = candleDataList.size();
    	Iterator<EquityTimeSeries> itr = candleDataList.iterator(); 
    	int i=0;
    	int width=rect.width()/num;
    	while(itr.hasNext()) {
    	    EquityTimeSeries candleData = (EquityTimeSeries)itr.next(); 
    	    CandleStick candleStick = getCandleStick(candleData,getMaxHigh(),getMinLow(),rect,width, num,i);
    	    i++;
    	    candleStickList.add(candleStick);
    	} 
    	
    	return candleStickList;
    }
    
    /**
     * Gets the candle stick.
     * 
     * @param candleData the candle data
     * @param max the max
     * @param min the min
     * @param rect the rect
     * @param width the width
     * @param num the num
     * @param ind the ind
     * 
     * @return the candle stick
     */
    public  CandleStick getCandleStick(EquityTimeSeries candleData,double max,double min,Rect rect, int width,int num, int ind){
    	CandleStick candleStick = new CandleStick();
  
    	int x=rect.left+ind*width;
    	candleStick.setTopLeftX(x-(int)(width*CFACTOR/2));
    	candleStick.setBotRightX(x+(int)(width*CFACTOR/2));
    	int topLeftY,botRightY, top, bot;
    	int height = rect.bottom-rect.top;
    	
		topLeftY = (int)(((candleData.getClose()-min)*height)/(min-max)+ rect.bottom);	
		botRightY= (int)(((candleData.getOpen()-min)*height)/(min-max)+ rect.bottom);
		top = (int)(((candleData.getHigh()-min)*height)/(min-max)+ rect.bottom);	
		bot= (int)(((candleData.getLow()-min)*height)/(min-max)+ rect.bottom);
		
		if (botRightY>topLeftY){
			candleStick.setTopLeftY(topLeftY);
			candleStick.setBotRightY(botRightY);
		}else{
			candleStick.setTopLeftY(botRightY);
			candleStick.setBotRightY(topLeftY);
		}
		candleStick.setTop(top);
		candleStick.setBottom(bot);
		candleStick.setX(x);
		if (candleData.getClose()>candleData.getOpen()){
			candleStick.setColor(Color.GREEN);
		}else{
			candleStick.setColor(Color.RED);
		}
		candleStick.setDate(candleData.getDate());
    	return candleStick;
    }
    
    /**
     * Gets the new rect.
     * 
     * @param canvas the canvas
     * 
     * @return the new rect
     */
    protected Rect getNewRect(Canvas canvas) {
    	Rect rect = canvas.getClipBounds();
    	Rect newRect = new Rect();
    	newRect.top=rect.top+MARGIN1;
    	newRect.bottom=rect.bottom-MARGIN1;
    	newRect.left=rect.left+MARGIN1;
    	newRect.right=rect.right-MARGIN1;
    	return newRect;
    }
    
    /**
     * Draw enclosure.
     * 
     * @param canvas the canvas
     * @param newRect the new rect
     */
    protected void drawEnclosure(Canvas canvas, Rect newRect) {
    	NumberFormat nf= NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
    	ShapeDrawable mDrawable1 = new ShapeDrawable();
    	mDrawable1.getPaint().setColor(Color.BLUE);
     	canvas.drawLine(newRect.left-MARGIN2,newRect.bottom+MARGIN2,newRect.right+MARGIN2,newRect.bottom+MARGIN2,mDrawable1.getPaint());
    	canvas.drawLine(newRect.left-MARGIN2,newRect.bottom+MARGIN2,newRect.left-MARGIN2,newRect.top-MARGIN2,mDrawable1.getPaint());
    	canvas.drawLine(newRect.left-MARGIN2,newRect.top-MARGIN2,newRect.right+MARGIN2,newRect.top-MARGIN2,mDrawable1.getPaint());
    	canvas.drawLine(newRect.right+MARGIN2,newRect.top-MARGIN2,newRect.right+MARGIN2,newRect.bottom+MARGIN2,mDrawable1.getPaint());
    	int incr = newRect.height()/NUMPRICE;
    	
    	/*Draw prices */
    	canvas.drawText(Double.toString(getMaxHigh()),newRect.right-MARGIN3+10,newRect.top, mDrawable1.getPaint());
    	canvas.drawText(Double.toString(getMinLow()),newRect.right-MARGIN3+10,newRect.bottom, mDrawable1.getPaint());
    	for (int i=0;i<=NUMPRICE;i++){
    		canvas.drawLine(newRect.right+MARGIN2,newRect.top+incr*i, newRect.right+MARGIN2-2,newRect.top+incr*i,mDrawable1.getPaint());
    		if (i>0 &&i<NUMPRICE& (i%2==0)){
    			double price=getMaxHigh() - (getMaxHigh()-getMinLow())*i/NUMPRICE;
    			canvas.drawText(nf.format(price),newRect.right-MARGIN3+10 ,newRect.top+incr*i, mDrawable1.getPaint());
    		}
    	}
    }
    
    /**
     * Draw candles.
     * 
     * @param canvas the canvas
     * @param candleStickList the candle stick list
     * @param newRect the new rect
     */
    protected void drawCandles(Canvas canvas, List<CandleStick> candleStickList, Rect newRect ) {
    	ShapeDrawable  mDrawable;
    	Iterator itr = candleStickList.iterator(); 
    	int i=0;
    	//	int num=candleStickList.size()/2;
    	while(itr.hasNext()) {
    		mDrawable = new ShapeDrawable();
    		i++;
    		CandleStick candleStick = (CandleStick)itr.next();
    		mDrawable.getPaint().setColor(candleStick.getColor());
    		mDrawable.setBounds(candleStick.getTopLeftX(), candleStick.getTopLeftY(), 
    				candleStick.getBotRightX(), candleStick.getBotRightY());

    		mDrawable.draw(canvas);

    		canvas.drawLine(candleStick.getX(),candleStick.getTop(), candleStick.getX(),candleStick.getBottom(),mDrawable.getPaint());

    		canvas.drawLine(candleStick.getX(),newRect.bottom+MARGIN2, candleStick.getX(),newRect.bottom+MARGIN2-2,mDrawable.getPaint());

    		/* if ((i%(num+1))==0){
        	canvas.drawText(this.ticker,newRect.centerX() ,newRect.top-MARGIN2-5, mDrawable.getPaint());
        }*/
    		if (i==(candleStickList.size())){
    			StringBuffer myDate= new StringBuffer();
    			myDate.append(new SimpleDateFormat("dd,MMM,yyyy").format(candleStick.getDate()));
    			//canvas.drawText(" Quote Ending "+ myDate.toString(),newRect.centerX()-20,newRect.top-MARGIN2+10, mDrawable.getPaint());
    			canvas.drawText(this.ticker+" Quote Ending "+ myDate.toString(),newRect.centerX()-70,newRect.top-MARGIN2+10, mDrawable.getPaint());
    		}
    		if (candleStickList.size()<31){
    			canvas.drawText(new Integer(candleStick.getDate().getDate()).toString(),candleStick.getX()-5 ,newRect.bottom+MARGIN2-5, mDrawable.getPaint());
    			canvas.drawLine(candleStick.getX(),candleStick.getTop(), candleStick.getX(),candleStick.getBottom()+7,mDrawable.getPaint());
    			canvas.drawLine(candleStick.getX(),newRect.bottom+MARGIN2, candleStick.getX(),newRect.bottom+MARGIN2-7,mDrawable.getPaint());
    		}else if ((candleStickList.size()>30)&&(candleStickList.size()<50)){
    			if (i%3==0){
    				canvas.drawText(new Integer(candleStick.getDate().getDate()).toString(),candleStick.getX()-5 ,newRect.bottom+MARGIN2-5, mDrawable.getPaint());
    				canvas.drawLine(candleStick.getX(),candleStick.getTop(), candleStick.getX(),candleStick.getBottom()+7,mDrawable.getPaint());

    				canvas.drawLine(candleStick.getX(),newRect.bottom+MARGIN2, candleStick.getX(),newRect.bottom+MARGIN2-7,mDrawable.getPaint());
    			}
    		}else if (candleStickList.size()<100){
    			if (i%4==0){
    				canvas.drawLine(candleStick.getX(),candleStick.getTop(), candleStick.getX(),candleStick.getBottom()+7,mDrawable.getPaint());       
    				canvas.drawLine(candleStick.getX(),newRect.bottom+MARGIN2, candleStick.getX(),newRect.bottom+MARGIN2-7,mDrawable.getPaint());
    				canvas.drawText(new Integer(candleStick.getDate().getDate()).toString(),candleStick.getX()-5 ,newRect.bottom+MARGIN2-5, mDrawable.getPaint());
    			}
    		}else {
    			if (i%5==0){
    				canvas.drawLine(candleStick.getX(),candleStick.getTop(), candleStick.getX(),candleStick.getBottom()+5,mDrawable.getPaint());    
    				canvas.drawLine(candleStick.getX(),newRect.bottom+MARGIN2, candleStick.getX(),newRect.bottom+MARGIN2-5,mDrawable.getPaint());
    				canvas.drawText(new Integer(candleStick.getDate().getDate()).toString(),candleStick.getX()-5 ,newRect.bottom+MARGIN2-5, mDrawable.getPaint());
    			}
    		}
    	}
    }

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    protected void onDraw(Canvas canvas) {
    	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
    	    // The following message is only displayed once.
    		
    		//ActivityManager am = ActivityManager..getDefault();
    		Surface.setOrientation(0, 2);
    	   // this..setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    	}
    	ShapeDrawable  mDrawable;
    	Rect rect = canvas.getClipBounds();
    	//List<EquityTimeSeries> candleDataList=EquityTimeSeries.dummyData();
    	List<EquityTimeSeries> candleDataList=YahooEquityPricingInformationFeed.getCandleStickDataForTicker(ticker);
    	Rect newRect=getNewRect(canvas);
    	List<CandleStick> candleStickList=getCandleSticks(candleDataList, newRect);
    	drawEnclosure(canvas, newRect);
    	drawCandles(canvas, candleStickList, newRect );
    
    }
  
    /**
     * The Class highSort.
     */
    class highSort implements Comparator<EquityTimeSeries> {
    	
	    /* (non-Javadoc)
	     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	     */
	    public int compare(EquityTimeSeries one, EquityTimeSeries two){
    	 return (new Double(one.getHigh())).compareTo(new Double(two.getHigh()));
    	}
    }
    
    /**
     * The Class lowSort.
     */
    class lowSort implements Comparator<EquityTimeSeries> {
    	
	    /* (non-Javadoc)
	     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	     */
	    public int compare(EquityTimeSeries one, EquityTimeSeries two){
    	 return (new Double(one.getLow())).compareTo(new Double(two.getLow()));
    	}
    }
   
}