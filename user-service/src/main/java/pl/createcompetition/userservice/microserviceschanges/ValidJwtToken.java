package pl.createcompetition.userservice.microserviceschanges;

import lombok.Builder;

@Builder
public record ValidJwtToken(String accessToken, int expiresIn, int refreshExpiresIn,
                            String refreshToken, String tokenType, String idToken,
                            int notBeforePolicy, String session_state, String scope){
}
