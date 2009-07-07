package net.nycjava.skylight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class TypeFaceTextView extends TextView {
	private Typeface typeface;

	public TypeFaceTextView(Context context) {
		super(context);
	}

	public TypeFaceTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		final String typefaceFileName = attrs.getAttributeValue(null, "typeface");
		if (typefaceFileName != null) {
			typeface = Typeface.createFromAsset(getContext().getAssets(), typefaceFileName);
		}

		setTypeface(typeface);
	}

	public TypeFaceTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		final String typefaceFileName = attrs.getAttributeValue("http://schemas.android.com/apk/res/android",
				"typeface");
		if (typefaceFileName != null) {
			typeface = Typeface.createFromAsset(getContext().getAssets(), typefaceFileName);
		}

		setTypeface(typeface);
	}
	//	
	// @Override
	// public TextPaint getPaint() {
	// final TextPaint paint = super.getPaint();
	// paint.setTypeface(typeface);
	// return paint;
	// }
	//	
	// @Override
	// protected void onDraw(Canvas canvas) {
	// getPaint();
	//		
	// this.setTypeface(tf)
	//		
	// super.onDraw(canvas);
	// }
}
