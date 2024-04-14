package com.marcos.silva.rodrigues.pix.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PixDTO {
  private String identifier;
  private String chaveOrigem;
  private String chaveDestino;
  private Double valor;
  private LocalDateTime dataTransferencia;
  private PixStatus status;
}