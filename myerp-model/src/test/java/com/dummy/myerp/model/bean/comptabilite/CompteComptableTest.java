package com.dummy.myerp.model.bean.comptabilite;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class CompteComptableTest {

	@Test
	public void getByNumeroTest() {
		List<CompteComptable> listCompteComptables = new ArrayList<CompteComptable>();
		
		CompteComptable compteComptable1 = new CompteComptable(401, "Fournisseurs");
		CompteComptable compteComptable2 = new CompteComptable(512, "Banque");
		CompteComptable compteComptable3 = new CompteComptable();
		
		listCompteComptables.add(compteComptable1);
		listCompteComptables.add(compteComptable2);
		listCompteComptables.add(compteComptable3);
		
		assertTrue(CompteComptable.getByNumero(listCompteComptables, 512).equals(compteComptable2));
	}
	
	@Test
	public void getByNumeroIsNullTest() {
		List<CompteComptable> listCompteComptables = new ArrayList<CompteComptable>();		
		assertNull(CompteComptable.getByNumero(listCompteComptables, 512));
		
		listCompteComptables.add(new CompteComptable(401, "Fournisseurs"));
		assertNull(CompteComptable.getByNumero(listCompteComptables, 606));
	}
}
