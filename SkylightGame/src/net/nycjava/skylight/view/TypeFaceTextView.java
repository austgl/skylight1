package net.nycjava.skylight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TypeFaceTextView extends TextView {
	private static final int BORDER_WIDTH = 1;
	private Typeface typeface;

	public TypeFaceTextView(Context context) {
		super(context);
	}

	public TypeFaceTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setTypeface(attrs);
	}

	private void setTypeface(AttributeSet attrs) {
		final String typefaceFileName = attrs.getAttributeValue(null, "typeface");
		if (typefaceFileName != null) {
			typeface = Typeface.createFromAsset(getContext().getAssets(), typefaceFileName);
		}

		setTypeface(typeface);
	}

	public TypeFaceTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setTypeface(attrs);
	}
	
	@Override
	public void draw(Canvas canvas) {
		drawBackground(canvas, -BORDER_WIDTH, -BORDER_WIDTH);
		drawBackground(canvas, BORDER_WIDTH, BORDER_WIDTH);
		drawBackground(canvas, -BORDER_WIDTH, BORDER_WIDTH);
		drawBackground(canvas, -BORDER_WIDTH, -BORDER_WIDTH);
		super.draw(canvas);
	}

	private void drawBackground(Canvas aCanvas, int aDX, int aDY) {
		aCanvas.save(Canvas.ALL_SAVE_FLAG);
		int originalColour = getCurrentTextColor();
		aCanvas.translate(aDX, aDY);
		setTextColor(Color.BLACK);
		super.draw(aCanvas);
		setTextColor(originalColour);
		aCanvas.restore();
	}
}
