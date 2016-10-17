/**
 * Copyright 2015 George Belden
 * 
 * This file is part of Genie.
 * 
 * Genie is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Genie is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Genie. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ciphertool.genetics.algorithms.crossover.cipherkey;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.algorithms.crossover.EvaluatedCrossoverAlgorithm;
import com.ciphertool.genetics.algorithms.mutation.MutationAlgorithm;
import com.ciphertool.genetics.entities.Ancestry;
import com.ciphertool.genetics.entities.KeyedChromosome;
import com.ciphertool.genetics.fitness.FitnessEvaluator;
import com.ciphertool.genetics.util.Coin;

public class EqualOpportunityGuaranteedCrossoverAlgorithm implements
		EvaluatedCrossoverAlgorithm<KeyedChromosome<Object>> {
	private Logger										log						= LoggerFactory.getLogger(getClass());

	private int											maxAttempts;

	private FitnessEvaluator							fitnessEvaluator;
	private MutationAlgorithm<KeyedChromosome<Object>>	mutationAlgorithm;
	private boolean										mutateDuringCrossover	= false;
	private int											maxGenerations;

	private Coin										coin;

	@Override
	public List<KeyedChromosome<Object>> crossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		if (mutateDuringCrossover && mutationAlgorithm == null) {
			throw new IllegalStateException(
					"Unable to perform crossover because the flag to mutate during crossover is set to true, but the MutationAlgorithm is null.");
		}

		List<KeyedChromosome<Object>> children = new ArrayList<KeyedChromosome<Object>>();

		KeyedChromosome<Object> child = performCrossover(parentA, parentB);

		// The Chromosome could be null if it's identical to one of its parents
		if (child != null) {
			children.add(child);
			child.setAncestry(new Ancestry(parentA.getId(), parentB.getId(), parentA.getAncestry(),
					parentB.getAncestry(), maxGenerations));
			parentA.increaseNumberOfChildren();
			parentB.increaseNumberOfChildren();
		}

		return children;
	}

	@SuppressWarnings("unchecked")
	protected KeyedChromosome<Object> performCrossover(KeyedChromosome<Object> parentA, KeyedChromosome<Object> parentB) {
		KeyedChromosome<Object> child;
		double originalFitness = parentA.getFitness();
		int attempts = 0;

		do {
			attempts++;
			child = (KeyedChromosome<Object>) parentA.clone();

			for (Object key : parentA.getGenes().keySet()) {
				if (coin.flip()) {
					child.replaceGene(key, parentB.getGenes().get(key).clone());
				}
			}

			if (attempts >= maxAttempts) {
				// revert crossover
				child = (KeyedChromosome<Object>) parentA.clone();

				log.debug("Unable to find guaranteed better fitness via crossover after " + maxAttempts
						+ " attempts.  Returning clone of first parent.");

				break;
			}
		} while (fitnessEvaluator.evaluate(child) < originalFitness);

		if (mutateDuringCrossover) {
			mutationAlgorithm.mutateChromosome(child);
		}

		return child;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Override
	public void setMutationAlgorithm(MutationAlgorithm<KeyedChromosome<Object>> mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @param mutateDuringCrossover
	 *            the mutateDuringCrossover to set
	 */
	@Override
	public void setMutateDuringCrossover(boolean mutateDuringCrossover) {
		this.mutateDuringCrossover = mutateDuringCrossover;
	}

	/**
	 * @param coin
	 *            the coin to set
	 */
	@Required
	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	@Override
	public String getDisplayName() {
		return "Equal Opportunity Guaranteed";
	}

	/**
	 * @param maxGenerations
	 *            the maxGenerations to set
	 */
	@Required
	public void setMaxGenerations(int maxGenerations) {
		this.maxGenerations = maxGenerations;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	@Required
	@Override
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	@Override
	public int numberOfOffspring() {
		return 1;
	}

	/**
	 * @param maxAttempts
	 *            the maxAttempts to set
	 */
	@Required
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
}
