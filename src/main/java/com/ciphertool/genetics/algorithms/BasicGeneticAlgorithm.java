/**
 * Copyright 2012 George Belden
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

package com.ciphertool.genetics.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.ciphertool.genetics.GeneticAlgorithmStrategy;
import com.ciphertool.genetics.Population;
import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.util.FitnessComparator;
import com.ciphertool.genetics.util.FitnessEvaluator;

public class BasicGeneticAlgorithm implements GeneticAlgorithm {
	private Logger log = Logger.getLogger(getClass());
	protected GeneticAlgorithmStrategy strategy;
	private Integer finalSurvivorCount;
	protected Population population;
	protected CrossoverAlgorithm crossoverAlgorithm;
	protected MutationAlgorithm mutationAlgorithm;
	private FitnessEvaluator fitnessEvaluator;
	private FitnessComparator fitnessComparator;
	private boolean stopRequested;

	public BasicGeneticAlgorithm() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#iterateUntilTermination
	 * ()
	 */
	@Override
	public void iterateUntilTermination() {
		List<String> validationErrors = validateParameters();
		if (validationErrors.size() > 0) {
			log.warn("Unable to execute genetic algorithm because one or more of the required parameters are missing.  The fields that failed validation are "
					+ validationErrors);

			return;
		}

		stopRequested = false;

		if (this.population == null) {
			log.info("Attempted to start algorithm with a null population.  Spawning population of size "
					+ strategy.getPopulationSize() + ".");

			this.spawnInitialPopulation();
		}

		long genesis = System.currentTimeMillis();
		long generationStart = 0;
		int i = 0;
		do {
			i++;
			generationStart = System.currentTimeMillis();

			log.info("Starting generation " + i);

			/*
			 * Doing the select first improves performance by a ratio of up to
			 * (1 - survivalRate). It makes more sense as well since only
			 * survivors can reproduce.
			 */
			select();

			crossover();

			mutate();

			population.populateIndividuals(strategy.getPopulationSize());

			population.evaluateFitness();

			log.info("Generation " + i + " finished in "
					+ (System.currentTimeMillis() - generationStart) + "ms.");
		} while (!stopRequested
				&& (strategy.getMaxGenerations() < 0 || i < strategy.getMaxGenerations()));

		log.info("Average generation time is " + ((System.currentTimeMillis() - genesis) / i)
				+ "ms.");
	}

	private List<String> validateParameters() {
		List<String> validationErrors = new ArrayList<String>();

		if (strategy.getGeneticStructure() == null) {
			validationErrors.add("geneticStructure");
		}

		if (strategy.getPopulationSize() == 0) {
			validationErrors.add("populationSize");
		}

		if (strategy.getMaxGenerations() == 0) {
			validationErrors.add("maxGenerations");
		}

		if (strategy.getSurvivalRate() == 0) {
			validationErrors.add("survivalRate");
		}

		if (strategy.getMutationRate() == 0) {
			validationErrors.add("mutationRate");
		}

		if (strategy.getCrossoverRate() == 0) {
			validationErrors.add("crossoverRate");
		}

		if (strategy.getFitnessEvaluator() == null) {
			validationErrors.add("fitnessEvaluator");
		}

		if (strategy.getCrossoverAlgorithm() == null) {
			validationErrors.add("crossoverAlgorithm");
		}

		return validationErrors;
	}

	@Override
	public List<Chromosome> getBestFitIndividuals() {
		List<Chromosome> individuals = this.population.getIndividuals();
		Collections.sort(individuals, fitnessComparator);

		List<Chromosome> bestFitIndividuals = new ArrayList<Chromosome>();
		int chromosomeIndex = (individuals.size() - finalSurvivorCount);

		for (int finalSurvivorIndex = ((chromosomeIndex < 0) ? 0 : chromosomeIndex); finalSurvivorIndex < individuals
				.size(); finalSurvivorIndex++) {
			bestFitIndividuals.add(individuals.get(finalSurvivorIndex));
		}

		return bestFitIndividuals;
	}

	@Override
	public void select() {
		List<Chromosome> individuals = this.population.getIndividuals();

		Collections.sort(individuals, fitnessComparator);

		int initialPopulationSize = this.population.size();

		long survivorIndex = Math.round((initialPopulationSize * (1 - strategy.getSurvivalRate())));
		log.debug(survivorIndex + " individuals to be removed from population of size "
				+ this.population.size() + " and survival rate of " + strategy.getSurvivalRate()
				+ ".");

		for (int i = 0; i < survivorIndex; i++) {
			/*
			 * We must remove the first element every time, since the List is
			 * sorted in ascending order.
			 */
			this.population.removeIndividual(0);
		}
	}

	/*
	 * Crossover algorithm utilizing Roulette Wheel Selection
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#crossover()
	 */
	@Override
	public void crossover() {
		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return;
		}

		Chromosome mom = null;
		Chromosome dad = null;
		Chromosome child1 = null;
		Chromosome child2 = null;
		int momIndex = -1;
		int dadIndex = -1;

		int initialPopulationSize = this.population.size();

		long pairsToCrossover = Math
				.round((initialPopulationSize * strategy.getCrossoverRate()) / 2);

		log.debug("Pairs to crossover: " + pairsToCrossover);

		for (int i = 0; i < pairsToCrossover; i++) {
			momIndex = this.population.spinIndexRouletteWheel();
			mom = this.population.getIndividuals().get(momIndex);

			/*
			 * Keep retrying until we find a different Chromosome.
			 */
			do {
				dadIndex = this.population.spinIndexRouletteWheel();
				dad = this.population.getIndividuals().get(dadIndex);
			} while (mom == dad);

			child1 = crossoverAlgorithm.crossover(mom, dad);

			child2 = crossoverAlgorithm.crossover(dad, mom);

			/*
			 * Remove the parents from the population and add the children since
			 * they are guaranteed to be at least as fit as their parents
			 */
			this.population.removeIndividual(momIndex);
			if (dadIndex > momIndex) {
				/*
				 * We have to decrease the index for dad since it was shifted
				 * left after mom was removed.
				 */
				dadIndex--;
			}
			this.population.removeIndividual(dadIndex);

			this.population.addIndividual(child1);
			this.population.addIndividual(child2);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#mutate()
	 */
	@Override
	public void mutate() {
		int mutantIndex = -1;

		int initialPopulationSize = this.population.size();

		long mutations = Math.round((initialPopulationSize * strategy.getMutationRate()));

		log.debug("Mutations to perform: " + mutations);

		for (int i = 0; i < mutations; i++) {
			/*
			 * Mutate a gene within a Chromosome
			 */
			mutantIndex = this.population.spinIndexRouletteWheel();
			Chromosome original = this.population.getIndividuals().get(mutantIndex);

			Chromosome mutation = original.clone();

			mutationAlgorithm.mutateChromosome(mutation);

			fitnessEvaluator.evaluate(mutation);

			if (mutation.getFitness() > original.getFitness()) {
				this.population.removeIndividual(mutantIndex);
				this.population.addIndividual(mutation);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ciphertool.zodiacengine.genetic.GeneticAlgorithm#spawnInitialPopulation
	 * ()
	 */
	@Override
	public void spawnInitialPopulation() {
		this.population.setIndividuals(new ArrayList<Chromosome>());

		this.population.populateIndividuals(strategy.getPopulationSize());

		this.population.evaluateFitness();
	}

	@Override
	public void requestStop() {
		this.stopRequested = true;
	}

	/**
	 * @return the population
	 */
	public Population getPopulation() {
		return population;
	}

	/**
	 * @param population
	 *            the population to set
	 */
	@Required
	public void setPopulation(Population population) {
		this.population = population;
	}

	/**
	 * @return the strategy
	 */
	public GeneticAlgorithmStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @param crossoverAlgorithm
	 *            the crossoverAlgorithm to set
	 */
	@Required
	public void setCrossoverAlgorithm(CrossoverAlgorithm crossoverAlgorithm) {
		this.crossoverAlgorithm = crossoverAlgorithm;
	}

	/**
	 * @param mutationAlgorithm
	 *            the mutationAlgorithm to set
	 */
	@Required
	public void setMutationAlgorithm(MutationAlgorithm mutationAlgorithm) {
		this.mutationAlgorithm = mutationAlgorithm;
	}

	/**
	 * @param fitnessEvaluator
	 *            the fitnessEvaluator to set
	 */
	@Required
	public void setFitnessEvaluator(FitnessEvaluator fitnessEvaluator) {
		this.fitnessEvaluator = fitnessEvaluator;
	}

	/**
	 * @param fitnessComparator
	 *            the fitnessComparator to set
	 */
	@Required
	public void setFitnessComparator(FitnessComparator fitnessComparator) {
		this.fitnessComparator = fitnessComparator;
	}

	/**
	 * @param finalSurvivorCount
	 *            the finalSurvivorCount to set
	 */
	@Required
	public void setFinalSurvivorCount(Integer finalSurvivorCount) {
		this.finalSurvivorCount = finalSurvivorCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ciphertool.genetics.algorithms.GeneticAlgorithm#setParameters(int,
	 * int, double, double, double)
	 */
	@Override
	public void setStrategy(GeneticAlgorithmStrategy geneticAlgorithmStrategy) {
		this.fitnessEvaluator = geneticAlgorithmStrategy.getFitnessEvaluator();

		this.population.setGeneticStructure(geneticAlgorithmStrategy.getGeneticStructure());
		this.population.setFitnessEvaluator(geneticAlgorithmStrategy.getFitnessEvaluator());

		this.crossoverAlgorithm = geneticAlgorithmStrategy.getCrossoverAlgorithm();

		this.mutationAlgorithm = geneticAlgorithmStrategy.getMutationAlgorithm();

		this.strategy = geneticAlgorithmStrategy;
	}
}
