package slot;

import shared.ApplicationError;

public class ModificarEstocAccio {
    public static void run(SlotDAO slotDAO, int posicio, int stock) throws ApplicationError {
        // Llegim el slot
        Slot slot = slotDAO.readSlot(posicio);

        // Ens assegurem que el slot existeix:
        if (slot == null) {
            throw new SlotNoTrobatError(String.format("No s'ha trobat un slot a la posició %d.", posicio));
        }

        // Si ens vol introduïr un valor menor a 1, mostrem un missatge i sortim del mètode
        if (stock < 1) {
            throw new EstocInvalidError("L'estoc no és vàlid.");
        }

        // Li assignem la nova quantitat (estoc demanat):
        slot.setQuantitat(stock);

        // Actualitzem la informació del slot:
        slotDAO.updateSlot(slot);
    }
}
