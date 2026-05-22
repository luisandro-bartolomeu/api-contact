package com.luisandro.Contactos.controller;

import com.luisandro.Contactos.dto.ContactRequestDTO;
import com.luisandro.Contactos.dto.ContactResponseDTO;
import com.luisandro.Contactos.model.Contact;
import com.luisandro.Contactos.service.ContactService;
import com.luisandro.Contactos.service.ContactServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Tag(name = "Contact Controller", description = "Endpoints para gerenciamentos de contactos")
public class ContactController {


    private final ContactService contactService;

    @PostMapping
    @Operation(summary = "Criar novo contacto", description = "Cria um novo contato com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email já existe")
    })
    public ResponseEntity<ContactResponseDTO> createContact (@RequestBody ContactRequestDTO contact){
        ContactResponseDTO createdContact = contactService.createContact(contact);
        return ResponseEntity.status(201).body(createdContact);
    }


    @GetMapping
    @Operation(summary = "Listar todos contatos", description = "Retorna uma lista com todos os contatos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<ContactResponseDTO>> getAllContact (){
        List<ContactResponseDTO> contacts = contactService.getAllContacts();
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar contato", description = "Atualiza os dados de um contato existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ContactResponseDTO> updateContact(@PathVariable Long id, @Valid @RequestBody ContactRequestDTO contact){
         ContactResponseDTO c = contactService.updateContact(id, contact);
         return ResponseEntity.ok(c);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar contato por ID", description = "Retorna um contato específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato encontrado"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<ContactResponseDTO> getContactById(
            @Parameter(description = "ID do contato", required = true)
            @PathVariable Long id) {
        ContactResponseDTO contact = contactService.getContactById(id);
        return ResponseEntity.ok(contact);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar contato", description = "Remove um contato do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contato removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar contatos por nome", description = "Retorna contatos que contenham o termo no nome")
    public ResponseEntity<List<ContactResponseDTO>> searchContactsByName(
            @RequestParam String name) {
        List<ContactResponseDTO> contacts = contactService.searchContactsByName(name);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar contato por email", description = "Retorna um contato pelo seu email")
    public ResponseEntity<ContactResponseDTO> getContactByEmail(@PathVariable String email) {
        ContactResponseDTO contact = contactService.getContactByEmail(email);
        return ResponseEntity.ok(contact);
    }
}
