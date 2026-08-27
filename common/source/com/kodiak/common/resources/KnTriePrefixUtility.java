/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;

import com.kodiak.logger.KnLogger;

import java.util.HashMap;
import java.util.Map;


/**
 * Prefix table based on trie structure. Allows to perform incremental lookup
 * and match based on search key prefixes (classic example - determine phone
 * area code for given phone number)
 *
 * @param <V> a type of value object to be stored along with prefix (e.g when
 *            key is a country name, the value could be a name of the country)
 */
public class KnTriePrefixUtility<V> {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTriePrefixUtility.class);
    private final String className = KnTriePrefixUtility.class.getName();
    private Entry<V> entry;
    private char key;
    private Map<Character, KnTriePrefixUtility<V>> childrens;

    public KnTriePrefixUtility() {
        this.childrens = new HashMap<Character, KnTriePrefixUtility<V>>(10);
        entry = new Entry<V>();
    }

    /**
     * non-public, used by put()
     *
     * @param key char
     */
    KnTriePrefixUtility(char key) {
        this.childrens = new HashMap<Character, KnTriePrefixUtility<V>>(10);
        this.key = key;
        entry = new Entry<V>();
    }

    public void put(String key, V value) {
        put(new StringBuffer(key), new StringBuffer(""), value);
    }

    private void put(StringBuffer remainder, StringBuffer prefix, V value) {
        if (remainder!= null && remainder.length() > 0) {
            char keyElement = remainder.charAt(0);
            KnTriePrefixUtility<V> t = null;
            try {
                t = childrens.get(keyElement);
            } catch (IndexOutOfBoundsException e) {
                knLogger.error( "put", "Exception occurred !!", e);

            }
            if (t == null) {
                t = new KnTriePrefixUtility<V>(keyElement);
                childrens.put(keyElement, t);
            }
            prefix.append(remainder.charAt(0));
            t.put(remainder.deleteCharAt(0), prefix, value);
        } else {
            this.entry.value = value;
            this.entry.prefix = prefix.toString();
        }

    }


    /**
     * Retrieves element from prefix table matching as a prefix to provided key.
     * E.g. is key is "abcde" and prefix table has node "ab" then this call will
     * return "ab"
     *
     * @param key a string which starts with prefix to be searched in the table
     *            (e.g. phone number)
     * @return an Object assosiated with matching prefix (i.e if key is a phone
     *         number it may return a corresponding country name)
     */
    public V get(String key) {
        return get(new StringBuffer(key), 0, null);
    }

    /**
     * Returns true if key has matching prefix in the table
     *
     * @param key String
     * @return boolean
     */
    public boolean hasPrefix(String key) {
        return (this.get(key) != null);
    }

    private V get(StringBuffer key, int level, V finalValue) {
        if (key.length() > 0) {
            KnTriePrefixUtility<V> triePrefix = childrens.get(key.charAt(0));
            knLogger.debug( "get()", " Trie :: ", triePrefix);

            if (triePrefix != null) {
                if (triePrefix.entry.prefix() != null) {
                    finalValue = triePrefix.entry.value();
                }
                return triePrefix.get(key.deleteCharAt(0), ++level, finalValue);
            } else {
                knLogger.info( "get()", "level :: ", level, "   finalValue ::", finalValue);
                return (level > 0) ? finalValue : null;
            }

        } else {
            knLogger.info( "get()", "finalValue ::", finalValue);
            return finalValue;

        }
    }

    //@Override
    public String toString() {
       // return "Trie [entry=" + entry + ", key=" + key + ", childrens=" + childrens + "]";
         return entry.toString();
    }

    static class Entry<V> {
        private String prefix;
        private V value;

        public String prefix() {
            return prefix;
        }

        public V value() {
            return value;
        }

        public String toString() {
            return "Entry [prefix=" + prefix + ", value=" + value + "]";
        }

    }
}

