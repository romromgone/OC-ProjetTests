package com.dummy.myerp.business.testIT;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SoldeCompteComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.technical.exception.TechnicalException;;


public class ComptabiliteManagerImplITTest extends BusinessTestCase {
	
	private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
	
	/**
     * Constructeur.
     */
    public ComptabiliteManagerImplITTest() {
        super();
    }
    
    
    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
    	SpringRegistry.init();
	}
    
    
    //Teste la validation de la règle 7 : les montants des lignes d'écritures peuvent comporter 2 chiffres maximum après la virgule
    //Cas 1 : valide
    @Test
    public void checkEcritureComptableUnitRG7Cas1() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
										                null, null,
										                new BigDecimal(123).setScale(2,RoundingMode.HALF_UP)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                										null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP), null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la non validation de la règle 7 : les montants des lignes d'écritures peuvent comporter 2 chiffres maximum après la virgule
    //Cas 2 : non valide, 3 décimales
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG7Cas2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
										                null, null,
										                new BigDecimal(123).setScale(3,RoundingMode.HALF_UP)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                										null, new BigDecimal(123).setScale(3,RoundingMode.HALF_UP), null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la validation de la règle 1 : calcul du solde
    @Test
    public void checkEcritureComptableUnitRG1Cas1() throws Exception {        
        
        // Test avec numeroCompteComptable innexistant renvoi solde == 0
         SoldeCompteComptable solde = manager.getSoldeCompteComptable(0);
        // Le solde doit être == 0
        assertTrue(solde.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
       
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411 = manager.getSoldeCompteComptable(411);
        assertFalse(solde411.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
    }
    
    
    //Teste la validation de la règle 1 : calcul du solde sur une période précise
    @Test
    public void checkEcritureComptableUnitRG1Cas2() throws Exception {
        // Test avec numeroCompteComptable innexistant renvoi solde == 0 et date aboutissant à des résultats
        SoldeCompteComptable solde0;
        solde0=manager.getSoldeCompteComptable(0,"2015-01-01", "2017-01-01");
        // Le solde doit être == 0
        assertTrue(solde0.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
        //
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411=manager.getSoldeCompteComptable(411,"2015-01-01", "2017-01-01");
        assertFalse(solde411.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
    }

    
    //Teste la validation de la règle 1 : calcul du solde en cherchant sur une période sans résultat
    @Test(expected = NotFoundException.class)
    public void checkEcritureComptableUnitRG1Cas3() throws Exception {
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411=manager.getSoldeCompteComptable(411,"2000-01-01", "2001-01-01");
    }

    
    //Teste la validation de la règle 1 : calcul du solde avec date invalide
    @Test(expected = TechnicalException.class)
    public void checkEcritureComptableUnitRG1Cas4() throws Exception {      
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411=manager.getSoldeCompteComptable(411,"3dzuh|de", "2018-01-01");
    }


    //Teste la validation de la règle 6 : la référence d'une écriture comptable doit être unique
    @Test
    public void checkEcritureComptableContextRG6Cas1() throws Exception {    
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        ectCptble.setId(-6);
        ectCptble.setReference(ectCptble.getReference().replace("2016","2017"));
        //execution du test
        manager.checkEcritureComptableContext(ectCptble);
        //modifs pour création d'une nouvelle réference unique
        ectCptble.setId(null);
        ectCptble.setReference(ectCptble.getReference().replace("2016","2017"));
        //execution du test
        manager.checkEcritureComptableContext(ectCptble);
    }


  	//Teste la non validation de la règle 6 
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6Cas2() throws Exception {      
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        ectCptble.setId(-6);
        manager.checkEcritureComptableContext(ectCptble);
    }


    //Teste la non validation de la règle 6 : reference id null
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6Cas3() throws Exception {
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        ectCptble.setId(null);
        manager.checkEcritureComptableContext(ectCptble);
    }


    //Teste la non validation de la règle 6 : non-unicité avec id existant
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6Cas4() throws Exception {
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        ectCptble.setId(-4);
        manager.checkEcritureComptableContext(ectCptble);
    }


    //Teste la validation de la règle 6 : la réference existe mais l'id est =
    @Test
    public void checkEcritureComptableContextRG6Cas5 () throws Exception {
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        manager.checkEcritureComptableContext(ectCptble);
    }

    //Test général des écritures comptables
    @Test
    public void checkEcritureComptableGeneral() throws Exception {
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(2);// On choisit une écriture comptable
        //
        manager.checkEcritureComptable(ectCptble);// On teste l'écriture comptable sur toutes les règles
    }


    //Test de addreference() sans erreur
    @Test
    public void addreferenceTestCas1() throws Exception {
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        // suppression la reference
        ectCptble.setReference("");
        //
        manager.addReference(ectCptble);
        //
    }


    //Test de addreference() avec journal_code incorrect
    @Test(expected = FunctionalException.class)
    public void addreferenceTestCas2() throws Exception {
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        // suppression la reference et set d'un journal_code erroné
        ectCptble.setReference("");
        JournalComptable jrnl = new JournalComptable("AS","AS de pique");
        ectCptble.setJournal(jrnl);
        // Test de deux cas d'exceptions fonctionnelles
        // 1) journal_code non reconnu
        // 2) journal_code nul
        try{
            manager.addReference(ectCptble);
        } catch(FunctionalException e) {
            JournalComptable jrnl2 = new JournalComptable();
            ectCptble.setJournal(jrnl2);
            manager.addReference(ectCptble);
        }
        //
    }

    
    //Test de addreference() sans erreur avec commencement d'une nouvelle numerotation
    // suite à un changement d'année (premiere écriture comptable de l'année)
	/*
	 * @Test public void addreferenceTestCas3() throws Exception { EcritureComptable
	 * ectCptble; List<EcritureComptable> listEcritures; //gets listEcritures =
	 * manager.getListEcritureComptable(); ectCptble=listEcritures.get(0); //
	 * suppression la reference et modification de l'année
	 * ectCptble.setReference(""); Date newDate = new Date(); newDate.setYear(117);
	 * // + 1900 newDate.setMonth(0); // Janv newDate.setDate(1); // 1er
	 * ectCptble.setDate(newDate); // manager.addReference(ectCptble); // String
	 * indiceJournal =
	 * ectCptble.getReference().substring(ectCptble.getReference().length() - 5,
	 * ectCptble.getReference().length()); //
	 * assertTrue(indiceJournal.equals("00001")); }
	 */

    
    //Test général avec génération de réferences, des checks, des insert, des select, des updates et des delete via un petit scénario
    @Test
    public void checkSenarioEcritureComptable() throws Exception {
        EcritureComptable vEcritureComptable;
        EcritureComptable vEcritureComptable2;
        List<EcritureComptable> lEcritures;
        int idEcritureEnBase=0;
        boolean isPushed=false;
        boolean isUpdated=false;
        boolean isDeleted=false;
        boolean testSucceed=false;
        // set journal comptable associé
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("VE", "Vente"));
        //vsetDate
        Date dateEcritureComptable = new Date();
        dateEcritureComptable.setYear(119);//118 aka 2018-1900(Reference pour l'objet Date)
        dateEcritureComptable.setMonth(0);
        dateEcritureComptable.setDate(1);
        vEcritureComptable.setDate(dateEcritureComptable);
        //
        // definition de la réference associée
        manager.addReference(vEcritureComptable);
        //
        vEcritureComptable.setLibelle("TMA Appli Zzz");
        //
        BigDecimal ttc=new BigDecimal(5000);
        BigDecimal ht=new BigDecimal(4166.66);
        BigDecimal tva=new BigDecimal(833.34);
        //
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Facture C110005", ttc.setScale(2,RoundingMode.HALF_UP),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(706),
                "TMA Appli Zzz", null, ht.setScale(2,RoundingMode.HALF_UP)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4457),
                "TVA 20%", null, tva.setScale(2,RoundingMode.HALF_UP)));
        //
        vEcritureComptable2 = (EcritureComptable)vEcritureComptable.clone();
        //
        // check
        manager.checkEcritureComptable(vEcritureComptable);// On teste l'écriture comptable sur toutes les règles
        // push
        manager.insertEcritureComptable(vEcritureComptable);// Test de l'écriture en base
        // pull et check 
        lEcritures = manager.getListEcritureComptable();
        for (EcritureComptable ecr:lEcritures) {
            if(ecr.getReference().equals(vEcritureComptable.getReference())){
                idEcritureEnBase = ecr.getId();
                isPushed = true;
            }
        }
        // modifs
        vEcritureComptable2.setId(idEcritureEnBase);
        vEcritureComptable2.setDate(new Date());
        //
        // check
        manager.checkEcritureComptable(vEcritureComptable2);// On teste l'écriture comptable sur toutes les règles
        // update
        manager.updateEcritureComptable(vEcritureComptable2);// Test de l'écriture en base
        //
        // pull et check (pas poulet tchècque)
        lEcritures = manager.getListEcritureComptable();
        for (EcritureComptable ecr:lEcritures) {
            if(ecr.getReference().equals(vEcritureComptable.getReference()) && ecr.getDate().getDate() == vEcritureComptable2.getDate().getDate() && ecr.getDate().getMonth() == vEcritureComptable2.getDate().getMonth() && ecr.getDate().getYear() == vEcritureComptable2.getDate().getYear()){
                idEcritureEnBase=ecr.getId();
                isUpdated=true;
            }
        }
        // delete
        try {
            manager.deleteEcritureComptable(idEcritureEnBase);// Test de suppression en base
            isDeleted = true;
        }catch(Exception e){
            // problem here
            e.printStackTrace();
        }
        testSucceed = isPushed && isUpdated && isDeleted;
        //
        assertTrue(testSucceed);
    }

}