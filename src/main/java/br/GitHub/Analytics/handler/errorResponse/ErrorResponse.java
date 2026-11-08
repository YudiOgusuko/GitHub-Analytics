package br.GitHub.Analytics.handler.errorResponse;

import lombok.Builder;

@Builder
public record ErrorResponse(String message,
                            Integer status) {
}
