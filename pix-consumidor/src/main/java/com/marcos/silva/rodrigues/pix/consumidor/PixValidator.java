package com.marcos.silva.rodrigues.pix.consumidor;

import com.marcos.silva.rodrigues.pix.dto.PixDTO;
import com.marcos.silva.rodrigues.pix.dto.PixStatus;
import com.marcos.silva.rodrigues.pix.exception.KeyNotFoundException;
import com.marcos.silva.rodrigues.pix.model.Key;
import com.marcos.silva.rodrigues.pix.model.Pix;
import com.marcos.silva.rodrigues.pix.repository.KeyRepository;
import com.marcos.silva.rodrigues.pix.repository.PixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class PixValidator {

  @Autowired
  private KeyRepository keyRepository;

  @Autowired
  private PixRepository pixRepository;

  @KafkaListener(topics = "pix-topic", groupId = "grupo")
  @RetryableTopic(
          backoff = @Backoff(value = 3000L),
          attempts = "5",
          autoCreateTopics = "true",
          include = KeyNotFoundException.class
  )
  public void processaPix(PixDTO pixDTO, Acknowledgment acknowledgment) {
//    acknowledgment.acknowledge();
    System.out.println("Pix  recebido: " + pixDTO.getIdentifier());

    Pix pix = pixRepository.findByIdentifier(pixDTO.getIdentifier());

    Key origem = keyRepository.findByChave(pixDTO.getChaveOrigem());
    Key destino = keyRepository.findByChave(pixDTO.getChaveDestino());


    if (origem == null || destino == null) {
      pix.setStatus(PixStatus.ERRO);
      throw new KeyNotFoundException();
    } else {
      pix.setStatus(PixStatus.PROCESSADO);
    }
    pixRepository.save(pix);

  }
}
