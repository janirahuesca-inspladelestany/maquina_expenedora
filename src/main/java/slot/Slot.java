package slot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Slot {
    private int posicio;
    private int quantitat;
    private String codiProducte;

    @Override
    public String toString() {
        return String.format("Posició: %-10d | Quantitat: %-10d | Codi Producte: %-10s",
                posicio, quantitat, codiProducte);
    }
}
