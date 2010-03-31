/*
* Copyright (C) 2010 The Skylight1 Open Source Project
*
* http://skylight1.googlecode.com
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package skylight1.wallpaper;

import skylight1.wallpaper.R;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

//TODO: add config to select background from a number of images

public class Skylight1Wallpaper extends WallpaperService {

    private final Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
//TODO: add activity that calls live wallpaper chooser and add this to its onCreate()
//        sendBroadcast(new Intent(android.app.WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new WallpaperEngine();
    }

    class WallpaperEngine extends Engine {

        private final Paint mPaint = new Paint();
        private float mTouchX = -1;
        private float mTouchY = -1;
        private int width, height;
        private float mCenterX;
        private float mCenterY;
        private float mOffset;
        private int mFrameCount = 0;
        private boolean mVisible;
        private boolean mPrintVisible;
        private Bitmap backgroundBitmap = null;
        private Bitmap foregroundBitmap = null;

        private final Runnable drawFingerRunnable = new Runnable() {
            public void run() {
            	mPaint.setAlpha(mFrameCount);
                drawFrame();
            	mFrameCount+=10;
            	if(mFrameCount>250) {
            		mPrintVisible = false;
            		mFrameCount = 0;
            	}
            }
        };

        WallpaperEngine() {
            Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            height = display.getHeight();
            width = display.getWidth();
            setBackgroundBitmap(R.drawable.bg);
            foregroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fingerprint);
        }

        private void setBackgroundBitmap(int id) {
            backgroundBitmap = BitmapFactory.decodeResource(getResources(), id);
            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, false);

            //TODO: use existing wallpaper for background - add current to list of backgrounds to choose from
            //Drawable currentWallPaperDrawable = WallpaperManager.getInstance(getApplicationContext()).getFastDrawable();
            //backgroundBitmap = ((BitmapDrawable)currentWallPaperDrawable).getBitmap();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(drawFingerRunnable);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            }
            else {
                mHandler.removeCallbacks(drawFingerRunnable);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mCenterX = foregroundBitmap.getWidth()/2.0f;
            mCenterY = foregroundBitmap.getHeight()/2.0f;
            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(drawFingerRunnable);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
            mOffset = xOffset;
            drawFrame();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mTouchX = event.getX();
                mTouchY = event.getY();
                mVisible = true;
                mPrintVisible = true;
                drawFrame();
            }
            else {
                mTouchX = -1;
                mTouchY = -1;
            }
            super.onTouchEvent(event);
        }

        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
               	  drawBackground(c);
               	  if(mPrintVisible) {
               		  drawTouchPoint(c);
               	  }
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            mHandler.removeCallbacks(drawFingerRunnable);
            if (mVisible && mPrintVisible) {
                mHandler.postDelayed(drawFingerRunnable, 1000 / 24);
            }
        }

        void drawBackground(Canvas c) {
        	if(backgroundBitmap!=null) {
        		c.drawBitmap(backgroundBitmap, 0, 0,mPaint);
        	}
        }

        void drawTouchPoint(Canvas c) {
      	  Log.i("drawTouchPoint:", "foregroundBitmap="+foregroundBitmap+" mTouchX="+mTouchX+" mTouchY="+mTouchY);

      	  if (mTouchX >=0 && mTouchY >= 0) {
        	if(foregroundBitmap!=null) {
        		c.drawBitmap(foregroundBitmap, mTouchX-mCenterX, mTouchY-mCenterY/2, mPaint);
        	}
          }
        }
    } //Engine
}
