package info.mackiewicz.bankapp.presentation.auth.registration.dto.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(setterPrefix = "with")
@Schema(description = "Response containing demo account credentials")
public record DemoRegistrationResponse(
        @Schema(example = "janusz.kowalski123456")
        String username,
        @Schema(example = "DemoPass123!")
        String password) {
}
