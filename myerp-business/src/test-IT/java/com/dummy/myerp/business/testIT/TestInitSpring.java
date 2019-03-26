package com.dummy.myerp.business.testIT;

import org.junit.Test;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;

import static org.junit.Assert.assertNotNull;

import java.util.List;


/**
 * Classe de test de l'initialisation du contexte Spring
 */
public class TestInitSpring extends BusinessTestCase {

    /**
     * Constructeur.
     */
    public TestInitSpring() {
        super();
    }


    /**
     * Teste l'initialisation du contexte Spring
     */
    @Test
    public void testInit() {
        SpringRegistry.init();
        assertNotNull(SpringRegistry.getBusinessProxy());
        assertNotNull(SpringRegistry.getTransactionManager());
    }
    
    @Test
    public void testgetListCompteComptable() {
    	SpringRegistry.init();
    	List<CompteComptable> list = SpringRegistry.getBusinessProxy().getComptabiliteManager().getListCompteComptable();
    	assertNotNull(list);
    }
}
