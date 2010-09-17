package skylight1.opengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.opengl.Visibility;

public class CollisionDetector {
	private static final int DEFAULT_INITIAL_CAPACITY = 50;

	public static interface CollisionObserver {
		void collisionOccurred(OpenGLGeometry anOpenGLGeometry);
	}

	private static final int NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE = 4;

	private final List<OpenGLGeometry> listOfGeometries;

	private float[] boundingSpheres;

	private int[] collisionIndices;

	private final Map<OpenGLGeometry, CollisionObserver> collisionObservers = new HashMap<OpenGLGeometry, CollisionObserver>();

	private int usedLengthOfArray;

	public CollisionDetector(int anInitialCapacityForGeometries) {
		boundingSpheres = new float[anInitialCapacityForGeometries * NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE];
		collisionIndices = new int[anInitialCapacityForGeometries];
		listOfGeometries = new ArrayList<OpenGLGeometry>(anInitialCapacityForGeometries);
	}

	public CollisionDetector() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	public void addGeometry(OpenGLGeometry anOpenGLGeometry, CollisionObserver aCollisionObserver) {
		// TODO (TF) this would be a good place to consider not recreating the array again and again...
		// possibly use varargs!

		listOfGeometries.add(anOpenGLGeometry);
		// if there is not any existing space left (from an earlier remove?) then enlarge the
		// bounding sphere array
		if (usedLengthOfArray < boundingSpheres.length) {
			final float[] enlargedBoundingSpheres = new float[boundingSpheres.length + NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE];
			System.arraycopy(boundingSpheres, 0, enlargedBoundingSpheres, 0, boundingSpheres.length);
			boundingSpheres = enlargedBoundingSpheres;
			collisionIndices = new int[listOfGeometries.size()];
		}

		// put the new bounding sphere into the geometry
		float[] addedBoundingSphere = anOpenGLGeometry.getBoundingSphere();
		System.arraycopy(addedBoundingSphere, 0, boundingSpheres, usedLengthOfArray, NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE);
		usedLengthOfArray += NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE;

		// add the collision observer
		collisionObservers.put(anOpenGLGeometry, aCollisionObserver);
	}

	public void removeGeometry(OpenGLGeometry anOpenGLGeometry) {
		// find the index of the geometry in the list
		final int indexOfGeometry = listOfGeometries.indexOf(anOpenGLGeometry);

		// TODO can we remove this for optimization
		if (indexOfGeometry == -1) {
			throw new IllegalArgumentException("geometry was not in the collision detector");
		}

		// find the index of the last item
		final int indexOfLastItem = listOfGeometries.size() - 1;

		// move the last item to the position of the one being removed
		listOfGeometries.set(indexOfGeometry, listOfGeometries.get(indexOfLastItem));
		listOfGeometries.remove(indexOfLastItem);

		// move the last bounding sphere in the array to the newly opened up spot
		usedLengthOfArray -= NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE;
		System
				.arraycopy(boundingSpheres, usedLengthOfArray, boundingSpheres, indexOfGeometry * NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE, NUMBER_OF_FLOATS_PER_BOUNDING_SPHERE);
	}

	public void addGeometries(Map<OpenGLGeometry, CollisionObserver> aMapOfOpenGLGeometriesToObservers) {
		for (Map.Entry<OpenGLGeometry, CollisionObserver> openGLGeometryAndItsObserver : aMapOfOpenGLGeometriesToObservers.entrySet()) {
			addGeometry(openGLGeometryAndItsObserver.getKey(), openGLGeometryAndItsObserver.getValue());
		}
	}

	public void detectCollisions(float[] aBoundingBox) {
		// find the indices of all of the collisions
		int numberOfCollisions =
				Visibility.frustumCullSpheres(aBoundingBox, 0, boundingSpheres, 0, listOfGeometries.size(), collisionIndices, 0, collisionIndices.length);

		// notify the observers of any collisions
		for (int collisionIndicesIndex = 0; collisionIndicesIndex < numberOfCollisions; collisionIndicesIndex++) {
			// FIXME if a remove is called by the observer, then the indices are all out, and the next line causes an
			// arrayoutofboundsexception
			final int indexOfCollidedGeometry = collisionIndices[collisionIndicesIndex];
			final OpenGLGeometry collidedGeometry = listOfGeometries.get(indexOfCollidedGeometry);
			final CollisionObserver collisionObserver = collisionObservers.get(collidedGeometry);
			collisionObserver.collisionOccurred(collidedGeometry);
		}
	}
}
