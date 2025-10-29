package com.example.ecommercedemo.service;

import com.example.ecommercedemo.entity.AddressEntity;
import com.example.ecommercedemo.hateoas.AddressRepresentationModelAssembler;
import com.example.ecommercedemo.model.Address;
import com.example.ecommercedemo.repository.AddressRepository;
import com.example.ecommercedemo.model.AddAddressReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

  private final AddressRepository repository;

  private final AddressRepresentationModelAssembler assembler;

  public AddressServiceImpl(AddressRepository repository, AddressRepresentationModelAssembler assembler) {
    this.repository = repository;
    this.assembler = assembler;
  }

  @Override
  @Transactional
  public Optional<Address> createAddress(AddAddressReq addAddressReq) {
    return Optional.of(assembler.toModel(repository.save(toEntity(addAddressReq))));
  }

  @Override
  @Transactional
  public void deleteAddressesById(String id) {
    repository.deleteById(UUID.fromString(id));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Address> getAddressesById(String id) {
    return repository.findById(UUID.fromString(id)).map(assembler::toModel);
   }

  @Override
  @Transactional(readOnly = true)
  public List<Address> getAllAddresses() {
    return assembler.toListModel(repository.findAll());
  }

  private AddressEntity toEntity(AddAddressReq model) {
    AddressEntity entity = new AddressEntity();
    return entity.setNumber(model.getNumber()).setResidency(model.getResidency())
        .setStreet(model.getStreet()).setCity(model.getCity()).setState(model.getState())
        .setCountry(model.getCountry()).setZipcode(model.getZipcode());
  }
}