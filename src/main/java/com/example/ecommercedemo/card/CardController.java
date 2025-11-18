package com.example.ecommercedemo.card;

import com.example.ecommercedemo.api.CardApi;
import com.example.ecommercedemo.model.CardReq;
import com.example.ecommercedemo.model.Card;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
@Validated
@RequestMapping("/api/v1")
public class CardController implements CardApi {

  private final CardService cardService;

  private final CardRepresentationModelAssembler cardAssembler;

  public CardController(CardService cardService, CardRepresentationModelAssembler cardAssembler) {
    this.cardService = cardService;
    this.cardAssembler = cardAssembler;
  }

  @Override
  public ResponseEntity<Card> registerCard(@PathVariable("id") UUID customerId, CardReq cardReq) {
    Card newCard = cardService.registerCard(customerId, cardReq);
    // Add HATEOAS links to the newly created card
    Card cardWithLinks = cardAssembler.toModel(newCard);
    return status(HttpStatus.CREATED).body(cardWithLinks);
  }

  @Override
  public ResponseEntity<List<Card>> getCustomerCards (@PathVariable("id") UUID id) {
    return ResponseEntity.ok(
        cardService.getCardsByCustomerId(id) // returns Optional<List<Card>>
            .map(cardAssembler::toModelList)
            .orElse(List.of()) // If Optional is empty (service returned null), provide an empty List<Card>
    );
  }

  @Override
  public ResponseEntity<Card> getCardById(UUID uuid) {
    return cardService.getCardById(uuid)
        .map(cardAssembler::toModel)
        .map(ResponseEntity::ok)
        .orElse(notFound().build());
  }

  @Override
  public ResponseEntity<Void> deleteCardById(UUID id) {

    boolean wasDeleted = cardService.deleteCardById(id);

    if (wasDeleted) {
      // Correct way to return 204 No Content for a successful deletion
      return ResponseEntity.noContent().build();
    } else {
      // Keep 404 Not Found if the resource didn't exist
      return ResponseEntity.notFound().build();
    }
  }
}
