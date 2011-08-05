package skylight1.opengl;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Encapsulates an OpenGL texture, with facilities for loading, activating, deactivating, and freeing. Warning: if mip
 * maps are used, which is the default, extra texture space is consumed and additional textures may result in a major
 * impact to rendering performance.
 */
public class Texture {
	private final boolean usesMipMap;
	private final int textureResource;
	private final Context context;
	
	private int textureId;

	private GL10 gL10;

	private boolean textureAllocated;

	/**
	 * Load a texture from a bitmap. Use a mip map.
	 */
//	public Texture(final GL10 aGL10, final Bitmap aBitmap) {
//		this(aGL10, aBitmap, true);
//	}

	/**
	 * Load a texture from a bitmap.
	 */
//	public Texture(final GL10 aGL10, final Bitmap aBitmap, final boolean aUsesMipMap) {
//		usesMipMap = aUsesMipMap;
//		loadBitmap(aGL10, aBitmap);
//	}

//	/**
//	 * Load a texture from an input stream. Use a mip map.
//	 */
//	public Texture(final GL10 aGL10, final InputStream anInputStream) {
//		this(aGL10, anInputStream, true);
//	}

//	/**
//	 * Load a texture from an input stream.
//	 */
//	public Texture(final GL10 aGL10, final InputStream anInputStream, final boolean aUsesMipMap) {
//		loadBitmap(aGL10, anInputStream, aUsesMipMap);
//	}

	/**
	 * Load a texture from a resource. Use a mip map.
	 */
	public Texture(final Context aContext, final int aTextureResource) {
		this(aContext, aTextureResource, true);
	}

	/**
	 * Load a texture from a resource.
	 */
	public Texture(final Context aContext, final int aTextureResource, final boolean aUsesMipMap) {
		context = aContext;
		usesMipMap = aUsesMipMap;
		textureResource = aTextureResource;
	}
	
	public void load(final GL10 aGL10) {
		final InputStream inputStream = context.getResources().openRawResource(textureResource);
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
	public void activate() {
		gL10.glEnable(GL10.GL_TEXTURE_2D);
		gL10.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
	}

	/**
	 * Deactivate this texture. Only required if the next rendering requires that no texture be active.
	 */
	public void deactivate() {
		// TODO is this method really needed!!!? OpenGL seems to push out the LRU texture anyway
		gL10.glDisable(GL10.GL_TEXTURE_2D);
	}

	/**
	 * Free this texture. Only required if the texture must be deleted from OpenGL sooner than this object being GC'd.
	 */
	public void free() {
		// release the texture
		if (textureAllocated) {
			try {
				gL10.glDeleteTextures(1, new int[] { textureId }, 0);
			} catch (Exception e) {
				// if the texture is no longer extant and the GLWrapper is in place, then a harmless exception is thrown
			}
		}
		textureAllocated = false;
	}
	
	public int getTextureId() {
		return textureId;
	}

	@Override
	protected void finalize() throws Throwable {
		free();

		// allow the super class to finalize too
		super.finalize();
	}

	private void loadBitmap(final GL10 aGL10, final InputStream anInputStream) {
		// create the bitmap
		final Bitmap bitmap = BitmapFactory.decodeStream(anInputStream);
		try {
			loadBitmap(aGL10, bitmap);
		} finally {
			// free up the bitmap
			bitmap.recycle();
		}
	}

	private void loadBitmap(final GL10 aGL10, final Bitmap aBitmap) {
		// save for later
		gL10 = aGL10;

		// generate a texture
		final int[] textures = new int[1];
		aGL10.glGenTextures(textures.length, textures, 0);
		textureId = textures[0];
		textureAllocated = true;

		// bind and activate the texture
		activate();
		
		int results[] = new int[2];
		aGL10.glGetIntegerv(GL10.GL_MAX_TEXTURE_UNITS, results, 0);
		aGL10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, results, 1);
		Log.i(Texture.class.getName(), String.format("available texture units %d, max side %d", results[0], results[1]));

		// TODO does it make sense to set these this early?
		// TODO does this need to be decided by the client?
		// TODO does this belong to the texture or the geometry?
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				usesMipMap ? GL10.GL_LINEAR_MIPMAP_NEAREST : GL10.GL_NEAREST);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		aGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		// TODO check that bitmap is a power of two

		// if not use mip map, then the bitmap is passed to OpenGL just the once
		if (!usesMipMap) {
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, aBitmap, 0);
		} else {
			// otherwise (use mip map), the bitmap is passed repeatedly, each time halved along each side, until it is
			// only one pixel along its shortest dimension
			float scale = 1;
			int level = 0;
			int scaledBitmapWidth;
			int scaledBitmapHeight;
			// TODO not sure falling back to RGB_565 is the best answer... don't really understand why some bitmaps
			// don't have a config
			final Config config = aBitmap.getConfig() == null ? Bitmap.Config.RGB_565 : aBitmap.getConfig();
			do {
				// try three times to create a bitmap
				Bitmap scaledBitmap = null;
				for (int attempt = 0; attempt < 3 && scaledBitmap == null; attempt++) {
					try {
						scaledBitmap = Bitmap.createBitmap(aBitmap.getWidth() >>> level, aBitmap.getHeight() >>> level, config);
					} catch (OutOfMemoryError e) {
						System.gc();
					}
				}
				final Canvas canvas = new Canvas(scaledBitmap);
				canvas.scale(scale, scale);
				canvas.drawBitmap(aBitmap, 0, 0, null);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, scaledBitmap, 0);

				// Recycle the scaled bitmap we just created, but keep track of how big it was.
				scaledBitmapWidth = scaledBitmap.getWidth();
				scaledBitmapHeight = scaledBitmap.getHeight();
				scaledBitmap.recycle();
				scaledBitmap = null;

				// halve for each new level
				scale = scale / 2f;
				level++;

				// keep going until one side of the texture reaches one pixel
			} while (scaledBitmapWidth > 1 && scaledBitmapHeight > 1);
		}

		// check for an error
		final int error = aGL10.glGetError();
		if (error != 0) {
			throw new RuntimeException(format("Error loading bitmap as texture =%d: %s", error, aGL10
					.glGetString(error)));
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s: texture id = %d", super.toString(), textureId);
	}
}
