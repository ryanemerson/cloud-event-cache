package io.gingersnap.project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

@Singleton
public class Cache {

   final Map<String, String> map = new ConcurrentHashMap<>();

   public String get(String key) {
      return map.get(key);
   }

   public void put(String key, String value) {
      map.put(key, value);
   }
}
