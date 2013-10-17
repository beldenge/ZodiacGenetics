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

package com.ciphertool.genetics.entities.statistics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "generation_stats")
public class GenerationStatistics implements Serializable {
	private static final long serialVersionUID = 5751129649317222013L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@NaturalId
	@ManyToOne
	@JoinColumn(name = "execution_id")
	private ExecutionStatistics executionStatistics;

	@NaturalId
	@Column(name = "generation")
	private int generation;

	@Column(name = "execution_time")
	private long executionTime;

	@Column(name = "best_fitness")
	private double bestFitness;

	@Column(name = "average_fitness")
	private double averageFitness;

	@Column(name = "known_solution_proximity", nullable = true)
	private Double knownSolutionProximity;

	@Transient
	private int numberOfMutations;

	@Transient
	private int numberOfCrossovers;

	@Transient
	private int numberRandomlyGenerated;

	@Transient
	private int numberSelectedOut;

	/**
	 * Default no-args constructor
	 */
	public GenerationStatistics() {
	}

	/**
	 * @param executionStatistics
	 *            the executionStatistics to set
	 * @param generation
	 *            the generation to set
	 */
	public GenerationStatistics(ExecutionStatistics executionStatistics, int generation) {
		this.executionStatistics = executionStatistics;
		this.generation = generation;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the executionStatistics
	 */
	public ExecutionStatistics getExecutionStatistics() {
		return executionStatistics;
	}

	/**
	 * @param executionStatistics
	 *            the executionStatistics to set
	 */
	public void setExecutionStatistics(ExecutionStatistics executionStatistics) {
		this.executionStatistics = executionStatistics;
	}

	/**
	 * @return the generation
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * @param generation
	 *            the generation to set
	 */
	public void setGeneration(int generation) {
		this.generation = generation;
	}

	/**
	 * @return the executionTime
	 */
	public long getExecutionTime() {
		return executionTime;
	}

	/**
	 * @param executionTime
	 *            the executionTime to set
	 */
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	/**
	 * @return the bestFitness
	 */
	public double getBestFitness() {
		return bestFitness;
	}

	/**
	 * @param bestFitness
	 *            the bestFitness to set
	 */
	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}

	/**
	 * @return the averageFitness
	 */
	public double getAverageFitness() {
		return averageFitness;
	}

	/**
	 * @param averageFitness
	 *            the averageFitness to set
	 */
	public void setAverageFitness(double averageFitness) {
		this.averageFitness = averageFitness;
	}

	/**
	 * @return the knownSolutionProximity
	 */
	public Double getKnownSolutionProximity() {
		return knownSolutionProximity;
	}

	/**
	 * @param knownSolutionProximity
	 *            the knownSolutionProximity to set
	 */
	public void setKnownSolutionProximity(Double knownSolutionProximity) {
		this.knownSolutionProximity = knownSolutionProximity;
	}

	/**
	 * @param numberOfMutations
	 *            the numberOfMutations to set
	 */
	public void setNumberOfMutations(int numberOfMutations) {
		this.numberOfMutations = numberOfMutations;
	}

	/**
	 * @param numberOfCrossovers
	 *            the numberOfCrossovers to set
	 */
	public void setNumberOfCrossovers(int numberOfCrossovers) {
		this.numberOfCrossovers = numberOfCrossovers;
	}

	/**
	 * @param numberRandomlyGenerated
	 *            the numberRandomlyGenerated to set
	 */
	public void setNumberRandomlyGenerated(int numberRandomlyGenerated) {
		this.numberRandomlyGenerated = numberRandomlyGenerated;
	}

	/**
	 * @param numberSelectedOut
	 *            the numberSelectedOut to set
	 */
	public void setNumberSelectedOut(int numberSelectedOut) {
		this.numberSelectedOut = numberSelectedOut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(averageFitness);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(bestFitness);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((executionStatistics == null) ? 0 : executionStatistics.hashCode());
		result = prime * result + (int) (executionTime ^ (executionTime >>> 32));
		result = prime * result + generation;
		result = prime * result
				+ ((knownSolutionProximity == null) ? 0 : knownSolutionProximity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GenerationStatistics other = (GenerationStatistics) obj;
		if (Double.doubleToLongBits(averageFitness) != Double
				.doubleToLongBits(other.averageFitness)) {
			return false;
		}
		if (Double.doubleToLongBits(bestFitness) != Double.doubleToLongBits(other.bestFitness)) {
			return false;
		}
		if (executionStatistics == null) {
			if (other.executionStatistics != null) {
				return false;
			}
		} else if (!executionStatistics.equals(other.executionStatistics)) {
			return false;
		}
		if (executionTime != other.executionTime) {
			return false;
		}
		if (generation != other.generation) {
			return false;
		}
		if (knownSolutionProximity == null) {
			if (other.knownSolutionProximity != null) {
				return false;
			}
		} else if (!knownSolutionProximity.equals(other.knownSolutionProximity)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String proximity = (this.knownSolutionProximity == null) ? "" : ", proximityToKnown="
				+ String.format("%1$,.2f", this.knownSolutionProximity) + "%";

		return "[generation=" + generation + ", executionTime=" + executionTime
				+ ", averageFitness=" + String.format("%1$,.2f", averageFitness) + ", bestFitness="
				+ String.format("%1$,.2f", bestFitness) + proximity + ", deaths="
				+ numberSelectedOut + ", crossovers=" + numberOfCrossovers + ", mutations="
				+ numberOfMutations + ", newSpawns=" + numberRandomlyGenerated + "]";
	}
}
