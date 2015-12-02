package ca.ulaval.glo4003.labo.concurrence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealEstateCache {

	private Object lock = new Object();
	private int refreshRateInMs;

	protected Map<String, RealEstate> realEstates = new HashMap<>();
	private RealEstateRepository realEstateRepository;
	private ScheduledExecutorService executor;

	public RealEstateCache(RealEstateRepository realEstateRepository,
			int RefreshRateInMs) {
		this.realEstateRepository = realEstateRepository;
		refreshRateInMs = RefreshRateInMs;

		start();
	}

	public RealEstate getRealEstate(String id) {
		RealEstate realEstate = realEstates.get(id);
		if (realEstate == null) {
			synchronized (lock) {
				realEstate = realEstates.get(id);
				if (realEstate == null) {
					realEstate = realEstateRepository.findById(id);
					realEstates.put(id, realEstate);
				}
			}
		}
		return realEstate;
	}

	public void start() {
		executor = Executors.newScheduledThreadPool(1);
		Runnable clearCache = new Runnable() {
			public void run() {
				realEstates.clear();
			}
		};

		executor.scheduleAtFixedRate(clearCache, 0, refreshRateInMs,
				TimeUnit.MILLISECONDS);
	}

	public void stop() {
		executor.shutdown();
	}
}
