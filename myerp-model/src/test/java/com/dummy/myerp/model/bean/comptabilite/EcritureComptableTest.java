package com.dummy.myerp.model.bean.comptabilite;


import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;


public class EcritureComptableTest {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }

    @Test
    public void isEquilibreeTest() {
        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }
    
    @Test 
    public void getTotalDebitTest() {
        EcritureComptable vEcriture = new EcritureComptable();
        
        String debit1 = "100.50";
        String debit2 = "35";
        String debit3 = "-5";
        String debit4 = null;
       
        vEcriture.setLibelle("Total débit");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, debit1, null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, debit2, "10"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, debit3, null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, debit4, null));
        vEcriture.getListLigneEcriture().add(new LigneEcritureComptable());
        
        BigDecimal totalDebit = new BigDecimal(debit1).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(debit2).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(debit3).setScale(2,RoundingMode.HALF_UP)));
             
        Assert.assertTrue(totalDebit.compareTo(vEcriture.getTotalDebit()) == 0);
	}
    
    @Test 
    public void getTotalCredit() {
    	EcritureComptable vEcriture = new EcritureComptable();
        
        String credit1 = "300.70";
        String credit2 = "7";
        String credit3 = "-5";
        String credit4 = null;
       
        vEcriture.setLibelle("Total crédit");
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, credit1));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "10", credit2));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, credit3));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, credit4));
        vEcriture.getListLigneEcriture().add(new LigneEcritureComptable());
        
        BigDecimal totalCredit = new BigDecimal(credit1).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(credit2).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(credit3).setScale(2,RoundingMode.HALF_UP)));
             
        Assert.assertTrue(totalCredit.compareTo(vEcriture.getTotalCredit()) == 0);
    }
}
