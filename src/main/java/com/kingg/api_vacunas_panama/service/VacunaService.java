package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.configuration.RabbitMQConfiguration;
import com.kingg.api_vacunas_panama.persistence.entity.*;
import com.kingg.api_vacunas_panama.persistence.repository.DosisRepository;
import com.kingg.api_vacunas_panama.persistence.repository.VacunaRepository;
import com.kingg.api_vacunas_panama.response.ApiContentResponse;
import com.kingg.api_vacunas_panama.response.ApiResponse;
import com.kingg.api_vacunas_panama.response.ApiResponseCode;
import com.kingg.api_vacunas_panama.response.IApiContentResponse;
import com.kingg.api_vacunas_panama.util.mapper.DosisMapper;
import com.kingg.api_vacunas_panama.web.dto.DosisDto;
import com.kingg.api_vacunas_panama.web.dto.InsertDosisDto;
import com.kingg.api_vacunas_panama.web.dto.VacunaFabricanteDto;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service for {@link Vacuna} and {@link Dosis}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VacunaService implements IVacunaService {
    private final RabbitTemplate rabbitTemplate;
    private final DosisMapper dosisMapper;
    private final VacunaRepository vacunaRepository;
    private final DosisRepository dosisRepository;
    private final PacienteService pacienteService;
    private final SedeService sedeService;
    private final DoctorService doctorService;

    @Cacheable(cacheNames = "huge", key = "'vacunas'")
    public List<VacunaFabricanteDto> getVacunasFabricante() {
        return vacunaRepository.findAllIdAndNombreAndFabricante();
    }

    @Transactional
    public IApiContentResponse createDosis(@NotNull InsertDosisDto insertDosisDto) {
        ApiContentResponse apiContentResponse = new ApiResponse();
        log.debug(insertDosisDto.toString());
        Paciente paciente = validatePacienteExist(insertDosisDto.pacienteId());
        Vacuna vacuna = validateVacunaExist(insertDosisDto.vacunaId());
        Sede sede = validateSedeExist(insertDosisDto.sedeId());
        Doctor doctor = validateDoctorExist(insertDosisDto.doctorId());
        log.debug("Paciente ID: {}", paciente.getId());
        log.debug("Vacuna ID: {}", vacuna.getId());
        log.debug("Sede ID: {}", sede.getId());
        this.dosisRepository.findTopByPacienteAndVacunaOrderByCreatedAtDesc(paciente, vacuna).ifPresentOrElse(
                ultimaDosis -> {
                    log.debug("Dosis encontrada ID: {}", ultimaDosis.getId());
                    if (!ultimaDosis.getNumeroDosis().isValidNew(insertDosisDto.numeroDosis())) {
                        apiContentResponse.addError(ApiResponseCode.VALIDATION_FAILED, String.format(
                                "La dosis %s no es válida. Último número de dosis %s", insertDosisDto.numeroDosis(),
                                ultimaDosis.getNumeroDosis()));
                    }
                    log.debug("Nueva dosis cumple las reglas de secuencia en número de dosis");
                }, () -> log.debug("El paciente no tiene dosis previas"));

        Dosis dosis = Dosis.builder()
                .paciente(paciente)
                .fechaAplicacion(insertDosisDto.fechaAplicacion() != null ? insertDosisDto.fechaAplicacion() : LocalDateTime.now(ZoneOffset.UTC))
                .numeroDosis(insertDosisDto.numeroDosis())
                .vacuna(vacuna)
                .sede(sede)
                .lote(insertDosisDto.lote())
                .doctor(doctor)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        dosis = dosisRepository.save(dosis);
        dosis = dosisRepository.findById(dosis.getId()).orElseThrow();
        log.debug("Nueva dosis. ID: {}", dosis.getId());
        DosisDto dosisDto = dosisMapper.toDto(dosis);
        apiContentResponse.addData("dosis", dosisDto);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.QUEUE_DOSIS, dosisDto);
        return apiContentResponse;
    }

    public DosisDto getDosisById(UUID idDosis) {
        return dosisMapper.toDto(dosisRepository.findById(idDosis).orElseThrow());
    }

    public List<DosisDto> getDosisByIdPacienteIdVacuna(UUID idPaciente, UUID idVacuna) {
        return dosisMapper.toDtoList(dosisRepository.findAllByPaciente_IdAndVacuna_IdOrderByCreatedAtDesc(idPaciente, idVacuna));
    }

    Paciente validatePacienteExist(UUID pacienteId) {
        return this.pacienteService.getPacienteById(pacienteId).orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));
    }

    Vacuna validateVacunaExist(UUID vacunaId) {
        return this.vacunaRepository.findById(vacunaId).orElseThrow(() -> new NoSuchElementException("Vacuna no encontrada"));
    }

    Sede validateSedeExist(UUID sedeId) {
        return this.sedeService.getSedeById(sedeId).orElseThrow(() -> new NoSuchElementException("Sede no encontrado"));
    }

    Doctor validateDoctorExist(UUID doctorId) {
        if (doctorId != null) {
            return this.doctorService.getDoctorById(doctorId).orElseThrow(() -> new NoSuchElementException("Doctor no encontrado"));
        }
        return null;
    }

}
