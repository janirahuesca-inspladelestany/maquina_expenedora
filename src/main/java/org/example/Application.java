package org.example;

import benefici.BeneficisDAO;
import producte.Producte;
import shared.AppLogger;
import shared.ApplicationError;
import shared.DAOFactory;
import shared.InfrastructureError;
import slot.Slot;
import producte.ProducteDAO;
import slot.SlotDAO;
import shared.utils.Stdin;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Application {
    private static DAOFactory daoFactory;
    private static ProducteDAO producteDAO;
    private static SlotDAO slotDAO;
    private static BeneficisDAO beneficisDAO;
    static AppLogger logger = AppLogger.getInstance();


    public static void main(String[] args) {

        /* TODO (COSES A MIRAR):
        - Fitxer d'escriptura on guardar el logging(encara que s'actualitzi cada vegada que executes el programa)
        - Connection poll de JBDC
        - Que els DAO guardin en caché la info amb hashmap (codi_producte - objecte producte), i només accedir a la BDD
        per INSERT, UPDATE O DELETE).
        */

        // Inicialització dels DAO:
        daoFactory = DAOFactory.getInstance();
        producteDAO = DAOFactory.getInstance().getProducteDAO();
        slotDAO = DAOFactory.getInstance().getSlotDAO();
        beneficisDAO = DAOFactory.getInstance().getBeneficisDAO();

        Scanner lector = new Scanner(System.in);            //TODO: passar Scanner a una classe InputHelper
        int opcio = 0;

        do {
            try {
                mostrarMenu();
                opcio = lector.nextInt();

                switch (opcio) {
                    case 1:
                        mostrarMaquina();
                        break;
                    case 2:
                        comprarProducte();
                        break;
                    case 10:
                        mostrarInventari();
                        break;
                    case 11:
                        afegirProductes();
                        break;
                    case 12:
                        modificarMaquina();
                        break;
                    case 13:
                        mostrarBenefici();
                        break;
                    case -1:
                        System.out.println("Bye...");
                        break;
                    default:
                        System.out.println("Opció no vàlida");
                }
            } catch (ApplicationError error) {
                logger.error(error.message);
            } catch (Exception exception) {
                logger.error(exception.getMessage());
                return;
            }
        } while (opcio != -1);
    }

    /**
     * Mètode que mostra la informació de la màquina (posició del slot, i nom i quantitat dels productes que conté).
     * @throws ApplicationError
     */
    private static void mostrarMaquina() throws ApplicationError {

        ArrayList<Slot> slots;

        // Llegim la informació dels slots de la màquina:
        slots = slotDAO.readSlots();

        // Per cada slot, mostrem la informació (accedint al nom del producte gràcies a la creació i lectura del producte):
        System.out.printf("%-10s%-30s%-10s\n", "POSICIÓ", "NOM PRODUCTE", "QUANTITAT");
        for (Slot slot : slots) {
            Producte producte = producteDAO.readProducte(slot.getCodiProducte());
            System.out.printf("%-10d%-30s%-10d\n", slot.getPosicio(), producte.getNom(), slot.getQuantitat());
        }
    }

    /**
     * Mètode que permet realitzar una compra indicant la posició on es troba el producte que es vol comprar.
     * Quan s'ha venut un producte queda reflectit a la BD que n'hi ha un menys.
     * @throws ApplicationError
     */
    private static void comprarProducte() throws ApplicationError {

        // TODO: Ampliació (0.5 punts): es permet entrar el NOM del producte per seleccionar-lo (abans cal mostrar els productes disponibles a la màquina)

        // Mostrem a l'usuari els productes disponibles:
        mostrarMaquina();

        // Demanem a l'usuari per un slot:
        int posicio = Stdin.inputInt("Introdueix la posicio del producte que vols comprar: ");
        Slot slot;

        // Llegim el slot de la base de dades:
        slot = slotDAO.readSlot(posicio);

        // Ens assegurem que el slot existeix:
        if (slot == null) {
            System.out.println("No hi ha un slot a la posició indicada.");
            return;
        }

        // Comprovem que hi ha productes a aquest slot:
        if (slot.getQuantitat() < 1) {
            System.out.println("No hi ha stock disponible.");
            return;
        }

        // Reduïm l'estoc del producte:
        slot.setQuantitat(slot.getQuantitat() - 1);

        // Actualitzem el slot (perquè quedi constància del nou estoc):
        slotDAO.updateSlot(slot);
        logger.info("Venda realitzada correctament.");

        // Afegim els beneficis obtinguts pel producte venut (preu venda - preu compra):
        Producte producteComprat;

        producteComprat = producteDAO.readProducte(slot.getCodiProducte());

        float benefici = producteComprat.getPreuVenta() - producteComprat.getPreuCompra();
        beneficisDAO.createBenefici(benefici);
    }

    /**
     * Mètode per mostrar la informació dels productes que tenim, i així conèixer l'inventari (productes i quantitat
     * que tenim).
     * @throws ApplicationError
     */
    private static void mostrarInventari() throws ApplicationError {

        // Agafem tots els productes de la BD i els mostrem:
        System.out.println("INVENTARI DE LA MÀQUINA:");
        System.out.println("========================");
        ArrayList<Producte> productes = producteDAO.readProductes();
        for (Producte prod : productes) {
            System.out.println(prod);
        }
    }

    /**
     * Mètode que ens permet crear un nou producte amb les dades que ens digui l'operari, així com afegir el producte a
     * la BD. Es tenen en compte les diferents situacions que poden passar:
     * El producte ja existeix
     * - Mostrar el producte que té el mateix codiProducte
     * - Preguntar si es vol actualitzar o descartar l'operació
     * El producte no existeix
     * - Afegir el producte a la BD
     * @throws ApplicationError
     * @throws SQLException
     */
    private static void afegirProductes() throws ApplicationError {

        // Demanem els diferents camps que componen un producte:
        System.out.println("""
                DADES PRODUCTE:
                ===============""");
        String codi = Stdin.input("- Codi producte: ");
        String nom = Stdin.input("- Nom: ");
        String descripcio = Stdin.input("- Descripció: ");
        float preuCompra = (float) Stdin.inputDouble("- Preu compra: ");
        float preuVenda = (float) Stdin.inputDouble("- Preu venda: ");

        // Creem un nou producte amb les dades sol·licitades:
        Producte p = new Producte(codi, nom, descripcio, preuCompra, preuVenda);

        // Creem un producte auxiliar i li assignem el valor del nou producte que hem creat
        Producte producteLlegit;
        producteLlegit = producteDAO.readProducte(codi);

        // Comprovem si el sproducte que es vol crear ja existeix. ç
        // Si ja existeix, informem a l'usuari i donem a triar dues opcions (actualitzar el producte o descartar els canvis):
        if (producteLlegit != null) {
            System.out.println("""
                    Producte ja existent. Què vols fer?
                    """);
            int opcio = Stdin.inputInt("""
                    1 - Actualitzar
                    2 - Descartar canvis
                    """);

            // Si tria descartar els canvis, sortim del mètode:
            if (opcio == 2) {
                logger.info("S'han descartat els canvis.");
                return;
            }

            // Si tria actualitzar el producte, realitzem la operació:
            producteDAO.updateProducte(p);
            logger.info("S'ha actualitzat el producte.");
        }

        // Guardem el producte p a la BD:
        producteDAO.createProducte(p);
        logger.info("Producte afegit correctament a la BDD.");

        // Agafem tots els productes de la BD i els mostrem (per comprovar que s'ha afegit):
        ArrayList<Producte> productes = producteDAO.readProductes();

        for (Producte prod : productes) {
            System.out.println(prod);
        }
    }

    /**
     * Mètode que permet modificar aspectes de la màquina expenedora. Per cadascun d'aquests aspectes s'han creat
     * submètodes, un per cada funcionalitat. Es mostra a l'usuari les opcions que pot fer, i amb un switch s'assigna
     * la opció triada per l'usuari al mètode corresponent.
     * @throws ApplicationError
     */
    private static void modificarMaquina() throws ApplicationError {

        int opcio = Stdin.inputInt("""
                MODIFICAR MAQUINA:
                ==================
                1. Modificar posicions
                2. Modificar estoc
                3. Afegir slots""");

        switch (opcio) {
            case 1 -> modificarPosicions();
            case 2 -> modificarEstoc();
            case 3 -> afegirSlots();
            default -> System.out.println("Opcio no valida");
        }
    }

    /**
     * Mètode que permet modificar les posicions on hi ha els productes de la màquina (quin article va a cada lloc).
     * @throws ApplicationError
     */
    private static void modificarPosicions() throws ApplicationError {
        // Mostrem informació de la màquina expenedora:
        mostrarMaquina();

        // Demanem a l'usuari dues posicions (les que es volen intercanviar),
        int posicioSlot1 = Stdin.inputInt("Introdueix el primer slot: ");
        int posicioSlot2 = Stdin.inputInt("Introdueix el segon slot: ");

        // Creem una posició auxiliar:
        int posicioAux;

        Slot slot1;
        Slot slot2;

        // Llegim les dades que hi ha a cada slot i ens assegurem que el slots existeixen:
        slot1 = slotDAO.readSlot(posicioSlot1);
        if (slot1 == null) {
            System.out.printf("No hi ha un slot a la posició %d.", posicioSlot1);
            return;
        }

        slot2 = slotDAO.readSlot(posicioSlot2);
        if (slot2 == null) {
            System.out.printf("No hi ha un slot a la posició %d.", posicioSlot2);
            return;
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

        logger.info("Posicions intercanviades correctament.");
    }

    /**
     *  Mètode que permet modificar l'estoc d'un producte que hi ha a la màquina
     * @throws ApplicationError
     */
    private static void modificarEstoc() throws ApplicationError {

        // Mostrem informació de la màquina expenedora:
        mostrarMaquina();

        // Demanem a l'usuari el slot del qual vol modificar l'estoc:
        int posicio = Stdin.inputInt("Introdueix el slot: ");

        // Llegim el slot
        Slot slot = slotDAO.readSlot(posicio);

        // Ens assegurem que el slot existeix:
        if (slot == null) {
            System.out.println("No hi ha un slot a la posició indicada.");
            return;
        }

        // Demanem a l'usuari el nou estoc que vol assignar al slot:
        int stock = Stdin.inputInt("Introdueix el nou stock: ");

        // Si ens vol introduïr un valor menor a 1, mostrem un missatge i sortim del mètode
        if (stock < 1) {
            System.out.println("El stock no és valid.");
            return;
        }

        // Li assignem la nova quantitat (estoc demanat):
        slot.setQuantitat(stock);

        // Actualitzem la informació del slot:
        slotDAO.updateSlot(slot);
        logger.info("Estoc modificat correctament.");
    }

    /**
     * Mètode que permet afegir més ranures (slots) a la màquina
     * @throws InfrastructureError
     */
    private static void afegirSlots() throws ApplicationError {

        // Mostrem tant la informació de la màquina com la dels productes, per ajudar a l'operari a decidir quin producte afegir:
        mostrarBenefici();
        mostrarInventari();

        // Demanem les dades dels diferents camps que composen un slot, i les assignem a les variables corresponents:
        System.out.println("""
                DADES SLOT A AFEGIR:
                ====================
                """);
        int posicio = Stdin.inputInt("- Posició: ");
        int quantitat = Stdin.inputInt("- Quantitat (unitats x producte): ");
        String codi_producte = Stdin.input("- Codi producte: ");

        // Creem un nou slot amb les dades sol·licitades:
        Slot s = new Slot(posicio, quantitat, codi_producte);

        // Creem un slot auxiliar i li assignem el valor del nou slot que hem creat
        Slot slotLlegit;
        slotLlegit = slotDAO.readSlot(posicio);

        // Comprovem si el slot que es vol crear ja existeix. Si ja existeix, informem a l'usuari i sortim del mètode.
        if (slotLlegit != null) {
            System.out.println("Aquest slot ja existeix.");
            return;
        }

        // Si no existeix, creem el slot:
        slotDAO.createSlot(s);
        logger.info("Slot afegit correctament.");
    }

    private static void mostrarBenefici() throws ApplicationError {

        /** Ha de mostrar el benefici de la sessió actual de la màquina, cada producte té un cost de compra
         * i un preu de venda. La suma d'aquesta diferència de tots productes que s'han venut ens donaran el benefici.
         *
         * Simplement s'ha de donar el benefici actual des de l'últim cop que s'ha engegat la màquina. (es pot fer
         * amb un comptador de benefici que s'incrementa per cada venda que es fa)
         */

        /** AMPLIACIÓ **
         * En entrar en aquest menú ha de permetre escollir entre dues opcions: veure el benefici de la sessió actual o
         * tot el registre de la màquina.
         *
         * S'ha de crear una nova taula a la BD on es vagi realitzant un registre de les vendes o els beneficis al
         * llarg de la vida de la màquina.
         */

        float benefici = beneficisDAO.readBeneficis();
        System.out.printf("El benefici es de: %.2f\n", benefici);
    }

    private static void mostrarMenu() {
        System.out.println("\nMenú de la màquina expenedora");
        System.out.println("=============================");
        System.out.println("Selecciona la operació a realitzar introduïnt el número corresponent: \n");

        //Opcions per client / usuari
        System.out.println("[1] Mostrar Posició / Nom producte / Stock de la màquina");
        System.out.println("[2] Comprar un producte");

        //Opcions per administrador / manteniment
        System.out.println();
        System.out.println("[10] Mostrar llistat productes disponibles (BD)");
        System.out.println("[11] Afegir productes disponibles");
        System.out.println("[12] Assignar productes / stock a la màquina");
        System.out.println("[13] Mostrar benefici");

        System.out.println();
        System.out.println("[-1] Sortir de l'aplicació");
    }
}
