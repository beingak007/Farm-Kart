package com.farmkart.service.vendor;

import com.farmkart.starter.common.exception.BusinessException;
import com.farmkart.client.dto.vendor.VendorOnboardRequest;
import com.farmkart.client.dto.vendor.VendorResponse;
import com.farmkart.repository.entity.Vendor;
import com.farmkart.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Transactional
    public VendorResponse onboard(VendorOnboardRequest request) {
        vendorRepository.findByUserId(request.userId()).ifPresent(v -> {
            throw new BusinessException("Vendor profile already exists for this user");
        });

        Vendor vendor = new Vendor();
        vendor.setUserId(request.userId());
        vendor.setFarmName(request.farmName());
        vendor.setAddress(request.address());
        vendor.setState(request.state());
        vendor.setDistrict(request.district());
        vendor.setPincode(request.pincode());
        vendor.setBankAccount(request.bankAccount());
        vendor.setIfsc(request.ifsc());
        vendor.setKycDocumentUrl(request.kycDocumentUrl());
        vendorRepository.save(vendor);
        return toResponse(vendor);
    }

    public VendorResponse getById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Vendor not found"));
        return toResponse(vendor);
    }

    private VendorResponse toResponse(Vendor vendor) {
        return new VendorResponse(
                vendor.getId(),
                vendor.getUserId(),
                vendor.getFarmName(),
                vendor.getAddress(),
                vendor.getState(),
                vendor.getDistrict(),
                vendor.getPincode(),
                vendor.getStatus(),
                vendor.getCreatedAt());
    }
}
