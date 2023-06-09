package benefici.domain;

import shared.ApplicationError;
import shared.InfrastructureError;

public interface BeneficisDAO {
    public void createBenefici(float benefici) throws ApplicationError;
    public float readBeneficis() throws ApplicationError;
}
