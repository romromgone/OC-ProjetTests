package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }

    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }
    
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }
      
    @Override
    public int getValSequenceJournalComptable(String codeJournalComptable, int anneeEcritureComptable) throws NotFoundException {
    	return getDaoProxy().getComptabiliteDao().getValSequenceJournalByCodeJournalComptableAndAnnee(codeJournalComptable, anneeEcritureComptable);
    }

    
    // TODO à tester
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException {       
        String codeJournalComptable = pEcritureComptable.getJournal().getCode();
        List<JournalComptable> listJournalComptables = getListJournalComptable();
        if (listJournalComptables.stream().noneMatch(journalComptable -> journalComptable.getCode().equals(codeJournalComptable))) {
        	throw new FunctionalException("Le code journal de l'écriture comptable n'existe pas");
        }
        
        int valSequenceJournal;
        int anneeEcritureComptable = pEcritureComptable.getDate().getYear()+1900;
        try {
            valSequenceJournal = getValSequenceJournalComptable(codeJournalComptable, anneeEcritureComptable) + 1;
        } catch (NotFoundException e) {
        	valSequenceJournal = 1;
        }
        
        String valSequenceJournalForReference = Integer.toString(valSequenceJournal);
        String sequenceTemplate = "00000";
        valSequenceJournalForReference = sequenceTemplate.substring(0, 5 - valSequenceJournalForReference.length()) + valSequenceJournalForReference;
        String reference = codeJournalComptable + "-" + anneeEcritureComptable + "/" + valSequenceJournalForReference;
        pEcritureComptable.setReference(reference);
        
        try {
            updateOrInsertValSequenceJournalComptable(codeJournalComptable, anneeEcritureComptable, valSequenceJournal);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }

    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    public void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        // Inutile déjà validé au dessus avec la vérification des contraintes unitaires
		/*
		 * int vNbrCredit = 0; int vNbrDebit = 0; for (LigneEcritureComptable
		 * vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) { if
		 * (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.
		 * getCredit(), BigDecimal.ZERO)) != 0) { vNbrCredit++; } if
		 * (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.
		 * getDebit(), BigDecimal.ZERO)) != 0) { vNbrDebit++; } } // On test le nombre
		 * de lignes car si l'écriture à une seule ligne // avec un montant au débit et
		 * un montant au crédit ce n'est pas valable if
		 * (pEcritureComptable.getListLigneEcriture().size() < 2 || vNbrCredit < 1 ||
		 * vNbrDebit < 1) { throw new FunctionalException(
		 * "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit."
		 * ); }
		 */

        // RG_Compta_5 : Format et contenu de la référence
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal
        // les contraintes de format sont déjà validé au dessus également grâce au regex
        if (!pEcritureComptable.getReference().substring(3,7).equals(Integer.toString(pEcritureComptable.getDate().getYear()+1900))) {        
            throw new FunctionalException("Erreur de contenu de l'année dans la reference de l'écriture comptable : " + pEcritureComptable.getReference().substring(3,7) + " par rapport à la date : " + Integer.toString(pEcritureComptable.getDate().getYear()+1900));
        }
        if (!pEcritureComptable.getReference().substring(0,2).equals(pEcritureComptable.getJournal().getCode())) {
            throw new FunctionalException("Erreur de contenu du code journal dans la reference de l'écriture comptable : " + pEcritureComptable.getReference().substring(0,2) + " par rapport au code : " + pEcritureComptable.getJournal().getCode());
        }
        
        // RG_Compta_7 : Les montants des lignes d'écritures peuvent comporter 2 chiffres maximum après la virgule.
        // idem traité avec le ConstraintValidator
    }

    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    public void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                    pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
 
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
    	this.checkEcritureComptable(pEcritureComptable); // ERREUR : ajout car manquant 
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    @Override
    public void updateOrInsertValSequenceJournalComptable(String codeJournalComptable, int anneeEcritureComptable, int valSequenceJournal) throws NotFoundException {
    	TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
        	if (valSequenceJournal > 1) {
        		getDaoProxy().getComptabiliteDao().updateValSequenceJournalComptable(codeJournalComptable, anneeEcritureComptable, valSequenceJournal);
        	} else {
        		getDaoProxy().getComptabiliteDao().insertValSequenceJournalComptable(codeJournalComptable, anneeEcritureComptable, valSequenceJournal);
        	}     	
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
    
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}
