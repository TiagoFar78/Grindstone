package io.github.tiagofar78.grindstone.game;

import io.github.tiagofar78.grindstone.services.SchedulerService;
import io.github.tiagofar78.grindstone.services.PlayerLocaleService;
import io.github.tiagofar78.grindstone.services.TranslationService;

public class GameDependencies {
    
    private PlayerLocaleService playerLocaleService;
    private TranslationService translationService;
    private SchedulerService schedulerService;
    
    public GameDependencies(SchedulerService ss) {
        playerLocaleService = PlayerLocaleService.getDefaultPlayerLocaleService();
        translationService = TranslationService.getDefaultTranslator();
        schedulerService = ss;
    }
    
    public GameDependencies(PlayerLocaleService pls, TranslationService ts, SchedulerService ss) {
        playerLocaleService = pls;
        translationService = ts;
        schedulerService = ss;
    }
    
    public PlayerLocaleService getPlayerLocaleService() {
        return playerLocaleService;
    }
    
    public TranslationService getTranslationService() {
        return translationService;
    }
    
    public SchedulerService getSchedulerService() {
        return schedulerService;
    }
    
}
