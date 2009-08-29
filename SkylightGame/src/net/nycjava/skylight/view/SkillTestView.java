package net.nycjava.skylight.view;

import net.nycjava.skylight.R;
import net.nycjava.skylight.SkylightActivity;
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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Canvas.EdgeType;
import android.graphics.Paint.Style;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SkillTestView extends View {
	private static final int DEGREE_OF_DOUBLE_VISION = 10;

	private static final int SCREEN_MARGIN = 10;

	@Dependency
	private BalancedObjectPublicationService balancedObjectPublicationService;

	@Dependency
	private CountdownPublicationService countdownPublicationService;

	private BalancedObjectObserver balancedObjectObserver;

	private CountdownObserver countdownObserver;

	private int remainingTime;

	final private Bitmap glassBitmap;

	private int width, height;

	final private Typeface face;

	@Dependency
	private Integer difficultyLevel;

	private Bitmap levelGlassFullBitmap;

	private Bitmap levelGlassEmptyBitmap;

	long animationCycleStartTime = System.currentTimeMillis();

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

	final private RectF timeRemainingRect;

	protected boolean glassInvalidated;

	final private int glassBitmapWidth;

	final private int glassBitmapHeight;

	final private int levelGlassWidth;

	final private int levelGlassHeight;

	final private Paint levelPaint = new Paint();
	
	final private Paint arcPaint = new Paint();
	
	final private Paint timePaint = new Paint();

	public SkillTestView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);
		glassBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.theglass);
		glassBitmapWidth = glassBitmap.getWidth();
		glassBitmapHeight = glassBitmap.getHeight();

		levelGlassFullBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.level_glass);
		levelGlassEmptyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.level_glass_empty);
		levelGlassWidth = levelGlassFullBitmap.getWidth();
		levelGlassHeight = levelGlassFullBitmap.getHeight();

		face = Typeface.createFromAsset(getContext().getAssets(), "passthedrink.ttf");

		timeRemainingRect = new RectF(SCREEN_MARGIN, SCREEN_MARGIN, SCREEN_MARGIN + levelGlassHeight, SCREEN_MARGIN
				+ levelGlassHeight);

		timeRemainingRectangleLeft = (int) timeRemainingRect.left - 1;
		timeRemainingRectangleTop = (int) timeRemainingRect.top - 1;
		timeRemainingRectangleRight = (int) timeRemainingRect.right + 1;
		timeRemainingRectangleBottom = (int) timeRemainingRect.bottom + 1;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		frames = 0;
		timeStartedCountingFrames = 0;

		balancedObjectObserver = new BalancedObjectObserver() {
			@Override
			public void balancedObjectNotification(float anX, float aY) {
				// if the glass has been invalidated, but not redrawn yet, then don't mess around with the
				// coordinates, otherwise the clip of the canvas and the coordinates won't match
				if (glassInvalidated) {
					return;
				}

				// invalidate where the glass used to be, so that the background will get
				// redrawn by the super class
				int extra = doubleVision() ? DEGREE_OF_DOUBLE_VISION : 0;
				SkillTestView.this.postInvalidate(glassRectangleLeft - extra, glassRectangleTop - extra, glassRectangleRight + extra,
						glassRectangleBottom + extra);

				// update the position of the glass
				updateGlassRectangle(anX, aY);

				// invalidate the new position so that the glass will be drawn there
				SkillTestView.this.postInvalidate(glassRectangleLeft, glassRectangleTop, glassRectangleRight,
						glassRectangleBottom);

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
//				System.out.println("remaining time = " + aRemainingTime);
				
//				if (aRemainingTime == 8)
//					Debug.startMethodTracing("skylight");
//				if (aRemainingTime == 3) {
//					Debug.stopMethodTracing();
//					System.out.println("stopping trace now");
//				}

				remainingTime = aRemainingTime;

				postInvalidate(timeRemainingRectangleLeft, timeRemainingRectangleTop, timeRemainingRectangleRight,
						timeRemainingRectangleBottom);
			}
		};
		countdownPublicationService.addObserver(countdownObserver);

		// set the background
		final int backgroundResourceId;
		if (difficultyLevel >= SkylightActivity.SMASHED_DIFFICULTY_LEVEL) {
			backgroundResourceId = R.drawable.marble;
		} else if (difficultyLevel >= SkylightActivity.BUZZED_DIFFICULTY_LEVEL) {
			backgroundResourceId = R.drawable.wood;
		} else {
			backgroundResourceId = R.drawable.background_table;
		}
		setBackgroundResource(backgroundResourceId);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		width = getWidth();
		height = getHeight();

		updateGlassRectangle(0, 0);
	}

	@Override
	protected void onDetachedFromWindow() {
		balancedObjectPublicationService.removeObserver(balancedObjectObserver);
		countdownPublicationService.removeObserver(countdownObserver);

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
		if (timeStartedCountingFrames == 0) {
			timeStartedCountingFrames = System.currentTimeMillis();
		}
		frames++;
		lastTimeFrameCounted = System.currentTimeMillis();

		if (!doubleVision()) {
			drawView(canvas);
		} else {
			double animationCycle = (double) (System.currentTimeMillis() - animationCycleStartTime);
			double degreeOfDoubleVision = Math.cos(animationCycle / 8666d) * DEGREE_OF_DOUBLE_VISION;
			double angleOfDoubleVision = animationCycle / 2534d;

			int x = (int) (Math.cos(angleOfDoubleVision) * degreeOfDoubleVision);
			int y = (int) (Math.sin(angleOfDoubleVision) * degreeOfDoubleVision);

			canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), 128, Canvas.ALL_SAVE_FLAG);
			canvas.translate(-x, -y);
			drawView(canvas);
			canvas.restore();

			canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), 128, Canvas.ALL_SAVE_FLAG);
			canvas.translate(x, y);
			drawView(canvas);
			canvas.restore();
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
		// draw the "pie"
		arcPaint.setAntiAlias(true);
		arcPaint.setStrokeWidth(2);
		int remainingTimeColor = Color.YELLOW;
		arcPaint.setColor(remainingTimeColor);
		aCanvas.drawArc(timeRemainingRect, -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15, true,
				arcPaint);
		arcPaint.setColor(Color.BLACK);
		arcPaint.setStyle(Style.STROKE);
		aCanvas.drawArc(timeRemainingRect, -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15, true,
				arcPaint);

		// draw the number
		timePaint.setTextSize(30);
		timePaint.setTypeface(face);
		timePaint.setAntiAlias(true);
		timePaint.setTextAlign(Paint.Align.CENTER);
//		Rect timeRemainingTextBounds = new Rect();
		final String timeRemainingString = String.valueOf(remainingTime);
//		paint.getTextBounds(timeRemainingString, 0, timeRemainingString.length(), timeRemainingTextBounds);
		// Log.d(SkillTestView.class.getName(), String.format("rect = %s", timeRemainingRect));
		final int x = SCREEN_MARGIN + levelGlassHeight / 2;
		final int y = (int) (SCREEN_MARGIN + levelGlassHeight / 2 - timePaint.ascent() / 4);
		timePaint.setColor(Color.BLACK);
		aCanvas.drawText(timeRemainingString, x - 1, y - 1, timePaint);
		aCanvas.drawText(timeRemainingString, x - 1, y + 1, timePaint);
		aCanvas.drawText(timeRemainingString, x + 1, y - 1, timePaint);
		aCanvas.drawText(timeRemainingString, x + 1, y + 1, timePaint);
		timePaint.setColor(Color.WHITE);
		aCanvas.drawText(timeRemainingString, x, y, timePaint);
	}

	private void drawCurrentLevel(Canvas aCanvas) {
		final int currentDifficultyLevelGlassImageX = width - levelGlassWidth - SCREEN_MARGIN - difficultyLevel * 4;

		// check to see if the invalidated area is near the current level
		if (aCanvas.quickReject(currentDifficultyLevelGlassImageX, SCREEN_MARGIN, width - SCREEN_MARGIN, SCREEN_MARGIN
				+ levelGlassHeight, EdgeType.AA)) {
			return;
		}

		for (int previousLevels = 0; previousLevels < difficultyLevel; previousLevels++) {
			aCanvas.drawBitmap(levelGlassEmptyBitmap, width - levelGlassWidth - SCREEN_MARGIN - previousLevels * 4,
					SCREEN_MARGIN, null);
		}
		aCanvas.drawBitmap(levelGlassFullBitmap, currentDifficultyLevelGlassImageX, SCREEN_MARGIN, null);

		// draw the number
		levelPaint.setTextSize(30);
		levelPaint.setTypeface(face);
		levelPaint.setAntiAlias(true);
		levelPaint.setTextAlign(Paint.Align.CENTER);
//		Rect timeRemainingTextBounds = new Rect();
		String levelString = String.valueOf(difficultyLevel + 1);
//		paint.getTextBounds(levelString, 0, levelString.length(), timeRemainingTextBounds);
		final int x = currentDifficultyLevelGlassImageX + levelGlassWidth / 2;
		final int y = (int) (SCREEN_MARGIN + levelGlassHeight / 2 - levelPaint.ascent() / 4);
		levelPaint.setColor(Color.BLACK);
		aCanvas.drawText(levelString, x - 1, y - 1, levelPaint);
		aCanvas.drawText(levelString, x - 1, y + 1, levelPaint);
		aCanvas.drawText(levelString, x + 1, y - 1, levelPaint);
		aCanvas.drawText(levelString, x + 1, y + 1, levelPaint);
		levelPaint.setColor(Color.WHITE);
		aCanvas.drawText(levelString, x, y, levelPaint);
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
