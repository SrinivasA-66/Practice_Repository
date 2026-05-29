package com.cts.openbankx.service;

import com.cts.openbankx.model.APIProduct;
import com.cts.openbankx.repository.APIPlanRepository;
import com.cts.openbankx.repository.APIProductRepository;
import com.cts.openbankx.repository.TPPSubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class APIProductService {

    private final APIProductRepository repo;
    private final APIPlanRepository planRepo;
    private final TPPSubscriptionRepository subscriptionRepo;

    public APIProductService(APIProductRepository repo,
                             APIPlanRepository planRepo,
                             TPPSubscriptionRepository subscriptionRepo) {
        this.repo = repo;
        this.planRepo = planRepo;
        this.subscriptionRepo = subscriptionRepo;
    }

    public List<APIProduct> findAll() {
        return repo.findAll();
    }

    public APIProduct findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException("API Product not found: " + id));
    }

    public APIProduct save(APIProduct product) {

        repo.findByName(product.getName())
            .ifPresent(existing -> {
                if (product.getProductId() == null ||
                    !existing.getProductId().equals(product.getProductId())) {
                    throw new IllegalArgumentException(
                        "API Product with this name already exists");
                }
            });

        return repo.save(product);
    }


    @Transactional
    public void delete(Long id) {
        subscriptionRepo.deleteAllByProductId(id);
        planRepo.deleteAllByProductId(id);
        repo.deleteById(id);
    }
}