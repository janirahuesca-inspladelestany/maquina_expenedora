package daos;

import java.sql.SQLException;

public interface BeneficisDAO {
    public void afegirBenefici(float benefici) throws SQLException;
    public float obtenirBeneficis() throws SQLException;
}
