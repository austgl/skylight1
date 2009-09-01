package net.nycjava.skylight.view;

import net.nycjava.skylight.R;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Canvas.EdgeType;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

final public class SkillTestView extends View {
	static enum PartToBeInvalidated {
		glassPart, timeRemainingPart;
	}

	private static final int BACKGROUND_CHANGE_LEVEL_FREQUENCY = 2;

	private static final int DEGREE_OF_DOUBLE_VISION = 10;

	private static final int SCREEN_MARGIN = 10;

	private final static int backgrounds[] = { R.drawable.background_table, R.drawable.largewoodgrain,
			R.drawable.marble, R.drawable.wood, R.drawable.seafoam, R.drawable.tiles, R.drawable.seafoam2,
			R.drawable.water, R.drawable.space1, R.drawable.galaxy };

	@Dependency
	private BalancedObjectPublicationService balancedObjectPublicationService;

	@Dependency
	private CountdownPublicationService countdownPublicationService;

	@Dependency
	private Integer difficultyLevel;

	private BalancedObjectObserver balancedObjectObserver;

	private CountdownObserver countdownObserver;

	private int remainingTime;

	private Bitmap glassBitmap;

	private int width, height;

	private int extraMarginToAccommodateDoubleVision;

	final private Typeface face;

	private Bitmap levelGlassFullBitmap;

	private Bitmap levelGlassEmptyBitmap;

	final private int timeRemainingRectangleLeft;

	final private int timeRemainingRectangleTop;

	final private int timeRemainingRectangleRight;

	final private int timeRemainingRectangleBottom;

	private int glassRectangleLeft;

	private int glassRectangleTop;

	private int glassRectangleRight;

	private int glassRectangleBottom;

	int frames;

	long timeStartedCountingFrames;

	long lastTimeFrameCounted;

	private boolean glassInvalidated;

	final private int glassBitmapWidth;

	final private int glassBitmapHeight;

	final private int levelGlassWidth;

	final private int levelGlassHeight;

	final private Paint levelPaint = new Paint();

	final private Paint arcPaint = new Paint();

	final private Paint timePaint = new Paint();

	private Bitmap difficultyLevelBitmap;

	private Bitmap timeRemainingBitmap;

	private RectF timeRemainingRect;

	private Bitmap spareTimeRemainingBitmap;

	private Canvas timeRemainingCanvas;

	private Canvas tempCanvasForMakingBitmapTransparent = new Canvas();

	public SkillTestView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);

		glassBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.theglass);
		glassBitmapWidth = glassBitmap.getWidth();
		glassBitmapHeight = glassBitmap.getHeight();

		levelGlassFullBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.level_glass);
		levelGlassEmptyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.level_glass_empty);
		levelGlassWidth = levelGlassFullBitmap.getWidth();
		levelGlassHeight = levelGlassFullBitmap.getHeight();

		timeRemainingRectangleLeft = SCREEN_MARGIN - 1;
		timeRemainingRectangleTop = SCREEN_MARGIN - 1;
		timeRemainingRectangleRight = SCREEN_MARGIN + levelGlassHeight + 1;
		timeRemainingRectangleBottom = SCREEN_MARGIN + levelGlassHeight + 1;
		timeRemainingRect = new RectF(0, 0, levelGlassHeight, levelGlassHeight);
		timeRemainingBitmap = Bitmap.createBitmap((int) timeRemainingRect.width(), (int) timeRemainingRect.height(),
				glassBitmap.getConfig());
		spareTimeRemainingBitmap = Bitmap.createBitmap((int) timeRemainingRect.width(), (int) timeRemainingRect
				.height(), glassBitmap.getConfig());
		timeRemainingCanvas = new Canvas();

		// load the type face
		face = Typeface.createFromAsset(getContext().getAssets(), "passthedrink.ttf");

		// initialize the paint for level
		levelPaint.setTextSize(30);
		levelPaint.setTypeface(face);
		levelPaint.setAntiAlias(true);
		levelPaint.setTextAlign(Paint.Align.CENTER);

		// initialize the two paints for time remaining
		timePaint.setTextSize(30);
		timePaint.setTypeface(face);
		timePaint.setAntiAlias(true);
		timePaint.setTextAlign(Paint.Align.CENTER);
		arcPaint.setAntiAlias(true);
		arcPaint.setStrokeWidth(2);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		frames = 0;
		timeStartedCountingFrames = 0;

		extraMarginToAccommodateDoubleVision = doubleVision() ? DEGREE_OF_DOUBLE_VISION : 0;

		balancedObjectObserver = new BalancedObjectObserver() {
			@Override
			public void balancedObjectNotification(float anX, float aY) {
				// if the glass has been invalidated, but not redrawn yet, then don't mess around with the
				// coordinates, otherwise the clip of the canvas and the coordinates won't match
				if (glassInvalidated) {
					return;
				}

				final int oldGlassRectangleLeft = glassRectangleLeft;
				final int oldGlassRectangleTop = glassRectangleTop;
				final int oldGlassRectangleRight = glassRectangleRight;
				final int oldGlassRectangleBottom = glassRectangleBottom;

				// update the position of the glass
				updateGlassRectangle(anX, aY);

				// invalidate where the glass used to be, so that the background will get
				// redrawn by the super class
				SkillTestView.this.postInvalidate(oldGlassRectangleLeft - extraMarginToAccommodateDoubleVision,
						oldGlassRectangleTop - extraMarginToAccommodateDoubleVision, oldGlassRectangleRight
								+ extraMarginToAccommodateDoubleVision, oldGlassRectangleBottom
								+ extraMarginToAccommodateDoubleVision);

				// invalidate the new position so that the glass will be drawn there
				postInvalidateAllChangeableParts(PartToBeInvalidated.glassPart);

				// safe to repaint now
				glassInvalidated = true;
			}

			@Override
			public void fallenOverNotification() {
			}
		};

		balancedObjectPublicationService.addObserver(balancedObjectObserver);

		countdownObserver = new CountdownObserver() {
			@Override
			public void countdownNotification(int aRemainingTime) {
				// System.out.println("remaining time = " + aRemainingTime);

				// if (aRemainingTime == 8)
				// Debug.startMethodTracing("skylight");
				// if (aRemainingTime == 3) {
				// Debug.stopMethodTracing();
				// System.out.println("stopping trace now");
				// }

				if (aRemainingTime == 0) {
					removeBalancedObjectObserver();
				}

				remainingTime = aRemainingTime;

				updateTimeRemainingBitmap();

				postInvalidateAllChangeableParts(PartToBeInvalidated.timeRemainingPart);
			}
		};
		countdownPublicationService.addObserver(countdownObserver);

		createDifficultyLevelBitmap();

		updateTimeRemainingBitmap();

		// prepare bit maps for double vision
		if (doubleVision()) {
			glassBitmap = glassBitmap.copy(glassBitmap.getConfig(), true);
			makeBitmapTransparent(glassBitmap);
			makeBitmapTransparent(difficultyLevelBitmap);
		}
	}

	private void updateTimeRemainingBitmap() {
		timeRemainingCanvas.setBitmap(spareTimeRemainingBitmap);
		timeRemainingCanvas.drawColor(Color.TRANSPARENT, Mode.SRC);

		arcPaint.setStyle(Style.FILL);
		arcPaint.setColor(Color.YELLOW);
		timeRemainingCanvas.drawArc(timeRemainingRect, -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15,
				true, arcPaint);
		arcPaint.setColor(Color.BLACK);
		arcPaint.setStyle(Style.STROKE);
		timeRemainingCanvas.drawArc(timeRemainingRect, -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15,
				true, arcPaint);

		// draw the number
		final String timeRemainingString = String.valueOf(remainingTime);
		final int x = levelGlassHeight / 2;
		final int y = (int) (levelGlassHeight / 2 - timePaint.ascent() / 4);
		timePaint.setColor(Color.BLACK);
		timeRemainingCanvas.drawText(timeRemainingString, x - 1, y - 1, timePaint);
		timeRemainingCanvas.drawText(timeRemainingString, x - 1, y + 1, timePaint);
		timeRemainingCanvas.drawText(timeRemainingString, x + 1, y - 1, timePaint);
		timeRemainingCanvas.drawText(timeRemainingString, x + 1, y + 1, timePaint);
		timePaint.setColor(Color.WHITE);
		timeRemainingCanvas.drawText(timeRemainingString, x, y, timePaint);

		if (doubleVision()) {
			makeBitmapTransparent(spareTimeRemainingBitmap);
		}

		// pull a switcheroo
		final Bitmap tempBitmap = timeRemainingBitmap;
		timeRemainingBitmap = spareTimeRemainingBitmap;
		spareTimeRemainingBitmap = tempBitmap;
	}

	private void createDifficultyLevelBitmap() {
		difficultyLevelBitmap = Bitmap.createBitmap(levelGlassWidth + difficultyLevel * 4, levelGlassHeight,
				glassBitmap.getConfig());
		final Canvas canvas = new Canvas(difficultyLevelBitmap);
		for (int previousLevel = 0; previousLevel < difficultyLevel; previousLevel++) {
			canvas.drawBitmap(levelGlassEmptyBitmap, difficultyLevelBitmap.getWidth() - levelGlassWidth - previousLevel
					* 4, 0, null);
		}
		canvas.drawBitmap(levelGlassFullBitmap, 0, 0, null);

		// draw the number
		final String levelString = String.valueOf(difficultyLevel + 1);
		final int x = levelGlassWidth / 2;
		final int y = (int) (levelGlassHeight / 2 - levelPaint.ascent() / 4);
		levelPaint.setColor(Color.BLACK);
		canvas.drawText(levelString, x - 1, y - 1, levelPaint);
		canvas.drawText(levelString, x - 1, y + 1, levelPaint);
		canvas.drawText(levelString, x + 1, y - 1, levelPaint);
		canvas.drawText(levelString, x + 1, y + 1, levelPaint);
		levelPaint.setColor(Color.WHITE);
		canvas.drawText(levelString, x, y, levelPaint);
	}

	private void makeBitmapTransparent(Bitmap aBitmap) {
		tempCanvasForMakingBitmapTransparent.setBitmap(aBitmap);
		tempCanvasForMakingBitmapTransparent.drawColor(Color.argb(128, 255, 255, 255), Mode.MULTIPLY);
	}

	/**
	 * Unfortunately, due to the double vision, it is necessary to redraw all three parts of the screen to keep the
	 * direction and degree of double vision remain constant!
	 */
	private void postInvalidateAllChangeableParts(PartToBeInvalidated aPartToBeInvalidated) {
		if (aPartToBeInvalidated == PartToBeInvalidated.glassPart || doubleVision()) {
			// invalidate where the glass is
			postInvalidate(glassRectangleLeft - extraMarginToAccommodateDoubleVision, glassRectangleTop
					- extraMarginToAccommodateDoubleVision, glassRectangleRight + extraMarginToAccommodateDoubleVision,
					glassRectangleBottom + extraMarginToAccommodateDoubleVision);
		}

		if (aPartToBeInvalidated == PartToBeInvalidated.timeRemainingPart || doubleVision()) {
			// invalidate where the time remaining is
			postInvalidate(timeRemainingRectangleLeft - extraMarginToAccommodateDoubleVision, timeRemainingRectangleTop
					- extraMarginToAccommodateDoubleVision, timeRemainingRectangleRight
					+ extraMarginToAccommodateDoubleVision, timeRemainingRectangleBottom
					+ extraMarginToAccommodateDoubleVision);
		}

		// invalidate where the level is
		if (doubleVision()) {
			postInvalidate(width - levelGlassWidth - SCREEN_MARGIN - difficultyLevel * 4
					- extraMarginToAccommodateDoubleVision, SCREEN_MARGIN - extraMarginToAccommodateDoubleVision, width
					- SCREEN_MARGIN + extraMarginToAccommodateDoubleVision, SCREEN_MARGIN + levelGlassHeight
					+ extraMarginToAccommodateDoubleVision);
		}
	}

	private void removeBalancedObjectObserver() {
		if (balancedObjectObserver != null) {
			balancedObjectPublicationService.removeObserver(balancedObjectObserver);
			balancedObjectObserver = null;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		width = getWidth();
		height = getHeight();

		updateGlassRectangle(0, 0);

		setBackground();
	}

	private void setBackground() {
		// set the background
		final int backgroundResourceId = backgrounds[Math.min(difficultyLevel / BACKGROUND_CHANGE_LEVEL_FREQUENCY,
				backgrounds.length - 1)];

		final Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundResourceId);
		final Bitmap scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, false);
		final BitmapDrawable backgroundDrawable = new BitmapDrawable(scaledBackgroundBitmap);
		setBackgroundDrawable(backgroundDrawable);
	}

	@Override
	protected void onDetachedFromWindow() {
		removeBalancedObjectObserver();
		countdownPublicationService.removeObserver(countdownObserver);

		if (difficultyLevelBitmap != null) {
			difficultyLevelBitmap.recycle();
			difficultyLevelBitmap = null;
		}

		if (glassBitmap != null) {
			glassBitmap.recycle();
			glassBitmap = null;
		}

		if (levelGlassEmptyBitmap != null) {
			levelGlassEmptyBitmap.recycle();
			levelGlassEmptyBitmap = null;
		}

		if (levelGlassFullBitmap != null) {
			levelGlassFullBitmap.recycle();
			levelGlassFullBitmap = null;
		}

		if (levelGlassFullBitmap != null) {
			levelGlassFullBitmap.recycle();
			levelGlassFullBitmap = null;
		}

		if (spareTimeRemainingBitmap != null) {
			spareTimeRemainingBitmap.recycle();
			spareTimeRemainingBitmap = null;
		}

		if (timeRemainingBitmap != null) {
			timeRemainingBitmap.recycle();
			timeRemainingBitmap = null;
		}

		super.onDetachedFromWindow();

		logFrameRate();
	}

	private void updateGlassRectangle(float anX, float aY) {
		// scale the object's location for the screen size
		int glassXOffset = (int) (width / 2 * anX);
		int glassYOffset = (int) (height / 2 * aY);

		// calculate the rectangle that holds the glass's new position
		glassRectangleLeft = width / 2 - glassBitmapWidth / 2 + glassXOffset;
		glassRectangleTop = height / 2 - glassBitmapHeight / 2 + glassYOffset;
		glassRectangleRight = glassRectangleLeft + glassBitmapWidth + 1;
		glassRectangleBottom = glassRectangleTop + glassBitmapHeight + 1;
	}

	private void logFrameRate() {
		final long timeElapsedInMillliseconds = lastTimeFrameCounted - timeStartedCountingFrames;
		final float timeElapsedInMillisecondsFloat = timeElapsedInMillliseconds;
		final float timeElapsedInSecondsFloat = timeElapsedInMillisecondsFloat / 1000f;
		final float frameRate = ((float) frames) / timeElapsedInSecondsFloat;
		Log.d(SkillTestView.class.getName(), String.format("number of frames per seconds = %f", frameRate));
	}

	public void onDraw(Canvas canvas) {
		// FPS stuff
		if (timeStartedCountingFrames == 0) {
			timeStartedCountingFrames = System.currentTimeMillis();
		}
		frames++;
		lastTimeFrameCounted = System.currentTimeMillis();

		if (!doubleVision()) {
			drawView(canvas);
		} else {
			double animationCycle = (double) (System.currentTimeMillis());

			// vary the distance of the double vision between half to a whole of DEGREE_OF_DOUBLE_VISION
			final double normalisedDegreeOfDoubleVision = Math.cos(animationCycle / 1000d);
			double degreeOfDoubleVisionInPixels = normalisedDegreeOfDoubleVision * DEGREE_OF_DOUBLE_VISION / 2
					+ Math.signum(normalisedDegreeOfDoubleVision) * DEGREE_OF_DOUBLE_VISION / 2;

			// rotate the double vision around
			double angleOfDoubleVision = animationCycle / 2534d;

			int x = (int) (Math.cos(angleOfDoubleVision) * degreeOfDoubleVisionInPixels);
			int y = (int) (Math.sin(angleOfDoubleVision) * degreeOfDoubleVisionInPixels);

			// canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), 128, Canvas.ALL_SAVE_FLAG);
			canvas.translate(-x, -y);
			drawView(canvas);
			// canvas.restore();

			// canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), 128, Canvas.ALL_SAVE_FLAG);
			canvas.translate(2 * x, 2 * y);
			drawView(canvas);
			// canvas.restore();
		}
	}

	private boolean doubleVision() {
		return difficultyLevel >= 10;
	}

	private void drawView(Canvas canvas) {
		// draw the glass
		drawGlass(canvas);

		// draw the level indicator
		drawCurrentLevel(canvas);

		// draw the timer
		drawTimeRemaining(canvas);
	}

	private void drawTimeRemaining(Canvas aCanvas) {
		// check to see if the invalidated area is near the time
		if (aCanvas.quickReject(timeRemainingRectangleLeft, timeRemainingRectangleTop, timeRemainingRectangleRight,
				timeRemainingRectangleBottom, EdgeType.AA)) {
			return;
		}
		aCanvas.drawBitmap(timeRemainingBitmap, SCREEN_MARGIN, SCREEN_MARGIN, null);
	}

	private void drawCurrentLevel(Canvas aCanvas) {
		final int currentDifficultyLevelGlassImageX = width - levelGlassWidth - SCREEN_MARGIN - difficultyLevel * 4;

		aCanvas.drawBitmap(difficultyLevelBitmap, currentDifficultyLevelGlassImageX, SCREEN_MARGIN, null);
	}

	private void drawGlass(Canvas aCanvas) {
		// check to see if the invalidated area is near the glass
		if (aCanvas.quickReject(glassRectangleLeft, glassRectangleTop, glassRectangleRight, glassRectangleBottom,
				EdgeType.AA)) {
			return;
		}
		aCanvas.drawBitmap(glassBitmap, glassRectangleLeft, glassRectangleTop, null);

		glassInvalidated = false;
	}
}
