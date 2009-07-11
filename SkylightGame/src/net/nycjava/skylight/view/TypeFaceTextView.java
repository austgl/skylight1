package net.nycjava.skylight.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TypeFaceTextView extends TextView {
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
}
