package com.heliumhq.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Collections {
	public static <K, V> Map<V, Set<K>> inverse(Map<K, Set<V>> map) {
		Map<V, Set<K>> result = new HashMap<V, Set<K>>();
		for (Map.Entry<K, Set<V>> entry : map.entrySet())
			for (V value : entry.getValue()) {
				if (! result.containsKey(value))
					result.put(value, new HashSet<K>());
				result.get(value).add(entry.getKey());
			}
		return result;
	}
}
