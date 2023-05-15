package benefici;

import shared.InfrastructureError;

public interface BeneficisDAO {
    public void createBenefici(float benefici) throws InfrastructureError;
    public float readBeneficis() throws InfrastructureError;
}
