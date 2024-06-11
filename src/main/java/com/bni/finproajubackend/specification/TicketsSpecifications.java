package com.bni.finproajubackend.specification;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.domain.Specification;

public class TicketsSpecifications {
    public static Specification<Tickets> orderByStatus() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(
                    criteriaBuilder.selectCase()
                            .when(criteriaBuilder.equal(root.get("ticketStatus"), TicketStatus.Diajukan), 1)
                            .when(criteriaBuilder.equal(root.get("ticketStatus"), TicketStatus.DalamProses), 2)
                            .when(criteriaBuilder.equal(root.get("ticketStatus"), TicketStatus.Selesai), 3)
                            .otherwise(4)
            ));
            return null;
        };
    }
}

