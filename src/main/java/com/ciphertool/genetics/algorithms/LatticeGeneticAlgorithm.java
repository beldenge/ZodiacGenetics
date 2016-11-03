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

package com.ciphertool.genetics.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciphertool.genetics.entities.Chromosome;
import com.ciphertool.genetics.entities.SpatialChromosome;
import com.ciphertool.genetics.population.LatticePopulation;

public class LatticeGeneticAlgorithm extends AbstractGeneticAlgorithm {
	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * A concurrent task for performing a crossover of two parent Chromosomes, producing one child Chromosome.
	 */
	private class CrossoverTask implements Callable<List<SpatialChromosome>> {
		private Chromosome	mom;
		private Chromosome	dad;

		public CrossoverTask(SpatialChromosome mom, SpatialChromosome dad) {
			this.mom = mom;
			this.dad = dad;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<SpatialChromosome> call() throws Exception {
			return crossoverAlgorithm.crossover(mom, dad);
		}
	}

	protected class SelectionTask implements Callable<SelectionResult> {
		int	x;
		int	y;

		public SelectionTask(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public SelectionResult call() throws Exception {
			LatticePopulation latticePopulation = (LatticePopulation) population;
			SpatialChromosome mom = null;
			SpatialChromosome dad = null;
			List<SpatialChromosome> parents = null;

			do {
				parents = latticePopulation.selectIndices(x, y);

				if (parents == null || parents.isEmpty() || parents.size() < 2) {
					throw new IllegalStateException("Unable to produce two parents for crossover");
				}

				mom = parents.get(0);
				mom.setXPos(x);
				mom.setYPos(y);

				dad = parents.get(1);
				dad.setXPos(x);
				dad.setYPos(y);
			}
			/*
			 * The idea is to make sure that individuals which share too much ancestry (i.e. immediate family members)
			 * or not enough ancestry (i.e. different species) cannot reproduce.
			 */
			while (mom == dad || (verifyAncestry && generationCount > generationsToKeep && mom.getAncestry() != null
					&& dad.getAncestry() != null
					&& !mom.getAncestry().sharesLineageWith(dad.getAncestry(), generationsToSkip)));

			return new SelectionResult(mom, dad);
		}
	}

	@Override
	public void select(int initialPopulationSize, List<Chromosome> moms, List<Chromosome> dads)
			throws InterruptedException {
		LatticePopulation latticePopulation = (LatticePopulation) this.population;

		List<FutureTask<SelectionResult>> futureTasks = new ArrayList<FutureTask<SelectionResult>>();
		FutureTask<SelectionResult> futureTask = null;

		/*
		 * Execute each selection concurrently. Each should produce two children, but this is not necessarily always
		 * guaranteed.
		 */
		for (int x = 0; x < latticePopulation.getLatticeRows(); x++) {
			for (int y = 0; y < latticePopulation.getLatticeColumns(); y++) {
				futureTask = new FutureTask<SelectionResult>(new SelectionTask(x, y));
				futureTasks.add(futureTask);
				this.taskExecutor.execute(futureTask);
			}
		}

		// Add the result of each FutureTask to the Lists of Chromosomes selected for subsequent crossover
		for (FutureTask<SelectionResult> future : futureTasks) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during concurrent selections");
			}

			try {
				SelectionResult result = future.get();
				moms.add(result.getMom());
				dads.add(result.getDad());
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for SelectionTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for SelectionTask ", ee);
			}
		}
	}

	@Override
	public int crossover(int pairsToCrossover, List<Chromosome> moms, List<Chromosome> dads)
			throws InterruptedException {
		LatticePopulation latticePopulation = (LatticePopulation) this.population;

		if (this.population.size() < 2) {
			log.info("Unable to perform crossover because there is only 1 individual in the population. Returning.");

			return 0;
		}

		log.debug("Pairs to crossover: " + pairsToCrossover);

		List<SpatialChromosome> childrenToAdd = new ArrayList<SpatialChromosome>();

		List<SpatialChromosome> crossoverResults = doConcurrentCrossovers(moms, dads);
		if (crossoverResults != null && !crossoverResults.isEmpty()) {
			childrenToAdd.addAll(crossoverResults);
		}

		if (childrenToAdd == null || childrenToAdd.size() < pairsToCrossover) {
			log.error(((null == childrenToAdd) ? "No" : childrenToAdd.size())
					+ " children produced from concurrent crossover execution.  Expected " + pairsToCrossover
					+ " children.");

			return ((null == childrenToAdd) ? 0 : childrenToAdd.size());
		}

		this.population.clearIndividuals();

		for (SpatialChromosome child : childrenToAdd) {
			if (stopRequested) {
				throw new InterruptedException(
						"Stop requested while adding individuals back to the population after crossover");
			}

			latticePopulation.addIndividual(child);
		}

		return (int) childrenToAdd.size();
	}

	private List<SpatialChromosome> doConcurrentCrossovers(List<Chromosome> moms, List<Chromosome> dads)
			throws InterruptedException {
		if (moms.size() != dads.size()) {
			throw new IllegalStateException(
					"Attempted to perform crossover on the population, but there are not an equal number of moms and dads.  Something is wrong.  Moms: "
							+ moms.size() + ", Dads:  " + dads.size());
		}

		List<FutureTask<List<SpatialChromosome>>> futureTasks = new ArrayList<FutureTask<List<SpatialChromosome>>>();
		FutureTask<List<SpatialChromosome>> futureTask = null;

		SpatialChromosome mom = null;
		SpatialChromosome dad = null;

		/*
		 * Execute each crossover concurrently. Parents should produce two children, but this is not necessarily always
		 * guaranteed.
		 */
		for (int i = 0; i < moms.size(); i++) {
			mom = (SpatialChromosome) moms.get(i);
			dad = (SpatialChromosome) dads.get(i);

			futureTask = new FutureTask<List<SpatialChromosome>>(new CrossoverTask(mom, dad));
			futureTasks.add(futureTask);
			this.taskExecutor.execute(futureTask);
		}

		List<SpatialChromosome> childrenToAdd = new ArrayList<SpatialChromosome>();
		// Add the result of each FutureTask to the population since it represents a new child Chromosome.
		for (FutureTask<List<SpatialChromosome>> future : futureTasks) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during concurrent crossovers");
			}

			try {
				/*
				 * Add children after all crossover operations are completed so that children are not inadvertently
				 * breeding immediately after birth.
				 */
				childrenToAdd.addAll(future.get());
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for CrossoverTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for CrossoverTask ", ee);
			}
		}

		return childrenToAdd;
	}

	@Override
	public int mutate(int initialPopulationSize) throws InterruptedException {
		LatticePopulation latticePopulation = (LatticePopulation) this.population;

		List<FutureTask<Void>> futureTasks = new ArrayList<FutureTask<Void>>();
		FutureTask<Void> futureTask = null;

		mutations.set(0);

		/*
		 * Execute each mutation concurrently.
		 */
		for (int x = 0; x < latticePopulation.getLatticeRows(); x++) {
			for (int y = 0; y < latticePopulation.getLatticeColumns(); y++) {
				futureTask = new FutureTask<Void>(new MutationTask(latticePopulation.getIndividualsAsArray()[x][y]));
				futureTasks.add(futureTask);
				this.taskExecutor.execute(futureTask);
			}
		}

		for (FutureTask<Void> future : futureTasks) {
			if (stopRequested) {
				throw new InterruptedException("Stop requested during mutation");
			}

			try {
				future.get();
			} catch (InterruptedException ie) {
				log.error("Caught InterruptedException while waiting for MutationTask ", ie);
			} catch (ExecutionException ee) {
				log.error("Caught ExecutionException while waiting for MutationTask ", ee);
			}
		}

		return mutations.get();
	}
}
