package ca.ulaval.glo4003.labo.concurrence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealEstateCache {

  private Object lock = new Object();
  private int refreshRateInMs;

  private Map<String, RealEstate> realEstates = new ConcurrentHashMap<>();
  private RealEstateRepository realEstateRepository;
  private ScheduledExecutorService executor;

  public RealEstateCache(RealEstateRepository realEstateRepository, int RefreshRateInMs) {
    this.realEstateRepository = realEstateRepository;
    refreshRateInMs = RefreshRateInMs;
  }

  public RealEstate getRealEstate(String id) {
    RealEstate realEstate = realEstates.get(id);
    if (realEstate == null) {
      synchronized (lock) {
        realEstate = realEstates.get(id);
        if (realEstate == null) {
          realEstate = realEstateRepository.findById(id);
          realEstates.put(id, realEstate);
          System.out.println(Thread.currentThread().getName() + " Feed cache");
          return realEstate;
        }
        System.out.println(Thread.currentThread().getName() + " Cache already feed");
        return realEstate;
      }
    }
    System.out.println(Thread.currentThread().getName() + " Found in cache");
    return realEstate;
  }

  public synchronized void start() {
    if (executor == null) {
      executor = Executors.newScheduledThreadPool(1);
    }
    Runnable clearCache = new Runnable() {
      public void run() {
        clearCache();
        System.out.println(Thread.currentThread().getName() + " Cleared cache");
      }

    };

    executor.scheduleAtFixedRate(clearCache, 0, refreshRateInMs, TimeUnit.MILLISECONDS);
  }

  public synchronized void stop() {
    executor.shutdown();
  }

  public void clearCache() {
    synchronized (lock) {
      realEstates.clear();
    }
  }

}
