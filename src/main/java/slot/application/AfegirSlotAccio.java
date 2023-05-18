package slot.application;

import shared.ApplicationError;
import slot.domain.Slot;
import slot.domain.SlotDAO;
import slot.domain.SlotExistentError;

public class AfegirSlotAccio {
    public static void run(SlotDAO slotDAO, Slot slotPerAfegir) throws ApplicationError {
        // Creem un slot auxiliar i li assignem el valor del nou slot que hem creat
        Slot slotLlegit = slotDAO.readSlot(slotPerAfegir.getPosicio());

        // Comprovem si el slot que es vol crear ja existeix. Si ja existeix, informem a l'usuari i sortim del mètode.
        if (slotLlegit != null) {
            throw new SlotExistentError(String.format("La posicio %d ja esta ocupada", slotPerAfegir.getPosicio()));
        }

        // Si no existeix, creem el slot:
        slotDAO.createSlot(slotPerAfegir);
    }
}
