package com.bni.finproajubackend.model.enumobject;

import lombok.Getter;

public enum TicketStatus {
    Diajukan(1), DalamProses(2), Selesai(3);

    private final int order;

    TicketStatus(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
