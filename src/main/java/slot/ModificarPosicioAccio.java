package slot;

import shared.ApplicationError;

public class ModificarPosicioAccio {
    public static void run(SlotDAO slotDAO, int posicio1, int posicio2) throws ApplicationError {
        // Creem una posició auxiliar:
        int posicioAux;

        Slot slot1;
        Slot slot2;

        // Llegim les dades que hi ha a cada slot i ens assegurem que el slots existeixen:
        slot1 = slotDAO.readSlot(posicio1);
        if (slot1 == null) {
            throw new SlotNoTrobatError(String.format("No hi ha un slot a la posició %d.", posicio1));
        }

        slot2 = slotDAO.readSlot(posicio2);
        if (slot2 == null) {
            throw new SlotNoTrobatError(String.format("No hi ha un slot a la posició %d.", posicio2));
        }

        // Assignem a posició auxiliar la posició que té el slot1:
        posicioAux = slot1.getPosicio();
        // Al slot 1 li assignem la posició 0:
        slot1.setPosicio(0);
        // Actualitzem el slot 1 amb les noves dades (posició 0):
        slotDAO.updateSlot(slot1);

        // Al slot 1, li assignem la posició que té el slot 2:
        slot1.setPosicio(slot2.getPosicio());
        // Al slot 2, li assignem la posició auxiliar (la inicial del slot 1):
        slot2.setPosicio(posicioAux);

        // Actualitzem els dos slots amb les noves dades:
        slotDAO.updateSlot(slot1);
        slotDAO.updateSlot(slot2);
    }
}
