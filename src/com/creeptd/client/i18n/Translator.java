/**
CreepTD is an online multiplayer towerdefense game
formerly created under the name CreepSmash as a project
at the Hochschule fuer Technik Stuttgart (University of Applied Science)

CreepTD (Since version 0.7.0+) Copyright (C) 2011 by
 * Daniel Wirtz, virtunity media
http://www.creeptd.com

CreepSmash (Till version 0.6.0) Copyright (C) 2008 by
 * Andreas Wittig
 * Bernd Hietler
 * Christoph Fritz
 * Fabian Kessel
 * Levin Fritz
 * Nikolaj Langner
 * Philipp Schulte-Hubbert
 * Robert Rapczynski
 * Ron Trautsch
 * Sven Supper
http://creepsmash.sf.net/

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.creeptd.client.i18n;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Translates text similar to GetText.
 *
 * Import both the static methods _ and __ to the file, you want to use
 * internatialization in, e.g.:
 * import static com.creeptd.client.i18n.Translator.*;
 *
 * You are then able to use _(text) and __(text, placeholderMap) statically.
 *
 * @author Daniel
 */
public class Translator {
    public final static String DEFAULT_LANGUAGE_KEY = "en_US";

    private static final Logger logger = Logger.getLogger(Translator.class.getName());
    private Language language;
    private Map<String,String> translations;
    private List<Language> availableLanguages = new LinkedList<Language>();

    private static Translator instance;
    
    static {
    	instance = new Translator();
    	instance.setLanguageByKey(Translator.DEFAULT_LANGUAGE_KEY);
    }

    /**
     * Add a language.
     *
     * @param languageKey The language's key, e.g. en_US
     * @return The language descriptor or null on failure
     */
    private Language addLanguage(String languageKey) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/i18n/languages/"+languageKey+".txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String languageName = reader.readLine().trim();
            Language lang = new Language(languageKey, languageName);
            this.availableLanguages.add(lang);
            return lang;
        } catch (Exception ex) {
            logger.warning("Unable to get language name for key="+languageKey+": "+ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Create translator for default language.
     */
    public Translator() {
        this(DEFAULT_LANGUAGE_KEY);
    }

    /**
     * Create translator for language.
     * 
     * @param language The language to translate into
     */
    public Translator(String languageKey) {
        addLanguage(DEFAULT_LANGUAGE_KEY); // en_US
        addLanguage("en_GB");
        addLanguage("de_DE");
        addLanguage("de_AT");
        addLanguage("de_CH");
        addLanguage("nl_NL");
        addLanguage("fi_FI");
        if (!this.setLanguageByKey(languageKey)) {
            this.setLanguageByKey(DEFAULT_LANGUAGE_KEY);
        }
    }
    
    /**
     * Get a map of all languages available.
     *
     * @return Available languages as key/name pair
     */
    public List<Language> getAvailableLanguages() {
        return new LinkedList<Language>(this.availableLanguages);
    }

    /**
     * Set the current language by key.
     *
     * @param key The language's key
     * @return true on success, else false
     */
    public boolean setLanguageByKey(String key) {
        for (Language lang : this.availableLanguages) {
            if (lang.getKey().equalsIgnoreCase(key)) {
                this.language = lang;
                logger.info("Setting current language to: "+lang.getName());
                loadTranslations();
                return true;
            }
        }
        logger.warning("Language not found: key="+key);
        return false;
    }

    /**
     * Set the current language by name.
     *
     * @param String The language's name
     * @return true on success, else false
     */
    public boolean setLanguageByName(String name) {
        for (Language lang : this.availableLanguages) {
            if (lang.getName().equalsIgnoreCase(name)) {
                return setLanguageByKey(lang.getKey());
            }
        }
        logger.warning("Language not found: name="+name);
        return false;
    }

    /**
     * Get the current language
     * @return Language definition
     */
    public Language getLanguage() {
        return this.language;
    }

    /**
     * Load translations for given language.
     */
    private void loadTranslations() {
        this.translations = new HashMap<String,String>();
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/i18n/languages/"+this.language.getKey()+".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int lineNo = 1;
            while (reader.ready()) {
                String line = reader.readLine();
                if (lineNo == 1) {
                    lineNo++; continue;
                }
                if (line.startsWith(" ")) { // Blank start is interpreted as comment
                    lineNo++;
                    continue;
                } else if (line.startsWith("@import ")) { // @import start is an import
                    String depKey = line.substring(8).trim();
                    if (this.existsLanguage(depKey)) {
                        logger.info("Importing language "+depKey+" to "+this.language.getKey()+"...");
                        Language origLang = this.getLanguage();
                        this.setLanguageByKey(depKey);
                        loadTranslations();
                        this.language = origLang;
                    } else {
                        logger.warning("Unable to import language "+depKey+" to "+this.language.getKey()+": Language does not exist");
                    }
                    lineNo++; continue;
                }
                int pos = line.indexOf("=");
                if (pos >= 0) {
                    String key = line.substring(0, pos).trim();
                    String val = line.substring(pos+1).trim();
                    this.translations.put(key, val);
                }
                lineNo++;
            }
            reader.close();
        } catch (Exception ex) {
            this.translations.clear();
            logger.warning("Failed to load translations for language "+this.language.getKey()+": "+ex);
        }
        logger.info("Loaded "+this.translations.size()+" translations for language: "+this.language.getName());
    }

    /**
     * Translate a given text.
     *
     * @param s Text to translate
     * @return Translated text
     */
    public String translate(String s) {
        s = s.replace("\n", "\\n");
        s = s.replace("\t", "\\t");
        String trans = this.translations.get(s);
        if (trans != null) s = trans;
        s = s.replace("\\n", "\n");
        s = s.replace("\\t", "\t");
        return s;
    }

    /**
     * Test if a language exists.
     *
     * @param langKey The language's key
     * @return true if it exists, else false
     */
    public boolean existsLanguage(String langKey) {
        for (Language l : this.availableLanguages) {
            if (l.getKey().equals(langKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Translate given text.
     *
     * @param s Text to translate
     * @return Translated text
     */
    public static String _(String text) {
        return instance.translate(text);
    }

    /**
     * Translate given text with given place holders.
     *
     * Place holders may be used as %key% inside the translation texts. The
     * place holder map must then contain the key/value pair key=value.
     *
     * @param text Text to translate
     * @param replacements Place holder key/value map
     * @return Translated text
     */
    public static String _(String text, Map<String,String> replacements) {
        text = _(text);
        Iterator<String> i = replacements.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            text = text.replace("%"+key+"%", replacements.get(key));
        }
        return text;
    }
}
