package com.marcos.silva.rodrigues.pix.consumidor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcos.silva.rodrigues.pix.avro.PixRecord;
import com.marcos.silva.rodrigues.pix.dto.PixDTO;
import com.marcos.silva.rodrigues.pix.dto.PixStatus;
import com.marcos.silva.rodrigues.pix.exception.KeyNotFoundException;
import com.marcos.silva.rodrigues.pix.model.Key;
import com.marcos.silva.rodrigues.pix.model.Pix;
import com.marcos.silva.rodrigues.pix.repository.KeyRepository;
import com.marcos.silva.rodrigues.pix.repository.PixRepository;
import org.apache.avro.generic.GenericData;
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

//  @KafkaListener(topics = "pix-topic", groupId = "grupo")
  @KafkaListener(topics = "pix-topic.public.pix", groupId = "grupo")
  @RetryableTopic(
          backoff = @Backoff(value = 3000L),
          attempts = "5",
          autoCreateTopics = "true",
          include = KeyNotFoundException.class
  )
//  public void processaPix(PixRecord pixRecord, Acknowledgment acknowledgment) {
  public void processaPix(GenericData.Record data) throws JsonProcessingException {
//    acknowledgment.acknowledge();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    PixDTO pixDTO = objectMapper.readValue(data.get("after").toString(), PixDTO.class);
    System.out.println("Pix  recebido: " + pixDTO.getIdentifier());

    if(pixDTO.getStatus().equals(PixStatus.EM_PROCESSAMENTO)) {
      Pix pix = pixRepository.findByIdentifier(pixDTO.getIdentifier().toString());

      Key origem = keyRepository.findByChave(pixDTO.getChaveOrigem().toString());
      Key destino = keyRepository.findByChave(pixDTO.getChaveDestino().toString());


      if (origem == null || destino == null) {
        pix.setStatus(PixStatus.ERRO);
        throw new KeyNotFoundException();
      } else {
        pix.setStatus(PixStatus.PROCESSADO);
      }
      pixRepository.save(pix);
    }



  }
}
