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

package com.ciphertool.genetics.algorithms.crossover;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ciphertool.genetics.entities.Chromosome;

public class LowestCommonGroupCrossoverAlgorithmHelperTest extends CrossoverAlgorithmTestBase {
	@Test
	public void testAdvanceIndexes() {
		Chromosome child = getMom();
		Chromosome parent = getDad();
		LowestCommonGroupCrossoverProgressDto crossoverProgressDto = new LowestCommonGroupCrossoverProgressDto();

		crossoverProgressDto.setFirstChromosomeSequencePosition(child.getGenes().get(0).size());
		crossoverProgressDto.setSecondChromosomeSequencePosition(parent.getGenes().get(0).size());

		LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
				parent, 0);

		assertEquals(6, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(8, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(1, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeEndGeneIndex());
		assertEquals(1, crossoverProgressDto.getFirstChromosomeEndGeneIndex());

		LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
				parent, 0);

		assertEquals(11, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(8, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(1, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeEndGeneIndex());
		assertEquals(2, crossoverProgressDto.getFirstChromosomeEndGeneIndex());

		LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
				parent, 0);

		assertEquals(11, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(11, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(1, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(2, crossoverProgressDto.getSecondChromosomeEndGeneIndex());
		assertEquals(2, crossoverProgressDto.getFirstChromosomeEndGeneIndex());

		LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
				parent, 0);

		assertEquals(15, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(15, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(3, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(3, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(3, crossoverProgressDto.getSecondChromosomeEndGeneIndex());
		assertEquals(3, crossoverProgressDto.getFirstChromosomeEndGeneIndex());
	}

	@Test
	public void testAdvanceIndexesPositiveOffset() {
		Chromosome child = getMom();
		Chromosome parent = getDad();
		LowestCommonGroupCrossoverProgressDto crossoverProgressDto = new LowestCommonGroupCrossoverProgressDto();

		crossoverProgressDto.setFirstChromosomeSequencePosition(child.getGenes().get(0).size());
		crossoverProgressDto.setSecondChromosomeSequencePosition(parent.getGenes().get(0).size());

		LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
				parent, 1);

		/*
		 * This is 8 instead of 6 because the offset makes the sequence position
		 * advance by the size of the third Gene. This is not how it would
		 * actually work in a 'real' execution of the method but is correct for
		 * testing purposes.
		 */
		assertEquals(8, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(8, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(2, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeEndGeneIndex());
		assertEquals(2, crossoverProgressDto.getFirstChromosomeEndGeneIndex());
	}

	@Test
	public void testAdvanceIndexesNegativeOffset() {
		Chromosome child = getMom();
		Chromosome parent = getDad();
		LowestCommonGroupCrossoverProgressDto crossoverProgressDto = new LowestCommonGroupCrossoverProgressDto();

		crossoverProgressDto.setFirstChromosomeSequencePosition(child.getGenes().get(0).size());
		crossoverProgressDto.setSecondChromosomeSequencePosition(parent.getGenes().get(0).size());

		LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
				parent, -1);

		assertEquals(6, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(8, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(1, crossoverProgressDto.getSecondChromosomeEndGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeEndGeneIndex());
	}

	@Test
	public void testAdvanceIndexesWhenChildReachesEnd() {
		Chromosome child = getMom();
		Chromosome parent = getDad();
		LowestCommonGroupCrossoverProgressDto crossoverProgressDto = new LowestCommonGroupCrossoverProgressDto();
		crossoverProgressDto.advanceFirstChromosomeEndGeneIndexBy(child.getGenes().size());
		crossoverProgressDto.advanceFirstChromosomeSequencePositionBy(child.actualSize());

		assertEquals(child.actualSize().intValue(), crossoverProgressDto
				.getFirstChromosomeSequencePosition());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeEndGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(child.getGenes().size(), crossoverProgressDto.getFirstChromosomeEndGeneIndex());

		LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
				parent, 0);

		// None of the indexes should have advanced
		assertEquals(child.actualSize().intValue(), crossoverProgressDto
				.getFirstChromosomeSequencePosition());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeSequencePosition());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeEndGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(child.getGenes().size(), crossoverProgressDto.getFirstChromosomeEndGeneIndex());
	}

	@Test
	public void testAdvanceIndexesWhenParentReachesEnd() {
		Chromosome child = getMom();
		Chromosome parent = getDad();
		LowestCommonGroupCrossoverProgressDto crossoverProgressDto = new LowestCommonGroupCrossoverProgressDto();
		crossoverProgressDto.advanceSecondChromosomeEndGeneIndexBy(parent.getGenes().size());
		crossoverProgressDto.advanceSecondChromosomeSequencePositionBy(parent.actualSize());

		assertEquals(0, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(parent.actualSize().intValue(), crossoverProgressDto
				.getSecondChromosomeSequencePosition());
		assertEquals(parent.getGenes().size(), crossoverProgressDto
				.getSecondChromosomeEndGeneIndex());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeEndGeneIndex());

		LowestCommonGroupCrossoverAlgorithmHelper.advanceIndexes(crossoverProgressDto, child,
				parent, 0);

		// None of the indexes should have advanced
		assertEquals(0, crossoverProgressDto.getFirstChromosomeSequencePosition());
		assertEquals(parent.actualSize().intValue(), crossoverProgressDto
				.getSecondChromosomeSequencePosition());
		assertEquals(parent.getGenes().size(), crossoverProgressDto
				.getSecondChromosomeEndGeneIndex());
		assertEquals(0, crossoverProgressDto.getSecondChromosomeBeginGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeBeginGeneIndex());
		assertEquals(0, crossoverProgressDto.getFirstChromosomeEndGeneIndex());
	}
}