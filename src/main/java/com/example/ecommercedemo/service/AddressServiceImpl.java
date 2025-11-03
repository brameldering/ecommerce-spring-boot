package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.mappers.AddressMapper;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.repository.AddressRepository;
import com.example.ecommercedemo.model.AddAddressReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class AddressServiceImpl implements AddressService {

  private final AddressRepository repository;
  private final AddressMapper mapper;

  public AddressServiceImpl(AddressRepository repository, AddressMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public Optional<Address> createAddress(AddAddressReq addAddressReq) {
    return Optional.of(mapper.entityToModel(repository.save(toEntity(addAddressReq))));
  }

  @Override
  @Transactional
  public void deleteAddressById(UUID uuid) {
    repository.deleteById(uuid);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Address> getAddressById(UUID uuid) {
    return repository.findById(uuid).map(mapper::entityToModel);
   }

  @Override
  @Transactional(readOnly = true)
  public List<Address> getAllAddresses() {
    return mapper.entityToModelList(repository.findAll());
  }

  private AddressEntity toEntity(AddAddressReq model) {
    AddressEntity entity = new AddressEntity();
    return entity.setNumber(model.getNumber()).setResidency(model.getResidency())
        .setStreet(model.getStreet()).setCity(model.getCity()).setState(model.getState())
        .setCountry(model.getCountry()).setZipcode(model.getZipcode());
  }
}