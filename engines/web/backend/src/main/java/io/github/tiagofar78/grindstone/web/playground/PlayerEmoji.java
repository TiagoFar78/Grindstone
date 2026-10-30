package io.github.tiagofar78.grindstone.web.playground;

import java.util.concurrent.ThreadLocalRandom;

public enum PlayerEmoji {

    HAPPY("happy"),         // 😀
    SWAG("swag"),           // 😎
    NERD("nerd"),           // 🤓
    COWBOY("cowboy"),       // 🤠
    PARTY("party"),         // 🥳
    ROBOT("robot"),         // 🤖
    GHOST("ghost"),         // 👻
    ALIEN("alien"),         // 👽
    CAT("cat"),             // 🐱
    DOG("dog"),             // 🐶
    FOX("fox"),             // 🦊
    PANDA("panda"),         // 🐼
    FROG("frog"),           // 🐸
    MONKEY("monkey"),       // 🐵
    UNICORN("unicorn"),     // 🦄
    OCTOPUS("octopus"),     // 🐙
    LION("lion"),           // 🦁
    TIGER("tiger"),         // 🐯
    PENGUIN("penguin"),     // 🐧
    OWL("owl"),             // 🦉
    PIZZA("pizza"),         // 🍕
    BURGER("burger"),       // 🍔
    DONUT("donut"),         // 🍩
    SOCCER("soccer");       // ⚽

    private String code;

    private PlayerEmoji(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }

    public static boolean emojiExists(String code) {
        if (code == null) {
            return false;
        }

        for (PlayerEmoji emoji : values()) {
            if (emoji.code.equals(code)) {
                return true;
            }
        }

        return false;
    }

    public static PlayerEmoji random() {
        return values()[ThreadLocalRandom.current().nextInt(values().length)];
    }

}
