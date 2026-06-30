package sn.rangmoygaaw.transport.domain.valueobject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
 
@Getter 
@Setter
public final class CodeTicket {

    private final String valeur;

    public CodeTicket(String valeur) {
        if (valeur == null || valeur.isBlank())
            throw new IllegalArgumentException("Code ticket ne peut pas être vide");
        if (valeur.length() < 8)
            throw new IllegalArgumentException("Code ticket trop court");
        this.valeur = valeur.toUpperCase();
    }

    

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CodeTicket)) return false;
        return valeur.equals(((CodeTicket) o).valeur);
    }

    @Override
    public int hashCode() { return Objects.hash(valeur); }

    @Override
    public String toString() { return valeur; }
}