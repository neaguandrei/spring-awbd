package com.fmi.user.service;

import com.fmi.common.exception.BadRequestException;
import com.fmi.common.exception.NotFoundException;
import com.fmi.user.dto.UserDto;
import com.fmi.dao.entity.AddressEntity;
import com.fmi.dao.entity.UserEntity;
import com.fmi.dao.repository.AddressRepository;
import com.fmi.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final String CUSTOMER_NOT_FOUND = "Customer not found";

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;

    private final MapperService mapperService;

    public void delete(String userId) throws NotFoundException {
        Optional<UserEntity> userEntity = userRepository.findByInternalId(userId);
        if (!userEntity.isPresent()) {
            throw new NotFoundException(CUSTOMER_NOT_FOUND);
        }
        userRepository.deleteByInternalId(userId);
    }

    public void save(UserDto object) throws BadRequestException {
        boolean isEmailExisting = userRepository.findByEmail(object.getEmail()).isPresent();
        boolean isPhoneExisting = userRepository.findByPhone(object.getPhone()).isPresent();
        if (isEmailExisting) {
            throw new BadRequestException("E-mail already exists!");
        }
        if (isPhoneExisting) {
            throw new BadRequestException("Phone already exists!");
        }

        final UserEntity userEntity = mapperService.convertCustomerDtoToCustomer(object);
        userEntity.setPassword(passwordEncoder.encode(object.getPassword()));

        userRepository.save(userEntity);
    }

    public void update(String internalId, UserDto object) {
        if (object == null) {
            throw new IllegalArgumentException("Object can't be null!");
        }

        Optional<UserEntity> optionalCustomer = userRepository.findByInternalId(internalId);
        if (optionalCustomer.isPresent()) {
            UserEntity userEntity = mapperService.convertCustomerDtoToCustomer(object);
            if (userEntity.getPassword() != null) {
                userEntity.setPassword(passwordEncoder.encode(object.getPassword()));
            }
            userEntity.setInternalId(internalId);
            updateAddress(internalId, userEntity);
            userRepository.save(mapperService.convertCustomerDtoToCustomer(object));
        }
    }

    public UserDto getUserByInternalId(String userId) throws NotFoundException {
        return mapperService.convertCustomerToCustomerDto(userRepository.findByInternalId(userId)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND)));
    }

    private void updateAddress(String userId, UserEntity userEntity) {
        Optional<AddressEntity> optionalAddress = addressRepository.findByUserInternalId(userId);
        optionalAddress.ifPresent(address -> {
            addressRepository.save(address);
            userEntity.setAddress(address);
        });
    }

    public UserDto getByEmail(String email) throws NotFoundException {
        return mapperService.convertCustomerToCustomerDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND)));
    }
}
