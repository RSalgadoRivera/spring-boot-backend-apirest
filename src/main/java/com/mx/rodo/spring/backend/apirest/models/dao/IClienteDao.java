package com.mx.rodo.spring.backend.apirest.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.mx.rodo.spring.backend.apirest.models.entity.Cliente;

public interface IClienteDao extends CrudRepository<Cliente, Long>{

}
