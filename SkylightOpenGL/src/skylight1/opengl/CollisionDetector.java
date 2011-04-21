package skylight1.opengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.opengl.Visibility;

public class CollisionDetector {
	private static final int DEFAULT_INITIAL_CAPACITY = 50;

	public static interface CollisionObserver {
		void collisionOccurred(float[] aBoundingSphere);
	}

	private static final int NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE = 4;

	private final List<float[]> listOfBoundingSpheres;

	private float[] boundingSpheres = new float[0];

	private int[] collisionIndices = new int[0];

	private final Map<float[], CollisionObserver> collisionObservers = new HashMap<float[], CollisionObserver>();

	public CollisionDetector(int anInitialCapacityForGeometries) {
		listOfBoundingSpheres = new ArrayList<float[]>(anInitialCapacityForGeometries);
	}

	public CollisionDetector() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	public void addBoundingSphere(float[] aBoundingSphere, CollisionObserver aCollisionObserver) {
		// TODO (TF) this would be a good place to consider not recreating the array again and again...
		// possibly use varargs!

		listOfBoundingSpheres.add(aBoundingSphere);

		// enlarge the bounding sphere array
		final float[] enlargedBoundingSpheres = new float[boundingSpheres.length + NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE];
		System.arraycopy(boundingSpheres, 0, enlargedBoundingSpheres, 0, boundingSpheres.length);

		// put the new bounding sphere into the array of bounding spheres
		System.arraycopy(aBoundingSphere, 0, enlargedBoundingSpheres, boundingSpheres.length, NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE);

		// swap to the new one
		boundingSpheres = enlargedBoundingSpheres;
		collisionIndices = new int[listOfBoundingSpheres.size()];

		// add the collision observer
		collisionObservers.put(aBoundingSphere, aCollisionObserver);
	}
	
	public void removeBoundingSphere(float[] aBoundingSphere) {
		// find the index of the geometry in the list
		final int indexOfBoundingSphere = listOfBoundingSpheres.indexOf(aBoundingSphere);

		// TODO can we remove this for optimization
		if (indexOfBoundingSphere == -1) {
			throw new IllegalArgumentException("bounding sphere was not in the collision detector");
		}

		// null out the geometry from the list of bounding sphere
		listOfBoundingSpheres.set(indexOfBoundingSphere, null);

		// note, the bounding sphere is still in the list, but that's no slower
		// than it already was and it will be ignored when collisions with it are
		// detected
	}

	public void detectCollisions(float[] aBoundingBox) {
		// find the indices of all of the collisions
		int numberOfCollisions =
				Visibility.frustumCullSpheres(aBoundingBox, 0, boundingSpheres, 0, listOfBoundingSpheres.size(), collisionIndices, 0, collisionIndices.length);

		// notify the observers of any collisions
		for (int collisionIndicesIndex = 0; collisionIndicesIndex < numberOfCollisions; collisionIndicesIndex++) {
			final int indexOfCollidedBoundingSphere = collisionIndices[collisionIndicesIndex];
			final float[] collidedBoundingSphere = listOfBoundingSpheres.get(indexOfCollidedBoundingSphere);
			
			// if a bounding sphere was found (i.e., not previously removed), then invoke its collision observer 
			if (collidedBoundingSphere != null) {
				final CollisionObserver collisionObserver = collisionObservers.get(collidedBoundingSphere);
				collisionObserver.collisionOccurred(collidedBoundingSphere);
			}
		}
	}
}
