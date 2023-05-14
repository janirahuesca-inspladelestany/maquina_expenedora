package daos;

import java.sql.SQLException;

public interface BeneficisDAO {
    public void afegirBenefici(float benefici) throws InfrastructureError;
    public float obtenirBeneficis() throws InfrastructureError;
}
