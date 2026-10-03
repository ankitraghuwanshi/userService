package org.example.userservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.userservice.models.Token;

import java.util.Date;

@Getter
@Setter
public class TokenDto {
    private String value;
    private Date expiryAt;

    public static TokenDto from(Token token) {
        if(token == null) {
            return null;
        }
        TokenDto tokenDto = new TokenDto();
        tokenDto.setValue(token.getValue());
        tokenDto.setExpiryAt(token.getExpiryAt());
        return tokenDto;
    }
}
