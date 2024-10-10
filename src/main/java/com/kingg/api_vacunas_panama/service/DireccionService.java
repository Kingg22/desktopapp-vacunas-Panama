package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.persistence.entity.Direccion;
import com.kingg.api_vacunas_panama.persistence.entity.Distrito;
import com.kingg.api_vacunas_panama.persistence.entity.Provincia;
import com.kingg.api_vacunas_panama.persistence.repository.DireccionRepository;
import com.kingg.api_vacunas_panama.persistence.repository.DistritoRepository;
import com.kingg.api_vacunas_panama.persistence.repository.ProvinciaRepository;
import com.kingg.api_vacunas_panama.util.IApiResponse;
import com.kingg.api_vacunas_panama.util.mapper.DireccionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for {@link Direccion}, {@link Distrito} and {@link Provincia}.
 */
@Service
@RequiredArgsConstructor
public class DireccionService {
    private final DireccionMapper mapper;
    private final DireccionRepository direccionRepository;
    private final DistritoRepository distritoRepository;
    private final ProvinciaRepository provinciaRepository;

    public void getDistritos(IApiResponse<?, Object> apiResponse) {
        List<Distrito> distritosList = distritoRepository.findAll();
        apiResponse.addData("distritos", mapper.distritoListToDto(distritosList));
    }

    public void getProvincias(IApiResponse<?, Object> apiResponse) {
        List<Provincia> provinciaList = provinciaRepository.findAll();
        apiResponse.addData("provincias", mapper.provinciaListToDto(provinciaList));
    }
}