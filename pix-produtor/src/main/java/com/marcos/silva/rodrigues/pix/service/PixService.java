package com.marcos.silva.rodrigues.pix.service;

import com.marcos.silva.rodrigues.pix.dto.PixDTO;
import com.marcos.silva.rodrigues.pix.model.Pix;
import com.marcos.silva.rodrigues.pix.repository.PixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PixService {

  @Autowired
  private final PixRepository pixRepository;

  @Autowired
  private final KafkaTemplate<String, PixDTO> kafkaTemplate;

  public PixDTO salvarPix(PixDTO pixDTO) {
    pixRepository.save(Pix.toEntity(pixDTO));
    kafkaTemplate.send("pix-topic", pixDTO.getIdentifier(), pixDTO);
    return pixDTO;
  }

}
