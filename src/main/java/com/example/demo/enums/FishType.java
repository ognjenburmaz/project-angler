package com.example.demo.enums;

public enum FishType {
    ZANDER("fish.zander"),
    CATFISH("fish.catfish"),
    PERCH("fish.perch"),
    ASP("fish.asp"),
    PIKE("fish.pike"),
    COMMON_CARP("fish.common.carp"),
    BREAM("fish.bream"),
    PRUSSIAN_CARP("fish.prussian.carp"),
    BARBEL("fish.barbel"),
    COMMON_NASE("fish.common.nase");

    private final String messageKey;

    FishType(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}