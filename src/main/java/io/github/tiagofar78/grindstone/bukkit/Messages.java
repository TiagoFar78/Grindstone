package io.github.tiagofar78.grindstone.bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private static final String BUNDLE_NAME = "lang.messages";

    public static Component translate(Locale locale, String key, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);

        String pattern = bundle.getString(key);
        String formatted = MessageFormat.format(pattern, args);

        return MINI.deserialize(formatted);
    }

    public static Component format(String message, Object... args) {
        String formatted = MessageFormat.format(message, args);
        return MINI.deserialize(formatted);
    }

    public static Component deserialize(String str) {
        return MINI.deserialize(str);
    }

    public static String raw(Locale locale, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        return bundle.getString(key);
    }

}
