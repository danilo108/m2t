package m2t.jobloader.service.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.translators.ClientTranslator;
import m2t.jobloader.dao.repositories.ClientRepository;
import m2t.jobloader.service.controllers.model.EntityServiceResponse;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;
import m2t.service.model.jobloader.CustomerDTO;

@Component
public class ClientController {
	
	@Autowired 
	ClientRepository clientRepository;
	@Autowired
	ClientTranslator clientTranslator;
	
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	@ResponseBody
	public EntityServiceResponse<Client> create(
			@RequestBody(required = true) CustomerDTO customer) {
		EntityServiceResponse<Client> response = new EntityServiceResponse<>("create", Client.class);
		Client client = clientRepository.findByClientCode(customer.getCode());
		if(client != null) {
			response.setError(true);
			response.setFound(1);
			response.setErrorDescription("Cannot create the client because exists already");
			response.addWarning(new ResponseErrorDetail("WARNING", "Client with code " + client.getClientCode(), client.toString()));
			return response;
		}
		client = clientTranslator.toDAO(customer);
		try {
			clientRepository.save(client);
			response.setId(client.getId().toString());
			response.setReference(client.getClientCode());
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("Error while saving the client");
			response.addWarning(new ResponseErrorDetail("Error", e.getMessage(), client, e));
		}finally {
			return response;
		}
	}
	
	@RequestMapping(path = "/save", method = RequestMethod.POST)
	@ResponseBody
	public EntityServiceResponse<Client> update(
			@RequestBody(required = true) CustomerDTO customer) {
		EntityServiceResponse<Client> response = new EntityServiceResponse<>("create", Client.class);
		Client client = clientRepository.findByClientCode(customer.getCode());
		if(client != null) {
			response.setError(false);
			response.setFound(1);
		}else {
			response.setFound(0);
			EntityServiceResponse<Client> createResponse = create(customer);
			response.addOperation(createResponse);
			client = clientRepository.findByClientCode(customer.getCode());
			if(client == null) {
				response.setError(true);
				response.setErrorDescription("after creating the client, it cannot be found by code " + customer.getCode());
				return response;
			}
			response.setId(client.getId().toString());
			response.setReference(client.getClientCode());
		}
		Client updatedClient = clientTranslator.toDAO(customer);
		updatedClient.setId(client.getId());
		try {
			clientRepository.save(updatedClient);
			
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("Error while updating the client");
			response.addWarning(new ResponseErrorDetail("Error", e.getMessage(), client, e));
		}finally {
			return response;
		}
	}
	
	
	@RequestMapping(path = "/getAll", method = RequestMethod.POST)
	@ResponseBody
	public List<CustomerDTO> getAll() {
		Iterable<Client> clients = clientRepository.findAll();
		
		 List<Client> list = new ArrayList<>();
		 for(Client c: clients) {
			 list.add(c);
		 }
		 List<CustomerDTO> dtos = list.stream().map(c -> {return clientTranslator.toDTO(c);}).collect(Collectors.toList());
		return dtos;
	}
	
	@RequestMapping(path = "/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public EntityServiceResponse<Client> delete(
			@RequestBody(required = true) String reference) {
		EntityServiceResponse<Client> response = new EntityServiceResponse<>("delete", Client.class);
		Client client = clientRepository.findByClientCode(reference);
		if(client == null) {
			response.setError(false);
			response.setErrorDescription("The client with code " + reference + " does not exist already");
			response.setFound(0);
			return response;
		}else {
			response.setFound(1);
			try {
				clientRepository.delete(client);
				
			} catch (Exception e) {
				e.printStackTrace();
				response.setError(true);
				response.setErrorDescription("Error while deleting the client");
				response.addWarning(new ResponseErrorDetail("Error", e.getMessage(), client, e));
			}finally {
				return response;
			}
			
		}
		
		
	}
	
}
