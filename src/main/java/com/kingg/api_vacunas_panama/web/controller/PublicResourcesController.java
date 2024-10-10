package com.kingg.api_vacunas_panama.web.controller;

import com.kingg.api_vacunas_panama.service.DireccionService;
import com.kingg.api_vacunas_panama.service.SedeService;
import com.kingg.api_vacunas_panama.service.UsuarioManagementService;
import com.kingg.api_vacunas_panama.service.VacunaService;
import com.kingg.api_vacunas_panama.util.ApiResponse;
import com.kingg.api_vacunas_panama.util.ApiResponseUtil;
import com.kingg.api_vacunas_panama.util.IApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/public", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicResourcesController {
    private final DireccionService direccionService;
    private final SedeService sedeService;
    private final VacunaService vacunaService;
    private final UsuarioManagementService usuarioManagementService;

    @GetMapping("/distritos")
    public ResponseEntity<Object> getDistritos(ServletWebRequest request) {
        IApiResponse<?, Object> response = new ApiResponse();
        direccionService.getDistritos(response);
        if (!response.hasErrors()) {
            response.addStatusCode(HttpStatus.OK);
        } else {
            response.addStatusCode(HttpStatus.BAD_REQUEST);
        }
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/provincias")
    public ResponseEntity<Object> getProvincias(ServletWebRequest request) {
        IApiResponse<?, Object> response = new ApiResponse();
        direccionService.getProvincias(response);
        if (!response.hasErrors()) {
            response.addStatusCode(HttpStatus.OK);
        } else {
            response.addStatusCode(HttpStatus.BAD_REQUEST);
        }
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/sedes")
    public ResponseEntity<Object> getSedes(ServletWebRequest request) {
        IApiResponse<?, Object> response = new ApiResponse();
        sedeService.getIdNombreSedes(response);
        if (!response.hasErrors()) {
            response.addStatusCode(HttpStatus.OK);
        } else {
            response.addStatusCode(HttpStatus.BAD_REQUEST);
        }
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/vacunas")
    public ResponseEntity<Object> getVacunas(ServletWebRequest request) {
        IApiResponse<?, Object> response = new ApiResponse();
        vacunaService.getVacunas(response);
        if (!response.hasErrors()) {
            response.addStatusCode(HttpStatus.OK);
        } else {
            response.addStatusCode(HttpStatus.BAD_REQUEST);
        }
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/roles")
    public ResponseEntity<Object> getRoles(ServletWebRequest request) {
        IApiResponse<?, Object> response = new ApiResponse();
        usuarioManagementService.getRoles(response);
        if (!response.hasErrors()) {
            response.addStatusCode(HttpStatus.OK);
        } else {
            response.addStatusCode(HttpStatus.BAD_REQUEST);
        }
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/roles/permisos")
    public ResponseEntity<Object> getPermisos(ServletWebRequest request) {
        IApiResponse<?, Object> response = new ApiResponse();
        usuarioManagementService.getPermisos(response);
        if (!response.hasErrors()) {
            response.addStatusCode(HttpStatus.OK);
        } else {
            response.addStatusCode(HttpStatus.BAD_REQUEST);
        }
        return ApiResponseUtil.sendResponse(response, request);
    }

}