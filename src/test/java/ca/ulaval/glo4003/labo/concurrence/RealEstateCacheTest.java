package ca.ulaval.glo4003.labo.concurrence;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;

@RunWith(MockitoJUnitRunner.class)
public class RealEstateCacheTest {
  private static final String REAL_ESTATE_ID = "uLaval";
  private static final int REFRESH_RATE = 500;
  private static final int RUN_COUNT = 200;

  @Mock
  private RealEstateRepository realEstateRepository;
  @Mock
  private RealEstate realEstate;

  private RealEstateCache realEstateCache;

  @Before
  public void setUp() {
    realEstateCache = new RealEstateCache(realEstateRepository, REFRESH_RATE);
    given(realEstateRepository.findById(REAL_ESTATE_ID)).willReturn(realEstate);
  }

  @Test
  public void givenRealEstateNotInCache_whenFindById_thenFetchInRepository() throws Exception {
    // given

    // when
    RealEstate realEstateFromCache = realEstateCache.getRealEstate(REAL_ESTATE_ID);

    // then
    Mockito.verify(realEstateRepository, times(1)).findById(REAL_ESTATE_ID);
    assertThat(realEstateFromCache, is(realEstate));
  }

  @Test
  public void givenRealEstateInCache_whenFindById_thenUseCache() throws Exception {
    // given
    realEstateCache.getRealEstate(REAL_ESTATE_ID);

    // when
    RealEstate realEstateFromCache = realEstateCache.getRealEstate(REAL_ESTATE_ID);

    // then
    Mockito.verify(realEstateRepository, times(1)).findById(REAL_ESTATE_ID);
    assertThat(realEstateFromCache, is(realEstate));
  }

  @Test
  public void givenMultipleThreadsAtTheSameTime_whenGetRealEstate_thenFetchOneTimeInRepository()
    throws Throwable
  {
    TestFramework.runManyTimes(new GivenMultipleThreadsAtTheSameTime_whenGetRealEstate_thenFetchOneTimeInRepository(),
                               RUN_COUNT);
  }

  class GivenMultipleThreadsAtTheSameTime_whenGetRealEstate_thenFetchOneTimeInRepository
    extends
      MultithreadedTestCase
  {
    @Override
    public void initialize() {
      reset(realEstateRepository);
      given(realEstateRepository.findById(REAL_ESTATE_ID)).willReturn(realEstate);
      realEstateCache = new RealEstateCache(realEstateRepository, REFRESH_RATE);
      realEstateCache.start();
      System.out.println("------");
    }

    public void thread1() {
      getRealEstate();
    }

    public void thread2() {
      getRealEstate();
    }

    public void thread3() {
      getRealEstate();
    }

    private void getRealEstate() {
      waitForTick(1);
      System.out.println(Thread.currentThread().getName() + " Getting real estate");
      RealEstate realEstateInCache = realEstateCache.getRealEstate(REAL_ESTATE_ID);

      assertThat(realEstateInCache, is(realEstate));
    }

    @Override
    public void finish() {
      realEstateCache.stop();
      verify(realEstateRepository, atMost(1)).findById(anyString());
    }
  }
}
