package producte.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producte {
    private String codiProducte;
    private String nom;
    private String descripcio;
    private float preuCompra;
    private float preuVenta;

    @Override
    public String toString() {
        return String.format("Codi: %-10s | Nom: %-20s | Descripció: %-30s | Preu Compra: %-10.2f | Preu Venta: %-10.2f",
                codiProducte, nom, descripcio, preuCompra, preuVenta);
    }
}


