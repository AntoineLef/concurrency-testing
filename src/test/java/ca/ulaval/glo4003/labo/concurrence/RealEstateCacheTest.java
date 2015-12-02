package ca.ulaval.glo4003.labo.concurrence;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RealEstateCacheTest {
	private static final String REAL_ESTATE_ID = "uLaval";

	@Mock
	private RealEstateRepository realEstateRepository;
	@Mock
	private RealEstate realEstate;

	private RealEstateCache realEstateCache;

	@Before
	public void setUp() {
		realEstateCache = new RealEstateCache(realEstateRepository);
		BDDMockito.given(realEstateRepository.findById(REAL_ESTATE_ID))
				.willReturn(realEstate);

	}

	@Test
	public void givenRealEstateNotInCache_whenFindById_thenFetchInRepository()
			throws Exception {
		// given

		// when
		RealEstate realEstateFromCache = realEstateCache
				.getRealEstate(REAL_ESTATE_ID);

		// then
		Mockito.verify(realEstateRepository, times(1)).findById(REAL_ESTATE_ID);
		assertThat(realEstateFromCache,
				org.hamcrest.CoreMatchers.is(realEstate));
	}

	@Test
	public void givenRealEstateInCache_whenFindById_thenUseCache()
			throws Exception {
		// given
		realEstateCache.getRealEstate(REAL_ESTATE_ID);

		// when
		RealEstate realEstateFromCache = realEstateCache
				.getRealEstate(REAL_ESTATE_ID);

		// then
		Mockito.verify(realEstateRepository, times(1)).findById(REAL_ESTATE_ID);
		assertThat(realEstateFromCache,
				org.hamcrest.CoreMatchers.is(realEstate));
	}
}
