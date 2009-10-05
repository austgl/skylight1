package net.nycjava.skylight1.service;

import java.util.concurrent.Future;

public interface CompassService {
	Future<Float> getCompassReading();
}
