package org.example;

import daos.*;
import model.Producte;
import model.Slot;
import utils.Stdin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Application {
    private static Connection conn;
    private static ProducteDAO producteDAO;

    private static SlotDAO slotDAO;//TODO: passar a una classe DAOFactory
    private static BeneficisDAO beneficisDAO;
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_ROUTE = "jdbc:mysql://localhost:3306/expenedora";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "1234";

    public static void main(String[] args) {
        try {
            Class.forName(DB_DRIVER); // Carreguem el driver
            conn = DriverManager.getConnection(DB_ROUTE, DB_USER, DB_PWD); // Arranquem la connexió a la BDD
            System.out.println("Connexió establerta satisfactòriament.");
        } catch (Exception e) {
            System.out.println("S'ha produït un error en intentar connectar amb la base de dades. Revisa els paràmetres.");
            System.out.println(e);
            return;
        }

        slotDAO = new SlotDAO_MySQL(conn);
        producteDAO = new ProducteDAO_MySQL(conn);
        beneficisDAO = new BeneficisDAO_MySQL(conn);

        Scanner lector = new Scanner(System.in);            //TODO: passar Scanner a una classe InputHelper
        int opcio = 0;

        do {
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

        } while (opcio != -1);

    }


    private static void modificarMaquina() {

        /**
         * Ha de permetre:
         *      - modificar les posicions on hi ha els productes de la màquina (quin article va a cada lloc)
         *      - modificar stock d'un producte que hi ha a la màquina
         *      - afegir més ranures a la màquina
         */

        int opcio = Stdin.inputInt("""
                MODIFICAR MAQUINA:
                ===============
                1. Modificar posicions
                2. Modificar stock
                3. Afegir slots""");

        switch (opcio) {
            case 1 -> modificarPosicions();
            case 2 -> modificarStock();
            case 3 -> afegirSlots();
            default -> System.out.println("Opcio no valida");
        }
    }

    private static void modificarPosicions() {
        mostrarMaquina();
        int positionSlot1 = Stdin.inputInt("Introdueix el primer slot: ");
        int positionSlot2 = Stdin.inputInt("Introdueix el segon slot: ");
        int positionAux;
        Slot slot1;
        Slot slot2;

        try {
            slot1 = slotDAO.readSlot(positionSlot1);
            slot2 = slotDAO.readSlot(positionSlot2);
        } catch (SQLException exception) {
            System.out.println("No existeix aquest slot!");
            exception.printStackTrace();
            return;
        }

        try {
            positionAux = slot1.getPosicio();
            slot1.setPosicio(0);
            slotDAO.updateSlot(slot1);

            slot1.setPosicio(slot2.getPosicio());
            slot2.setPosicio(positionAux);
            slotDAO.updateSlot(slot1);
            slotDAO.updateSlot(slot2);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        System.out.println("Posicions intercanviades correctament");
    }

    private static void modificarStock() {
        mostrarMaquina();
        int position = Stdin.inputInt("Introdueix el primer slot: ");
        Slot slot;

        int stock = Stdin.inputInt("Introdueix el nou stock: ");
        if (stock < 1) {
            System.out.println("El stock no es valid");
            return;
        }

        try {
            slot = slotDAO.readSlot(position);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }

        slot.setQuantitat(stock);

        try {
            slotDAO.updateSlot(slot);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    private static void afegirSlots() {
        System.out.println("""
                DADES SLOT A AFEGIR:
                ====================
                """);
        int posicio = Stdin.inputInt("- Posició: ");
        int quantitat = Stdin.inputInt("- Quantitat (unitats x producte): ");
        String codi_producte = Stdin.input("Codi producte: ");

        Slot s = new Slot(posicio, quantitat, codi_producte);

        Slot slotLlegit;

        try {
            slotLlegit = slotDAO.readSlot(posicio);
        } catch (SQLException exception){
            exception.printStackTrace();
            return;
        }

        if (slotLlegit != null) {
            System.out.println("Aquest slot ja existeix.");
            return;
        }

        try {
            slotDAO.createSlot(s);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void afegirProductes() {

        /**
         *      Crear un nou producte amb les dades que ens digui l'operari
         *      Agefir el producte a la BD (tenir en compte les diferents situacions que poden passar)
         *          El producte ja existeix
         *              - Mostrar el producte que té el mateix codiProducte
         *              - Preguntar si es vol actualitzar o descartar l'operació
         *          El producte no existeix
         *              - Afegir el producte a la BD
         *
         *     Podeu fer-ho amb llenguatge SQL o mirant si el producte existeix i després inserir o actualitzar
         */

        System.out.println("""
                DADES PRODUCTE:
                ===============""");
        String codi = Stdin.input("- Codi producte: ");
        String nom = Stdin.input("- Nom: ");
        String descripcio = Stdin.input("- Descripció: ");
        float preuCompra = (float) Stdin.inputDouble("- Preu compra: ");
        float preuVenda = (float) Stdin.inputDouble("- Preu venda: ");

        Producte p = new Producte(codi, nom, descripcio, preuCompra, preuVenda);

        Producte producteLlegit;

        try {
            producteLlegit = producteDAO.readProducte(codi);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }

        if (producteLlegit != null) {
            System.out.println("""
                    Producte ja existent. Què vols fer?
                    """);
            int opcio = Stdin.inputInt("""
                    1 - Actualitzar
                    2 - Descartar canvis
                    """);

            if (opcio == 2) {
                return;
            }

            try {
                producteDAO.updateProducte(p);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return;
        }

        try {
            //Demanem de guardar el producte p a la BD
            producteDAO.createProducte(p);

            //Agafem tots els productes de la BD i els mostrem (per comprovar que s'ha afegit)
            ArrayList<Producte> productes = producteDAO.readProductes();
            for (Producte prod : productes) {
                System.out.println(prod);
            }

        } catch (SQLException e) {          //TODO: tractar les excepcions
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    private static void mostrarInventari() {

        try {
            //Agafem tots els productes de la BD i els mostrem
            ArrayList<Producte> productes = producteDAO.readProductes();
            for (Producte prod : productes) {
                System.out.println(prod);
            }

        } catch (SQLException e) {          //TODO: tractar les excepcions
            e.printStackTrace();
        }
    }

    private static void comprarProducte() {

        /**
         * Mínim: es realitza la compra indicant la posició on es troba el producte que es vol comprar
         * Ampliació (0.5 punts): es permet entrar el NOM del producte per seleccionar-lo (abans cal mostrar els
         * productes disponibles a la màquina)
         *
         * Tingueu en compte que quan s'ha venut un producte HA DE QUEDAR REFLECTIT a la BD que n'hi ha un menys.
         * (stock de la màquina es manté guardat entre reinicis del programa)
         */

        // Mostrant a l'usuari els productes disponibles
        mostrarMaquina();

        // Demanant a l'usuari per un slot
        int posicio = Stdin.inputInt("Introdueix la posicio del producte que vols comprar: ");
        Slot slot;

        // Llegint slot de la base de dades
        try {
            slot = slotDAO.readSlot(posicio);
            if (slot == null) {
                System.out.printf("No hi ha un slot a la posicio: %d\n", posicio);
                return;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }

        // Comprovant que hi ha productes a aquest slot
        if (slot.getQuantitat() < 1) {
            System.out.println("No hi ha stock disponible.");
            return;
        }
        // Reduint estoc
        slot.setQuantitat(slot.getQuantitat() - 1);

        // Actualitzant slot
        try {
            slotDAO.updateSlot(slot);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        System.out.println("Venda realitzada correctament.");

        // Afegint producte comprat al llistat
        Producte producteComprat;
        try {
            producteComprat = producteDAO.readProducte(slot.getCodiProducte());
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }
        float benefici = producteComprat.getPreuVenta() - producteComprat.getPreuCompra();
        try {
            beneficisDAO.afegirBenefici(benefici);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void mostrarMaquina() {

        /** IMPORTANT **
         * S'està demanat NOM DEL PRODUCTE no el codiProducte (la taula Slot conté posició, codiProducte i stock)
         * també s'acceptarà mostrant només el codi producte, però comptarà menys.
         *
         * Posicio      Producte                Quantitat disponible
         * ===========================================================
         * 1            Patates 3D              8
         * 2            Doritos Tex Mex         6
         * 3            Coca-Cola Zero          10
         * 4            Aigua 0.5L              7
         */
        ArrayList<Slot> slots;
        try {
            slots = slotDAO.readSlots();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }

        try {
            for (Slot slot : slots) {
                Producte producte = producteDAO.readProducte(slot.getCodiProducte());
                System.out.printf("%d   %s  %d\n", slot.getPosicio(), producte.getNom(), slot.getQuantitat());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

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

    private static void mostrarBenefici() {

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

        float benefici;
        try {
            benefici = beneficisDAO.obtenirBeneficis();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }
        System.out.printf("El benefici es de: %.2f\n", benefici);
    }
}
