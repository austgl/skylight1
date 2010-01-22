package skylight1.toast.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Optionally outline text in black and display using XML specified typeface.
 * <p>
 * XML Attributes:<dl>
 * <dt>outline</dt>
 * <dd>Boolean attribute to enable outline. Default is true.</dd>
 * 
 * <dt>typeface</dt>
 * <dd>File name of the typeface from the assets directory. Example: "skylight.ttf"</dd>
 * </dl>
 * <p>
 * Implementation note: current outlining method doesn't work during AlphaAnimation until full visibility reached.
 */
public class TypeFaceTextView extends TextView {
	
	private static final Paint BLACK_OUTLINE_PAINT = new Paint();

	static {
		BLACK_OUTLINE_PAINT.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
	}

	private static final int OUTLINE_WIDTH = 1;
	
	private static final boolean DEFAULT_IS_OUTLINE_ENABLED = true;
	
	private boolean isOutlineEnabled = DEFAULT_IS_OUTLINE_ENABLED;

	public TypeFaceTextView(Context context) {
		super(context);
		init(null);
	}

	public TypeFaceTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public TypeFaceTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	private void init(AttributeSet attrs) {
		
		//Setup outlining if enabled.
		if ( null != attrs ) {	
			isOutlineEnabled = attrs.getAttributeBooleanValue(null, "outline", DEFAULT_IS_OUTLINE_ENABLED);
		}
		if ( isOutlineEnabled ) {
			setDrawingCacheEnabled(false);	
		}
		
		//Setup typeface if specified.
		if ( null != attrs ) {	
			final String typefaceFileName = attrs.getAttributeValue(null, "typeface");
			if ( null != typefaceFileName ) {
				final Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), typefaceFileName);
				setTypeface(typeface);
			}
		}
	}

	@Override
	public void draw(Canvas aCanvas) {
		if ( isOutlineEnabled ) {
			aCanvas.saveLayer(null, BLACK_OUTLINE_PAINT, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
					| Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.MATRIX_SAVE_FLAG);
			drawOutlineSide(aCanvas, -OUTLINE_WIDTH, -OUTLINE_WIDTH);
			drawOutlineSide(aCanvas, OUTLINE_WIDTH + OUTLINE_WIDTH, 0);
			drawOutlineSide(aCanvas, 0, OUTLINE_WIDTH + OUTLINE_WIDTH);
			drawOutlineSide(aCanvas, -OUTLINE_WIDTH - OUTLINE_WIDTH, 0);
			aCanvas.restore();
		}
		super.draw(aCanvas);
	}

	private void drawOutlineSide(Canvas aCanvas, int aDX, int aDY) {
		aCanvas.translate(aDX, aDY);
		super.draw(aCanvas);
	}
}
