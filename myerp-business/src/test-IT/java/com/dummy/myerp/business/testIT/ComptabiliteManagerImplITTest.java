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
    public void checkEcritureComptableUnitRG1() throws Exception {        
        
        // Test avec numeroCompteComptable innexistant renvoi solde == 0
         SoldeCompteComptable solde = manager.getSoldeCompteComptable(0);
        // Le solde doit être == 0
        assertTrue(solde.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
       
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411 = manager.getSoldeCompteComptable(411);
        assertFalse(solde411.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
    }
}