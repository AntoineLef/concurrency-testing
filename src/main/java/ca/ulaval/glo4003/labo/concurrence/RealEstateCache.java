package ca.ulaval.glo4003.labo.concurrence;

import java.util.HashMap;
import java.util.Map;

public class RealEstateCache {

	private Object lock = new Object();

	private Map<String, RealEstate> realEstates = new HashMap<>();
	private RealEstateRepository realEstateRepository;

	public RealEstateCache(RealEstateRepository realEstateRepository) {
		this.realEstateRepository = realEstateRepository;
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
}
