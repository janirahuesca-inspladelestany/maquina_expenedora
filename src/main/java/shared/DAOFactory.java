package shared;

import benefici.BeneficisDAO;
import benefici.BeneficisDAO_MySQL;
import producte.ProducteDAO;
import producte.ProducteDAO_MySQL;
import slot.SlotDAO;
import slot.SlotDAO_MySQL;

public class DAOFactory {
    private static DAOFactory instance;
    private static ProducteDAO producteDAOinstance;
    private static ProducteDAO slotDAOinstance;
    private static ProducteDAO beneficisDAOinstance;
    private DAOFactory() {
        // init ConnectionFactory
    }
    public static DAOFactory getInstance() {
        if (instance == null)
            instance = new DAOFactory();
        return instance;
    }
    public ProducteDAO getProducteDAO() {
        if (producteDAOinstance == null)
            producteDAOinstance = new ProducteDAO_MySQL();
        return producteDAOinstance;
    }
    public SlotDAO getSlotDAO() {
        if (slotDAOinstance == null)
            slotDAOinstance = new SlotDAO_MySQL();
        return (SlotDAO) slotDAOinstance;
    }
    public BeneficisDAO getBeneficisDAO() {
        if (beneficisDAOinstance == null)
            beneficisDAOinstance = new BeneficisDAO_MySQL();
        return (BeneficisDAO) beneficisDAOinstance;
    }
}
