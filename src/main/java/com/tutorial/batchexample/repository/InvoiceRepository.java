package com.tutorial.batchexample.repository;

import com.tutorial.batchexample.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
