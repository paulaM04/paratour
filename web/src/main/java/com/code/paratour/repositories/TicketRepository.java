package com.code.paratour.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code.paratour.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    void deleteByInstanceId(Long instanceId);
}

