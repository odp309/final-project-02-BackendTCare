package com.bni.finproajubackend.model.enumobject;

import lombok.Getter;

@Getter
public enum StarRating {
    Satu(1), Dua(2), Tiga(3), Empat(4), Lima(5);

    private final int value;

    StarRating(int value) {
        this.value = value;
    }

    public static StarRating fromValue(int value) {
        for (StarRating rating : values()) {
            if (rating.value == value) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Unknown rating value: " + value);
    }
}

