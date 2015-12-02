package ca.ulaval.glo4003.labo.concurrence;

public interface RealEstateRepository {

	public RealEstate findById(String id);

	public RealEstate findAll();

	public void save(RealEstate realEstate);

	public void remove(String id);
}
