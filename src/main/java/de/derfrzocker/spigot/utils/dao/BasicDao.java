package de.derfrzocker.spigot.utils.dao;

import java.util.Optional;
import java.util.Set;

public interface BasicDao<K, V> {

    Optional<V> get(K key);

    void remove(V value);

    void save(V value);

    Set<V> getAll();

}
