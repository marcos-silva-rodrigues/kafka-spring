package com.marcos.silva.rodrigues.pix.consumidor;

import com.marcos.silva.rodrigues.pix.avro.PixRecord;
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
  public void processaPix(PixRecord pixRecord, Acknowledgment acknowledgment) {
//    acknowledgment.acknowledge();
    System.out.println("Pix  recebido: " + pixRecord.getIdenticador());

    Pix pix = pixRepository.findByIdentifier(pixRecord.getIdenticador().toString());

    Key origem = keyRepository.findByChave(pixRecord.getChaveOrigem().toString());
    Key destino = keyRepository.findByChave(pixRecord.getChaveDestino().toString());


    if (origem == null || destino == null) {
      pix.setStatus(PixStatus.ERRO);
      throw new KeyNotFoundException();
    } else {
      pix.setStatus(PixStatus.PROCESSADO);
    }
    pixRepository.save(pix);

  }
}
