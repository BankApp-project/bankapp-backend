package info.mackiewicz.bankapp.presentation.auth.registration.controller;

import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.demo.DemoRegistrationResponse;
import info.mackiewicz.bankapp.system.error.handling.dto.BaseApiError;
import info.mackiewicz.bankapp.system.error.handling.dto.ValidationApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@SecurityRequirements
@Tag(name = "Registration", description = "API for user registration")
@RequestMapping("/api/public/registration")
public interface RegistrationController {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user in the system. The user will be registered with the provided details."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "User registration details",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RegistrationRequest.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseApiError.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationApiError.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseApiError.class,
                                    example = """
                                            {
                                              "status": "CONFLICT",
                                              "title": "USER_ALREADY_EXISTS",
                                              "message": "User with these credentials already exists.",
                                              "path": "/api/registration/regular",
                                              "timestamp": "11-04-2025 16:18:29"
                                            }
                                            """)))
    })
    @PostMapping("/regular")
    ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest request);

    @Operation(
            summary = "Create a demo account",
            description = "Creates a demo account with 2-month transaction history. No input required - returns auto-generated credentials."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Demo account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DemoRegistrationResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Demo account creation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseApiError.class)))
    })
    @PostMapping("/demo")
    ResponseEntity<DemoRegistrationResponse> registerDemoUser();
}
