package mg.vinaAkoho.vina_akoho.exception.depense;

import lombok.Getter;

@Getter
public class DepenseDtoNonSupporteException extends RuntimeException {

    private final String typeDto;

    private DepenseDtoNonSupporteException(String typeDto) {
        super("Type DTO non supporté pour les dépenses : " + typeDto);
        this.typeDto = typeDto;
    }

    public static DepenseDtoNonSupporteException pourType(Class<?> typeDto) {
        return new DepenseDtoNonSupporteException(typeDto.getName());
    }
}
