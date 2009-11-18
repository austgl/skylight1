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

/**
 * Encapsultes an OpenGL texture, with facilities for loading, activating, deactivating, and freeing.
 */
public class Texture {
	private int textureId;

	private GL10 gL10;

	private boolean textureAllocated;

	/**
	 * Load a texture from an input stream.
	 */
	public Texture(GL10 aGL10, InputStream anInputStream) {
		loadBitmap(aGL10, anInputStream);
	}

	/**
	 * Load a texture from a resource.
	 */
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

	/**
	 * Activate this texture. This is <b>not</b> required if the texture was the last texture loaded, as loading a
	 * texture automatically activates it.
	 */
	public void activateTexture() {
		gL10.glEnable(GL10.GL_TEXTURE_2D);
		gL10.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
	}

	/**
	 * Deactivate this texture. Only required if the next rendering requires that no texture be active.
	 */
	public void deactivateTexture() {
		// TODO is this method really needed!!!? OpenGL seems to push out the LRU texture anyway
		gL10.glDisable(GL10.GL_TEXTURE_2D);
	}

	/**
	 * Free this texture. Only required if the texture must be deleted from OpenGL sooner than this object being GC'd.
	 */
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

	private void loadBitmap(final GL10 aGL10, final InputStream anInputStream) {
		// save for later
		gL10 = aGL10;

		// generate a texture
		final int[] textures = new int[1];
		aGL10.glGenTextures(textures.length, textures, 0);
		textureId = textures[0];
		textureAllocated = true;

		// bind and activate the texture
		activateTexture();

		// TODO does it make sense to set these this early?
		// TODO does this need to be decided by the client?
		// TODO does this belong to the texture or the geometry?
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		// create the bitmap
		final Bitmap bitmap = BitmapFactory.decodeStream(anInputStream);
		try {
			// TODO check that bitmap is power of two
			// TODO check that bitmap

			// load the bitmap
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

			// check for an error
			final int error = aGL10.glGetError();
			if (error != 0) {
				Log.e(Texture.class.getName(), format("error is %d = %s", error, aGL10.glGetString(error)));
			}
		} finally {
			// free up the bitmap
			bitmap.recycle();
		}
	}
}
