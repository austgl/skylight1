package org.skylight1.neny.server;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.skylight1.neny.model.Address;
import org.skylight1.neny.model.Borough;
import org.skylight1.neny.model.Grade;
import org.skylight1.neny.model.Restaurant;

public final class EMF {
	private static final EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("transactions-optional");

	private EMF() {
	}

	static {
		for (final Restaurant r : new Restaurant[] {
				new Restaurant("234234", "Timothy's dodgy bagels", Borough.MANHATTAN, new Address("163", "163 William St", "10001"), "212-555-5555", "01", Grade.GRADE_C, new Date()),
				new Restaurant("237654", "Morrison's street meats", Borough.QUEENS, new Address("34", "Main St", "10001"), "212-555-2344", "03", Grade.GRADE_A, new Date()) }) {
			final EntityManager em = emfInstance.createEntityManager();
			try {
				em.persist(r);
				System.out.println("just saved " + r);
			} finally {
				em.close();
			}
		}
	}

	public static EntityManagerFactory get() {
		return emfInstance;
	}
}