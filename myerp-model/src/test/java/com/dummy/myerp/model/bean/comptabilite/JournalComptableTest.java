package com.dummy.myerp.model.bean.comptabilite;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class JournalComptableTest {

	@Test
	public void getByCodeTest() {
		List<JournalComptable> listjournalComptables = new ArrayList<JournalComptable>();
		
		JournalComptable journalComptable1 = new JournalComptable("AC", "Achat");
		JournalComptable journalComptable2 = new JournalComptable("VE", "Vente");
		JournalComptable journalComptable3 = new JournalComptable();
		
		listjournalComptables.add(journalComptable1);
		listjournalComptables.add(journalComptable2);
		listjournalComptables.add(journalComptable3);
		
		assertTrue(listjournalComptables.toString(), JournalComptable.getByCode(listjournalComptables, "VE").equals(journalComptable2));
	}
	
	@Test
	public void getByCodeIsNullTest() {
		List<JournalComptable> listjournalComptables = new ArrayList<JournalComptable>();		
		assertNull(listjournalComptables.toString(), JournalComptable.getByCode(listjournalComptables, "AC"));
		
		listjournalComptables.add(new JournalComptable("AC", "Vente"));
		assertNull(listjournalComptables.toString(), JournalComptable.getByCode(listjournalComptables, "VE"));
	}
}
