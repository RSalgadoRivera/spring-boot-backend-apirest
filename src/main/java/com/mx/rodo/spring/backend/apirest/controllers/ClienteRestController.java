package com.mx.rodo.spring.backend.apirest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.rodo.spring.backend.apirest.models.entity.Cliente;
import com.mx.rodo.spring.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}

	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {

		Cliente cliente = null;

		Map<String, Object> response = new HashMap<>();

		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (cliente == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" NO existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
		}

	}

	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		Cliente nuevoCliente = null;
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			//Anterior a jdk 8
			/*List<String> errors = new ArrayList<>();
			
			for(FieldError error : result.getFieldErrors()) {
				errors.add("El campo '"+error.getField()+"' "+error.getDefaultMessage());
			}*/
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(error -> "El campo '"+error.getField()+"' "+error.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			nuevoCliente = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al insertar al cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El Cliente ".concat(nuevoCliente.getNombre().concat(" se ha creado con éxito")));
		response.put("cliente", nuevoCliente);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {

		Cliente clienteActual = null;
		Cliente clienteActualizado = null;

		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			//Anterior a jdk 8
			/*List<String> errors = new ArrayList<>();
			
			for(FieldError error : result.getFieldErrors()) {
				errors.add("El campo '"+error.getField()+"' "+error.getDefaultMessage());
			}*/
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(error -> "El campo '"+error.getField()+"' "+error.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {

			clienteActual = clienteService.findById(id);
			if (clienteActual != null) {
				clienteActual.setApellido(cliente.getApellido());
				clienteActual.setNombre(cliente.getNombre());
				clienteActual.setEmail(cliente.getEmail());

				clienteActualizado = clienteService.save(clienteActual);

				response.put("mensaje",
						"El cliente ".concat(clienteActualizado.getNombre().concat(" fue actualizado con éxito")));
				response.put("cliente", clienteActualizado);
			} else {
				response.put("mensaje", "Error el id: ".concat(id.toString()).concat(" NO existe en la base de datos"));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}

		} catch (DataAccessException e) {
			response.put("mensaje", "ocurrió un error en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();

		try {
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar al cliente de la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Cliente eliminado con éxito de la base de datos");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}

}
