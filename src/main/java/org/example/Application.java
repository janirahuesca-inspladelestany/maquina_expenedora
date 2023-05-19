package org.example;

import benefici.domain.BeneficisDAO;
import producte.application.AfegirProducteAccio;
import producte.domain.Producte;
import producte.domain.ProducteExistentError;
import shared.AppLogger;
import shared.ApplicationError;
import shared.DAOFactory;
import shared.InfrastructureError;
import shared.utils.Stdout;
import producte.domain.ProducteDAO;
import shared.utils.Stdin;
import slot.application.AfegirSlotAccio;
import slot.application.ComprarProducteAccio;
import slot.application.ModificarEstocAccio;
import slot.application.ModificarPosicioAccio;
import slot.domain.Slot;
import slot.domain.SlotDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Application {
    private static DAOFactory daoFactory = DAOFactory.getInstance();
    private static ProducteDAO producteDAO;
    private static SlotDAO slotDAO;
    private static BeneficisDAO beneficisDAO;
    static AppLogger logger = AppLogger.getInstance();


    public static void main(String[] args) {
        // Inicialització dels DAO:
        producteDAO = daoFactory.getProducteDAO();
        slotDAO = daoFactory.getSlotDAO();
        beneficisDAO = daoFactory.getBeneficisDAO();

        Scanner lector = new Scanner(System.in);
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
                var metadata = error.getMetadata();
                if (metadata != null) {
                    logger.debug(metadata);
                }
            } catch (Exception exception) {
                logger.error(exception.getMessage());
                return;
            }
        } while (opcio != -1);

        try {
            DAOFactory.getConn().close();
        } catch (Exception exception) {
            logger.error("Error closing database connection");
            logger.error(exception.getMessage());
        }
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
        // Mostrem a l'usuari els productes disponibles:
        mostrarMaquina();

        // Demanem a l'usuari per un slot:
        int posicio = Stdin.inputInt("Introdueix la posicio del producte que vols comprar: ");

        ComprarProducteAccio.run(slotDAO, producteDAO, beneficisDAO, posicio);

        logger.info("Venda realitzada correctament.");
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

        try {
            AfegirProducteAccio.run(producteDAO, p);
        } catch (ProducteExistentError error) {
            System.out.println(error.message);
            System.out.println("Què vols fer?");
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
            return;
        }

        logger.info("Producte afegit correctament a la BDD.");
        // Agafem tots els productes de la BD i els mostrem (per comprovar que s'ha afegit):
        ArrayList<Producte> productes = producteDAO.readProductes();
        Stdout.showArray(productes.toArray());
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
                3. Afegir slots\n""");

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

        ModificarPosicioAccio.run(slotDAO, posicioSlot1, posicioSlot2);

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

        // Demanem a l'usuari el nou estoc que vol assignar al slot:
        int stock = Stdin.inputInt("Introdueix el nou stock: ");

        ModificarEstocAccio.run(slotDAO, posicio, stock);

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

        Slot slotPerAfegir = new Slot(posicio, quantitat, codi_producte);

        AfegirSlotAccio.run(slotDAO, slotPerAfegir);
        logger.info("Slot afegit correctament.");
    }

    /**
     * Mètode que mostra el benefici de la sessió actual de la màquina (suma de la diferència de preu resta de preu
     * venda - preu compra). Cada vegada que es fa efectiva una compra, es crida al mètode createBenefici(),
     * que inserta un nou registre a la taula benefici.
     * @throws ApplicationError
     */
    private static void mostrarBenefici() throws ApplicationError {

        float benefici = beneficisDAO.readBeneficis();
        System.out.printf("El benefici és de: %.2f\n", benefici);
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
