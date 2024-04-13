package com.marcos.silva.rodrigues.pix.controller;

import com.marcos.silva.rodrigues.pix.dto.PixDTO;
import com.marcos.silva.rodrigues.pix.dto.PixStatus;
import com.marcos.silva.rodrigues.pix.service.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixController {

  @Autowired
  private final PixService pixService;

  @PostMapping
  public PixDTO salvarPix(@RequestBody PixDTO dto) {
    dto.setIdentifier(UUID.randomUUID().toString());
    dto.setDataTransferencia(LocalDateTime.now());
    dto.setStatus(PixStatus.EM_PROCESSAMENTO);

    return pixService.salvarPix(dto);
  }
}
