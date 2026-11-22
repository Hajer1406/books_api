package com.hajer.livres_proj;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hajer.livres_proj.entities.Ecrivain;
import com.hajer.livres_proj.entities.Livre;
import com.hajer.livres_proj.repos.LivreRepository;

@SpringBootTest
class LivresProjApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private LivreRepository livreRepository;

	@Test
	public void testCreateLivre() {
		Livre liv = new Livre("Le Petit Prince", 120L, new Date());
		Livre saved = livreRepository.save(liv);
		assertNotNull(saved.getIdLivre());
	}

	@Test
	public void testFindLivre() {
		Livre liv = new Livre("Livre Test Find", 200L, new Date());
		Livre saved = livreRepository.save(liv);
		Livre l = livreRepository.findById(saved.getIdLivre()).orElse(null);
		assertNotNull(l);
		System.out.println(l);
	}

	@Test
	public void testUpdateLivre() {
		Livre liv = new Livre("Livre Test Update", 150L, new Date());
		Livre saved = livreRepository.save(liv);
		saved.setNombrePages(180L);
		Livre updated = livreRepository.save(saved);
		assertNotNull(updated);
	}

	@Test
	public void testDeleteLivre() {
		Livre liv = new Livre("Livre Test Delete", 80L, new Date());
		Livre saved = livreRepository.save(liv);
		livreRepository.deleteById(saved.getIdLivre());
	}

	@Test
	public void testListerTousLivres() {
		List<Livre> livs = livreRepository.findAll();
		assertNotNull(livs);
		for (Livre l : livs) {
			System.out.println(l);
		}
	}

	@Test
	public void testFindByTitreLivre() {
		Livre liv = new Livre("Le Petit Prince", 120L, new Date());
		livreRepository.save(liv);
		List<Livre> livs = livreRepository.findByTitreLivre("Le Petit Prince");
		assertNotNull(livs);
	}

	@Test
	public void testFindByTitreLivreContains() {
		Livre liv = new Livre("Le Prince et le Pauvre", 250L, new Date());
		livreRepository.save(liv);
		List<Livre> livs = livreRepository.findByTitreLivreContains("Prince");
		assertNotNull(livs);
	}

	@Test
	public void testfindByTitrePages() {
		Livre liv = new Livre("Prince", 100L, new Date());
		livreRepository.save(liv);
		List<Livre> livs = livreRepository.findByTitrePages("Prince", 100L);
		assertNotNull(livs);
	}

	@Test
	public void testfindByEcrivain() {
		Ecrivain ecriv = new Ecrivain();
		ecriv.setIdEcrivain(1L);
		List<Livre> livs = livreRepository.findByEcrivain(ecriv);
		assertNotNull(livs);
	}

	@Test
	public void findByEcrivainIdEcrivain() {
		List<Livre> livs = livreRepository.findByEcrivainIdEcrivain(1L);
		assertNotNull(livs);
	}

	@Test
	public void testfindByOrderByTitreLivreAsc() {
		List<Livre> livs = livreRepository.findByOrderByTitreLivreAsc();
		assertNotNull(livs);
	}

	@Test
	public void testTrierLivresTitresPages() {
		List<Livre> livs = livreRepository.trierLivresTitresPages();
		assertNotNull(livs);
	}

}
