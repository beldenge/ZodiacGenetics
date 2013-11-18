/**
 * Copyright 2013 George Belden
 * 
 * This file is part of ZodiacGenetics.
 * 
 * ZodiacGenetics is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ZodiacGenetics is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ZodiacGenetics. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.algorithms.mutation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.ciphertool.genetics.dao.SequenceDao;
import com.ciphertool.genetics.entities.Gene;
import com.ciphertool.genetics.mocks.MockChromosome;
import com.ciphertool.genetics.mocks.MockGene;
import com.ciphertool.genetics.mocks.MockSequence;

public class SingleSequenceMutationAlgorithmTest {
	private final static int MAX_MUTATIONS = 1;
	private static Logger logMock;
	private static SingleSequenceMutationAlgorithm singleSequenceMutationAlgorithm;
	private static SequenceDao sequenceDaoMock;

	@BeforeClass
	public static void setUp() {
		singleSequenceMutationAlgorithm = new SingleSequenceMutationAlgorithm();

		sequenceDaoMock = mock(SequenceDao.class);
		singleSequenceMutationAlgorithm.setSequenceDao(sequenceDaoMock);

		logMock = mock(Logger.class);
		Field logField = ReflectionUtils.findField(SingleSequenceMutationAlgorithm.class, "log");
		ReflectionUtils.makeAccessible(logField);
		ReflectionUtils.setField(logField, singleSequenceMutationAlgorithm, logMock);
	}

	@Before
	public void resetMocks() {
		singleSequenceMutationAlgorithm.setMaxMutationsPerChromosome(MAX_MUTATIONS);

		reset(logMock);
		reset(sequenceDaoMock);
	}

	@Test
	public void testSetSequenceDao() {
		SequenceDao sequenceDaoToSet = mock(SequenceDao.class);

		SingleSequenceMutationAlgorithm singleSequenceMutationAlgorithm = new SingleSequenceMutationAlgorithm();
		singleSequenceMutationAlgorithm.setSequenceDao(sequenceDaoToSet);

		Field sequenceDaoField = ReflectionUtils.findField(SingleSequenceMutationAlgorithm.class,
				"sequenceDao");
		ReflectionUtils.makeAccessible(sequenceDaoField);
		SequenceDao sequenceDaoFromObject = (SequenceDao) ReflectionUtils.getField(
				sequenceDaoField, singleSequenceMutationAlgorithm);

		assertSame(sequenceDaoToSet, sequenceDaoFromObject);
	}

	@Test
	public void testSetMaxMutationsPerChromosome() {
		Integer maxMutationsPerChromosomeToSet = 3;

		SingleSequenceMutationAlgorithm singleSequenceMutationAlgorithm = new SingleSequenceMutationAlgorithm();
		singleSequenceMutationAlgorithm
				.setMaxMutationsPerChromosome(maxMutationsPerChromosomeToSet);

		Field maxMutationsPerChromosomeField = ReflectionUtils.findField(
				SingleSequenceMutationAlgorithm.class, "maxMutationsPerChromosome");
		ReflectionUtils.makeAccessible(maxMutationsPerChromosomeField);
		Integer maxMutationsPerChromosomeFromObject = (Integer) ReflectionUtils.getField(
				maxMutationsPerChromosomeField, singleSequenceMutationAlgorithm);

		assertSame(maxMutationsPerChromosomeToSet, maxMutationsPerChromosomeFromObject);
	}

	@Test(expected = IllegalStateException.class)
	public void testMutateChromosomeNullMaxMutations() {
		singleSequenceMutationAlgorithm.setMaxMutationsPerChromosome(null);

		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene2);

		singleSequenceMutationAlgorithm.mutateChromosome(mockChromosome);
	}

	@Test
	public void testMutateChromosome() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene2);

		when(sequenceDaoMock.findRandomSequence(any(Gene.class), anyInt())).thenReturn(
				new MockSequence("x"));

		singleSequenceMutationAlgorithm.mutateChromosome(mockChromosome);

		MockGene originalMockGene1 = new MockGene();
		MockSequence mockGene1Sequence1 = new MockSequence("w");
		originalMockGene1.addSequence(mockGene1Sequence1);
		mockGene1Sequence1.setGene(mockGene1);
		MockSequence mockGene1Sequence2 = new MockSequence("e");
		originalMockGene1.addSequence(mockGene1Sequence2);
		mockGene1Sequence2.setGene(mockGene1);
		originalMockGene1.setChromosome(mockChromosome);

		MockGene originalMockGene2 = new MockGene();
		MockSequence mockGene2Sequence1 = new MockSequence("s");
		originalMockGene2.addSequence(mockGene2Sequence1);
		mockGene2Sequence1.setGene(mockGene2);
		MockSequence mockGene2Sequence2 = new MockSequence("m");
		originalMockGene2.addSequence(mockGene2Sequence2);
		mockGene2Sequence2.setGene(mockGene2);
		MockSequence mockGene2Sequence3 = new MockSequence("i");
		originalMockGene2.addSequence(mockGene2Sequence3);
		mockGene2Sequence3.setGene(mockGene2);
		MockSequence mockGene2Sequence4 = new MockSequence("l");
		originalMockGene2.addSequence(mockGene2Sequence4);
		mockGene2Sequence4.setGene(mockGene2);
		MockSequence mockGene2Sequence5 = new MockSequence("e");
		originalMockGene2.addSequence(mockGene2Sequence5);
		mockGene2Sequence5.setGene(mockGene2);
		originalMockGene2.setChromosome(mockChromosome);

		/*
		 * Only one Gene should be mutated.
		 */
		assertTrue((originalMockGene1.equals(mockGene1) && !originalMockGene2.equals(mockGene2))
				|| (!originalMockGene1.equals(mockGene1) && originalMockGene2.equals(mockGene2)));
		verify(sequenceDaoMock, times(1)).findRandomSequence(any(Gene.class), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateGene() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene2);

		when(sequenceDaoMock.findRandomSequence(any(Gene.class), anyInt())).thenReturn(
				new MockSequence("x"));

		singleSequenceMutationAlgorithm.mutateGene(mockChromosome, 0);

		// Only one of the letters from the first Gene should be changed
		assertTrue(("w".equals(mockGene1.getSequences().get(0).getValue()) && !"e".equals(mockGene1
				.getSequences().get(1).getValue()))
				|| (!"w".equals(mockGene1.getSequences().get(0).getValue()) && "e".equals(mockGene1
						.getSequences().get(1).getValue())));
		assertTrue("s".equals(mockGene2.getSequences().get(0).getValue())
				&& "m".equals(mockGene2.getSequences().get(1).getValue())
				&& "i".equals(mockGene2.getSequences().get(2).getValue())
				&& "l".equals(mockGene2.getSequences().get(3).getValue())
				&& "e".equals(mockGene2.getSequences().get(4).getValue()));
		verify(sequenceDaoMock, times(1)).findRandomSequence(same(mockGene1), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateInvalidGene() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		mockGene.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene);

		singleSequenceMutationAlgorithm.mutateGene(mockChromosome, 1);

		// Nothing should be changed
		assertEquals("s", mockGene.getSequences().get(0).getValue());
		assertEquals("m", mockGene.getSequences().get(1).getValue());
		assertEquals("i", mockGene.getSequences().get(2).getValue());
		assertEquals("l", mockGene.getSequences().get(3).getValue());
		assertEquals("e", mockGene.getSequences().get(4).getValue());
		verifyZeroInteractions(sequenceDaoMock);
		verify(logMock, times(1)).info(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomGene() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene2);

		when(sequenceDaoMock.findRandomSequence(any(Gene.class), anyInt())).thenReturn(
				new MockSequence("x"));

		Integer mutatedIndex = singleSequenceMutationAlgorithm.mutateRandomGene(mockChromosome,
				Arrays.asList(0, 1));

		MockGene originalMockGene1 = new MockGene();
		MockSequence mockGene1Sequence1 = new MockSequence("w");
		originalMockGene1.addSequence(mockGene1Sequence1);
		mockGene1Sequence1.setGene(mockGene1);
		MockSequence mockGene1Sequence2 = new MockSequence("e");
		originalMockGene1.addSequence(mockGene1Sequence2);
		mockGene1Sequence2.setGene(mockGene1);
		originalMockGene1.setChromosome(mockChromosome);

		MockGene originalMockGene2 = new MockGene();
		MockSequence mockGene2Sequence1 = new MockSequence("s");
		originalMockGene2.addSequence(mockGene2Sequence1);
		mockGene2Sequence1.setGene(mockGene2);
		MockSequence mockGene2Sequence2 = new MockSequence("m");
		originalMockGene2.addSequence(mockGene2Sequence2);
		mockGene2Sequence2.setGene(mockGene2);
		MockSequence mockGene2Sequence3 = new MockSequence("i");
		originalMockGene2.addSequence(mockGene2Sequence3);
		mockGene2Sequence3.setGene(mockGene2);
		MockSequence mockGene2Sequence4 = new MockSequence("l");
		originalMockGene2.addSequence(mockGene2Sequence4);
		mockGene2Sequence4.setGene(mockGene2);
		MockSequence mockGene2Sequence5 = new MockSequence("e");
		originalMockGene2.addSequence(mockGene2Sequence5);
		mockGene2Sequence5.setGene(mockGene2);
		originalMockGene2.setChromosome(mockChromosome);

		/*
		 * Only one Gene should be mutated.
		 */
		assertTrue((originalMockGene1.equals(mockGene1) && !originalMockGene2.equals(mockGene2))
				|| (!originalMockGene1.equals(mockGene1) && originalMockGene2.equals(mockGene2)));
		assertTrue(mutatedIndex == 0 || mutatedIndex == 1);
		verify(sequenceDaoMock, times(1)).findRandomSequence(any(Gene.class), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithUsedIndex() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene2);

		when(sequenceDaoMock.findRandomSequence(any(Gene.class), anyInt())).thenReturn(
				new MockSequence("x"));

		Integer mutatedIndex = singleSequenceMutationAlgorithm.mutateRandomGene(mockChromosome,
				Arrays.asList(1));

		MockGene originalMockGene1 = new MockGene();
		MockSequence mockGene1Sequence1 = new MockSequence("w");
		originalMockGene1.addSequence(mockGene1Sequence1);
		mockGene1Sequence1.setGene(mockGene1);
		MockSequence mockGene1Sequence2 = new MockSequence("e");
		originalMockGene1.addSequence(mockGene1Sequence2);
		mockGene1Sequence2.setGene(mockGene1);
		originalMockGene1.setChromosome(mockChromosome);

		MockGene originalMockGene2 = new MockGene();
		MockSequence mockGene2Sequence1 = new MockSequence("s");
		originalMockGene2.addSequence(mockGene2Sequence1);
		mockGene2Sequence1.setGene(mockGene2);
		MockSequence mockGene2Sequence2 = new MockSequence("m");
		originalMockGene2.addSequence(mockGene2Sequence2);
		mockGene2Sequence2.setGene(mockGene2);
		MockSequence mockGene2Sequence3 = new MockSequence("i");
		originalMockGene2.addSequence(mockGene2Sequence3);
		mockGene2Sequence3.setGene(mockGene2);
		MockSequence mockGene2Sequence4 = new MockSequence("l");
		originalMockGene2.addSequence(mockGene2Sequence4);
		mockGene2Sequence4.setGene(mockGene2);
		MockSequence mockGene2Sequence5 = new MockSequence("e");
		originalMockGene2.addSequence(mockGene2Sequence5);
		mockGene2Sequence5.setGene(mockGene2);
		originalMockGene2.setChromosome(mockChromosome);

		/*
		 * Only one Gene should be mutated.
		 */
		assertEquals(originalMockGene1, mockGene1);
		assertFalse(originalMockGene2.equals(mockGene2));
		assertTrue(mutatedIndex == 0 || mutatedIndex == 1);
		assertEquals(mutatedIndex, new Integer(1));
		verify(sequenceDaoMock, times(1)).findRandomSequence(same(mockGene2), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateRandomGeneWithAllIndicesUsed() {
		MockChromosome mockChromosome = new MockChromosome();

		MockGene mockGene1 = new MockGene();
		mockGene1.addSequence(new MockSequence("w"));
		mockGene1.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene1);

		MockGene mockGene2 = new MockGene();
		mockGene2.addSequence(new MockSequence("s"));
		mockGene2.addSequence(new MockSequence("m"));
		mockGene2.addSequence(new MockSequence("i"));
		mockGene2.addSequence(new MockSequence("l"));
		mockGene2.addSequence(new MockSequence("e"));
		mockChromosome.addGene(mockGene2);

		Integer mutatedIndex = singleSequenceMutationAlgorithm.mutateRandomGene(mockChromosome,
				new ArrayList<Integer>());

		MockGene originalMockGene1 = new MockGene();
		MockSequence mockGene1Sequence1 = new MockSequence("w");
		originalMockGene1.addSequence(mockGene1Sequence1);
		mockGene1Sequence1.setGene(mockGene1);
		MockSequence mockGene1Sequence2 = new MockSequence("e");
		originalMockGene1.addSequence(mockGene1Sequence2);
		mockGene1Sequence2.setGene(mockGene1);
		originalMockGene1.setChromosome(mockChromosome);

		MockGene originalMockGene2 = new MockGene();
		MockSequence mockGene2Sequence1 = new MockSequence("s");
		originalMockGene2.addSequence(mockGene2Sequence1);
		mockGene2Sequence1.setGene(mockGene2);
		MockSequence mockGene2Sequence2 = new MockSequence("m");
		originalMockGene2.addSequence(mockGene2Sequence2);
		mockGene2Sequence2.setGene(mockGene2);
		MockSequence mockGene2Sequence3 = new MockSequence("i");
		originalMockGene2.addSequence(mockGene2Sequence3);
		mockGene2Sequence3.setGene(mockGene2);
		MockSequence mockGene2Sequence4 = new MockSequence("l");
		originalMockGene2.addSequence(mockGene2Sequence4);
		mockGene2Sequence4.setGene(mockGene2);
		MockSequence mockGene2Sequence5 = new MockSequence("e");
		originalMockGene2.addSequence(mockGene2Sequence5);
		mockGene2Sequence5.setGene(mockGene2);
		originalMockGene2.setChromosome(mockChromosome);

		/*
		 * No Genes should be mutated.
		 */
		assertTrue(originalMockGene1.equals(mockGene1) && originalMockGene2.equals(mockGene2));
		assertNull(mutatedIndex);
		verifyZeroInteractions(sequenceDaoMock);
		verify(logMock, times(1)).warn(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateSequence() {
		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		mockGene.addSequence(new MockSequence("e"));

		when(sequenceDaoMock.findRandomSequence(same(mockGene), anyInt())).thenReturn(
				new MockSequence("x"));

		singleSequenceMutationAlgorithm.mutateSequence(mockGene, 4);

		assertEquals("s", mockGene.getSequences().get(0).getValue());
		assertEquals("m", mockGene.getSequences().get(1).getValue());
		assertEquals("i", mockGene.getSequences().get(2).getValue());
		assertEquals("l", mockGene.getSequences().get(3).getValue());
		assertFalse("e".equals(mockGene.getSequences().get(4).getValue()));
		verify(sequenceDaoMock, times(1)).findRandomSequence(same(mockGene), anyInt());
		verifyZeroInteractions(logMock);
	}

	@Test
	public void testMutateInvalidSequence() {
		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		mockGene.addSequence(new MockSequence("e"));

		singleSequenceMutationAlgorithm.mutateSequence(mockGene, 5);

		// No sequences should be changed
		assertEquals("s", mockGene.getSequences().get(0).getValue());
		assertEquals("m", mockGene.getSequences().get(1).getValue());
		assertEquals("i", mockGene.getSequences().get(2).getValue());
		assertEquals("l", mockGene.getSequences().get(3).getValue());
		assertEquals("e", mockGene.getSequences().get(4).getValue());
		verify(logMock, times(1)).info(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateSequenceCannotFindDifferentSequence() {
		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		MockSequence sequenceToReplace = new MockSequence("e");
		mockGene.addSequence(sequenceToReplace);

		when(sequenceDaoMock.findRandomSequence(same(mockGene), anyInt())).thenReturn(
				new MockSequence("e"));
		when(logMock.isDebugEnabled()).thenReturn(true);

		singleSequenceMutationAlgorithm.mutateSequence(mockGene, 4);

		assertEquals("s", mockGene.getSequences().get(0).getValue());
		assertEquals("m", mockGene.getSequences().get(1).getValue());
		assertEquals("i", mockGene.getSequences().get(2).getValue());
		assertEquals("l", mockGene.getSequences().get(3).getValue());
		assertEquals("e", mockGene.getSequences().get(4).getValue());
		assertSame(sequenceToReplace, mockGene.getSequences().get(4));
		verify(sequenceDaoMock, times(1000)).findRandomSequence(same(mockGene), anyInt());
		verify(logMock, times(1)).isDebugEnabled();
		verify(logMock, times(1)).debug(anyString());
		verifyNoMoreInteractions(logMock);
	}

	@Test
	public void testMutateRandomSequence() {
		MockGene mockGene = new MockGene();
		mockGene.addSequence(new MockSequence("s"));
		mockGene.addSequence(new MockSequence("m"));
		mockGene.addSequence(new MockSequence("i"));
		mockGene.addSequence(new MockSequence("l"));
		mockGene.addSequence(new MockSequence("e"));

		when(sequenceDaoMock.findRandomSequence(same(mockGene), anyInt())).thenReturn(
				new MockSequence("x"));

		singleSequenceMutationAlgorithm.mutateRandomSequence(mockGene);

		assertFalse("s".equals(mockGene.getSequences().get(0).getValue())
				&& "m".equals(mockGene.getSequences().get(1).getValue())
				&& "i".equals(mockGene.getSequences().get(2).getValue())
				&& "l".equals(mockGene.getSequences().get(3).getValue())
				&& "e".equals(mockGene.getSequences().get(4).getValue()));
		verify(sequenceDaoMock, times(1)).findRandomSequence(same(mockGene), anyInt());
		verifyZeroInteractions(logMock);
	}
}
