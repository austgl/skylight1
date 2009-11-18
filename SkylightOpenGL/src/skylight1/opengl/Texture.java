package skylight1.opengl;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class Texture {
	private int textureId;

	private GL10 gL10;

	private boolean textureAllocated;

	public Texture(GL10 aGL10, InputStream anInputStream) {
		loadBitmap(aGL10, anInputStream);
	}

	public Texture(GL10 aGL10, Context aContext, int aTextureDrawable) {
		final InputStream inputStream = aContext.getResources().openRawResource(aTextureDrawable);
		try {
			loadBitmap(aGL10, inputStream);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// No harm, no foul
			}
		}
	}

	public void activateTexture() {
		gL10.glEnable(GL10.GL_TEXTURE_2D);
		gL10.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
	}

	public void deactivateTexture() {
		// TODO is this method really needed!!!?
		gL10.glDisable(GL10.GL_TEXTURE_2D);
	}

	private void loadBitmap(final GL10 aGL10, final InputStream anInputStream) {
		gL10 = aGL10;

		final int[] textures = new int[1];
		aGL10.glGenTextures(textures.length, textures, 0);

		textureId = textures[0];
		textureAllocated = true;

		aGL10.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		// TODO does it make sense to set these this early?
		// TODO does this need to be decided by the client?
		// TODO does this belong to the texture or the geometry?
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		final Bitmap bitmap = BitmapFactory.decodeStream(anInputStream);
		try {
			// TODO check that bitmap is power of two
			// TODO check that bitmap

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			final int error = aGL10.glGetError();
			if (error != 0) {
				Log.e(Texture.class.getName(), format("error is %d = %s", error, aGL10.glGetString(error)));
			}
		} finally {
			bitmap.recycle();
		}
	}

	public void freeTexture() {
		// release the texture
		if (textureAllocated) {
			gL10.glDeleteTextures(1, new int[] { textureId }, 0);
		}
		textureAllocated = false;
	}

	@Override
	protected void finalize() throws Throwable {
		freeTexture();
		// allow the super class to finalize too
		super.finalize();
	}
}
