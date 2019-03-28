package com.dummy.myerp.business.testIT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;


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
    
    
    //test de validité du format numérique des lignes d'écritures comptables
    @Test
    public void checkEcritureComptableContextRG7 () throws Exception {         
        List<EcritureComptable> listEcritures = manager.getListEcritureComptable();
        EcritureComptable ecritureComptable = listEcritures.get(0);
        //modifs pour création d'anomalies dans le format
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123).setScale(2,RoundingMode.HALF_UP)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP), null));
        manager.checkEcritureComptableUnit(ecritureComptable);
    }
}