package io.github.tiagofar78.grindstone.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public interface PlayerLocaleService {
    
    static PlayerLocaleService getDefaultPlayerLocaleService() {
        return new PlayerLocaleService() {
            
            private Map<UUID, Locale> playerLocale = new HashMap<>();
            
            @Override
            public void register(UUID id, Locale locale) {
                playerLocale.put(id, locale);
            }
            
            @Override
            public Locale getLocale(UUID id) {
                return playerLocale.getOrDefault(id, Locale.ENGLISH);
            }
        };
    }

    void register(UUID id, Locale locale);

    Locale getLocale(UUID id);

}
