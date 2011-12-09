/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.creeptd.client.i18n;

import java.net.URL;

/**
 * A language definition.
 *
 * @author Daniel
 */
public class Language {
    private String key;
    private String name;

    /**
     * Create new language structure.
     *
     * @param key The language's key
     * @param name The language's name
     */
    protected Language(String key, String name) {
        this.key = key;
        this.name = name;
    }

    /**
     * Get the language's key.
     *
     * @return The key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Get the language's name.
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the language icon's URL.
     *
     * @return The icon's URL
     */
    public URL getIconURL() {
        return getIconURLForLanguage(this.key);
    }

    /**
     * Get the language icon's URL.
     *
     * @param key The language's key
     * @return The icon's URL
     */
    public static URL getIconURLForLanguage(String key) {
        return Language.class.getClassLoader().getResource("com/creeptd/client/resources/i18n/icons/en_US.gif");
    }
}
