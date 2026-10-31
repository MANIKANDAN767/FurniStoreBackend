package com.furnistore.controller;

import com.furnistore.dto.AddressRequest;
import com.furnistore.dto.AddressResponse;
import com.furnistore.service.AddressService;
import com.furnistore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        return userService.getUserIdByEmail(userDetails.getUsername());
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(getCurrentUserId(userDetails), request));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.getAllAddresses(getCurrentUserId(userDetails)));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long addressId,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(getCurrentUserId(userDetails), addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long addressId) {
        addressService.deleteAddress(getCurrentUserId(userDetails), addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.setDefaultAddress(getCurrentUserId(userDetails), addressId));
    }
}