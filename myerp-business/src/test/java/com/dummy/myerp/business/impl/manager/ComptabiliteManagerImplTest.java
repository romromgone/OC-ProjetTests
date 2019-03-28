package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.junit.Test;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;


public class ComptabiliteManagerImplTest {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();


    @Test
    public void checkEcritureComptableUnit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123).setScale(2, RoundingMode.HALF_UP),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123).setScale(2, RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    
    //Teste la non validation des champs d'EcritureComptable par le ConstraintValidator
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    
    //Teste de la non validation de la règle 2 : l'équilibre de l'écriture comptable
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la non validation de la règle 3 : au moins deux lignes d'écriture, une au débit et une au crédit
    //Cas 1 : 2 lignes en débit
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Cas1() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
                                                                                 null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la non validation de la règle 3 : au moins deux lignes d'écriture, une au débit et une au crédit
    //Cas 2 : 1 ligne à l'équilibre
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Cas2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123).setScale(2, RoundingMode.HALF_UP), 
    																			new BigDecimal(123).setScale(2, RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la non validation de la règle 3 : au moins deux lignes d'écriture, une au débit et une au crédit
    //Cas 3 : 1 ligne à Null
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Cas3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la validation de la règle 4 : les montants des lignes d'écriture sont signés et peuvent prendre des valeurs négatives
    //Cas 1 : 2 lignes équilibrées avec montants négatifs
    @Test
    public void checkEcritureComptableUnitRG4Cas1() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
										                null, new BigDecimal(-123).setScale(2,RoundingMode.HALF_UP),
										                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
										                null, null,
										                new BigDecimal(-123).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la validation de la règle 4 : les montants des lignes d'écriture sont signés et peuvent prendre des valeurs négatives
    //Cas 2 : 3 lignes à l'équilibre avec un seul montant négatif
    @Test
    public void checkEcritureComptableUnitRG4Cas2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
										                null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
										                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
										                null, null,
										                new BigDecimal(-123).setScale(2,RoundingMode.HALF_UP)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
										                null, null,
										                new BigDecimal(246).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la non validation de la règle 5 : bon formatage de la référence (XX-AAAA/#####) et bonnes données 
    //Cas 1 : mauvais code du journal comptable dans la référence
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5Cas1() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("VE", "Vente"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
									                null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
									                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
									                null, null,
									                new BigDecimal(123).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    //Teste la non validation de la règle 5 : bon formatage de la référence (XX-AAAA/#####) et bonnes données 
    //Cas 2 : mauvaise date dans la référence
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5Cas2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("VE", "Vente"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("VE-2016/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
									                null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
									                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
									                null, null,
									                new BigDecimal(123).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    //Teste la non validation de la règle 5 : bon formatage de la référence (XX-AAAA/#####) et bonnes données 
    //Cas 3 : mauvais format pour le numéro de séquence
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5Cas3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("VE", "Vente"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2019/0001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
									                null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
									                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
									                null, null,
									                new BigDecimal(123).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
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
}
