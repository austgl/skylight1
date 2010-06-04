package skylight1.opengl;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Visibility;

public class CollisionDetector {
	public static interface CollisionObserver {
		void collisionOccurred(OpenGLGeometry anOpenGLGeometry);
	}

	private static final int NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE = 4;

	private List<OpenGLGeometry> listOfGeometries = new ArrayList<OpenGLGeometry>();

	private float[] boundingSpheres;

	private int[] collisionIndices;

	private List<CollisionObserver> collisionObservers = new ArrayList<CollisionObserver>();

	public void addCollisionObserver(CollisionObserver aCollisionObserver) {
		collisionObservers.add(aCollisionObserver);
	}

	public void addGeometry(OpenGLGeometry anOpenGLGeometry) {
		listOfGeometries.add(anOpenGLGeometry);
		if (boundingSpheres == null) {
			boundingSpheres = new float[NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE];
		} else {
			final float[] enlargedBoundingSpheres = new float[boundingSpheres.length + NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE];
			System.arraycopy(boundingSpheres, 0, enlargedBoundingSpheres, 0, boundingSpheres.length);
			boundingSpheres = enlargedBoundingSpheres;
		}

		float[] addedBoundingSphere = anOpenGLGeometry.getBoundingSphere();
		collisionIndices = new int[boundingSpheres.length / NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE];
		System
				.arraycopy(addedBoundingSphere, 0, boundingSpheres, boundingSpheres.length - NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE, NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE);
	}

	public void addGeometries(List<OpenGLGeometry> aListOfOpenGLGeometries) {
		for (OpenGLGeometry openGLGeometry : aListOfOpenGLGeometries) {
			addGeometry(openGLGeometry);
		}
	}

	public void detectCollisions(float[] aBoundingBox) {
		// find the indices of all of the collisions
		int numberOfCollisions =
				Visibility.frustumCullSpheres(aBoundingBox, 0, boundingSpheres, 0, collisionIndices.length, collisionIndices, 0, collisionIndices.length);

		// notify the observers of any collisions
		for (int collisionIndicesIndex = 0; collisionIndicesIndex < numberOfCollisions; collisionIndicesIndex++) {
			final int indexOfCollidedGeometry = collisionIndices[collisionIndicesIndex];
			final OpenGLGeometry collidedGeometry = listOfGeometries.get(indexOfCollidedGeometry);
			for (int observerIndex = 0; observerIndex < collisionObservers.size(); observerIndex++) {
				final CollisionObserver collisionObserver = collisionObservers.get(observerIndex);
				collisionObserver.collisionOccurred(collidedGeometry);
			}
		}
	}
}
