/**
 * Copyright 2015 George Belden
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

package com.ciphertool.genetics.fitness;

import com.ciphertool.genetics.entities.Chromosome;

public class DescendingFitnessComparator implements FitnessComparator {

	@Override
	public int compare(Chromosome c1, Chromosome c2) {
		if (c1.getFitness() < c2.getFitness()) {
			return 1;
		} else if (c1.getFitness() > c2.getFitness()) {
			return -1;
		}

		return 0;
	}
}
