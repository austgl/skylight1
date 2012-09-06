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
				new Restaurant("234234", "Timothy's dodgy bagels", Borough.MANHATTAN, new Address("163", "163 William St", "10001"), "212-555-5555", "07", Grade.GRADE_C, new Date()),
				new Restaurant("237654", "Morrison's street meats", Borough.QUEENS, new Address("34", "Main St", "10001"), "212-555-2344", "39", Grade.GRADE_A, new Date()),
				new Restaurant("238451", "Dario's dairy products", Borough.BROOKLYN, new Address("514", "2nd Street", "11215"), "718-400-3253", "23", Grade.GRADE_A, new Date()),
		        new Restaurant("234215", "Mark's marvelous milkshakes", Borough.BROOKLYN, new Address("78", "Prospect Park West", "11215"), "718-555-1212", "43", Grade.GRADE_PENDING, new Date() ),
		        new Restaurant("253659", "Simon's selected sandwiches", Borough.MANHATTAN, new Address("881", "Seventh Avenue", "10019"), "212-903-9600","32", Grade.GRADE_A, new Date()),
		        new Restaurant("323454", "Vince's vegetable garden", Borough.STATEN_ISLAND, new Address("323", "Victory Boulevard", "10301"), "718-420-0910", "84", Grade.GRADE_C, new Date()),
				new Restaurant("435281", "Fera's Falafels", Borough.QUEENS, new Address("29-10", "Broadway", "11106"), "718-838-8029", "56", Grade.GRADE_A, new Date())
		}) {
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