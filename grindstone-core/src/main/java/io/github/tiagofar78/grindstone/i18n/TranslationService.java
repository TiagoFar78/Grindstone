package io.github.tiagofar78.grindstone.i18n;

import java.util.Locale;

public interface TranslationService {
    
    static TranslationService getDefaultTranslator() {
        return new TranslationService() {
            
            @Override
            public String translate(Locale locale, String key) {
                return key;
            }
            
            @Override
            public void register(String folderPath) {
                // Empty
            }
        };
    }

    void register(String folderPath);

    String translate(Locale locale, String key);

}
