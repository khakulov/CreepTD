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

import com.creeptd.client.Core;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
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
    private final static String DEFAULT_LANGUAGE_KEY = "en_US";

    private static final Logger logger = Logger.getLogger(Translator.class.getName());
    private String languageKey = DEFAULT_LANGUAGE_KEY;
    private String languageName;
    private Map<String,String> translations;
    private Map<String,String> availableLanguages = null;
    
    /**
     * Get a list of all languages that are available.
     */
    public void loadAvailableLanguages() {
        if (this.availableLanguages != null) return;
        this.availableLanguages = new HashMap<String,String>();
        /* URL dirURL = this.getClass().getClassLoader().getResource("com/creeptd/client/i18n/languages");
        File dir = new File(dirURL.getPath());
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i=0; i<files.length; i++) {
                File file = files[i];
                if (file.getName().endsWith(".txt")) {
                    String key = file.getName().substring(0, file.getName().length()-4);
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        String value = reader.readLine();
                        reader.close();
                        this.availableLanguages.put(key, value);
                    } catch (IOException ex) {
                        logger.warning("Unable to check for available language on file "+file+": "+ex);
                    }
                }
            }
        } */
        this.availableLanguages.put("de_DE", "Deutsch (DE)");
        this.availableLanguages.put("en_US", "English (US)");
        String msg = "";
        Iterator<String> i = this.availableLanguages.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            if (msg.length() > 0) msg +=", ";
            msg += this.availableLanguages.get(key)+" ("+key+")";
        }
        logger.info("Available languages are: "+msg);
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
        this.loadAvailableLanguages();
        if (!this.setLanguageByKey(languageKey)) {
            this.setLanguageByKey(DEFAULT_LANGUAGE_KEY);
        }
    }
    
    /**
     * Get a map of all languages available.
     *
     * @return Available languages as key/name pair
     */
    public Map<String,String> getAvailableLanguages() {
        return new HashMap<String,String>(this.availableLanguages);
    }

    /**
     * Set the current language by key.
     *
     * @param key The language's key
     * @return true on success, else false
     */
    public boolean setLanguageByKey(String key) {
        logger.info("Setting current language to "+key);
        if (this.availableLanguages.containsKey(key)) {
            this.languageKey = key;
            this.languageName = this.availableLanguages.get(key);
            loadTranslations();
            return true;
        }
        logger.warning("Language not found: "+key);
        return false;
    }

    /**
     * Set the current language by name.
     *
     * @param String The language's name
     * @return true on success, else false
     */
    public boolean setLanguageByName(String name) {
        if (this.availableLanguages.containsValue(name)) {
            Iterator<String> i = this.availableLanguages.keySet().iterator();
            while (i.hasNext()) {
                String key = i.next();
                if (this.availableLanguages.get(key).equals(name)) {
                    return setLanguageByKey(key);
                }
            }
        }
        return false;
    }

    /**
     * Get the current language's key, e.g. "EN"
     * @return Language key
     */
    public String getLanguageKey() {
        return this.languageKey;
    }
    /**
     * Get the current language's name, e.g. "English"
     * @return Language name
     */
    public String getLanguageName() {
        return this.languageName;
    }

    /**
     * Get language icon for given language key.
     *
     * @param language
     * @return The icon URL
     */
    public URL getLanguageIconURL(String languageKey) {
        return this.getClass().getClassLoader().getResource("com/creeptd/client/i18n/languages/"+languageKey+".gif");
    }

    /**
     * Load translations for given language.
     */
    private void loadTranslations() {
        this.translations = new HashMap<String,String>();
        try {
            logger.info("Loading language "+this.languageKey+"...");
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/i18n/languages/"+languageKey+".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int lineNo = 1;
            while (reader.ready()) {
                String line = reader.readLine();
                if (lineNo == 1) {
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
            logger.warning("Failed to load translations for language "+this.languageKey+": "+ex);
        }
        logger.info("Loaded "+this.translations.size()+" translations for language "+this.languageKey);
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
     * Translate given text.
     *
     * @param s Text to translate
     * @return Translated text
     */
    public static String _(String text) {
        Translator t = Core.getInstance().getTranslator();
        return t.translate(text);
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
