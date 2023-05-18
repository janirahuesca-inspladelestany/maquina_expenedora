package slot.application;

import benefici.domain.BeneficisDAO;
import producte.domain.Producte;
import producte.domain.ProducteDAO;
import shared.ApplicationError;
import slot.domain.EstocInsuficientError;
import slot.domain.Slot;
import slot.domain.SlotDAO;
import slot.domain.SlotNoTrobatError;

public class ComprarProducteAccio {
    public static void run(SlotDAO slotDAO, ProducteDAO producteDAO, BeneficisDAO beneficisDAO, int posicio) throws ApplicationError {

        // Llegim el slot de la base de dades:
        Slot slotLlegit = slotDAO.readSlot(posicio);

        // Ens assegurem que el slot existeix:
        if (slotLlegit == null) {
            throw new SlotNoTrobatError(String.format("No hi ha un slot a la posició %d.", posicio));
        }

        // Comprovem que hi ha productes a aquest slot:
        if (slotLlegit.getQuantitat() < 1) {
            throw new EstocInsuficientError("No hi ha stock disponible.");
        }

        // Reduïm l'estoc del producte:
        slotLlegit.setQuantitat(slotLlegit.getQuantitat() - 1);

        // Actualitzem el slot (perquè quedi constància del nou estoc):
        slotDAO.updateSlot(slotLlegit);



        // Afegim els beneficis obtinguts pel producte venut (preu venda - preu compra):
        Producte producteComprat;

        producteComprat = producteDAO.readProducte(slotLlegit.getCodiProducte());

        float benefici = producteComprat.getPreuVenta() - producteComprat.getPreuCompra();
        beneficisDAO.createBenefici(benefici);
    }
}
