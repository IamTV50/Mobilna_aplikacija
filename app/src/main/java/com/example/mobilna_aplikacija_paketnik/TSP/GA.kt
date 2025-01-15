package com.example.mobilna_aplikacija_paketnik.TSP

class GA(
    private val popSize: Int,
    private val cr: Double, // crossover probability
    private val pm: Double,  // mutation probability
    private val random: RandomUtils
) {
    private var population = mutableListOf<Tour>()
    private var offspring = mutableListOf<Tour>()

    fun execute(problem: TSP): Tour {
        population.clear()
        offspring.clear()
        var best: Tour? = null

        // Initialize population
        repeat(popSize) {
            val newTour = problem.generateTour()
            problem.evaluate(newTour)
            population.add(newTour)
            if (best == null || newTour.getDistance() < best!!.getDistance()) {
                best = newTour.clone()
            }
        }

        while (problem.getNumberOfEvaluations() < problem.getMaxEvaluations()) {
            offspring.clear()
            
            // Elitism - add best solution to offspring
            best?.let { offspring.add(it.clone()) }

            // Generate offspring
            while (offspring.size < popSize) {
                val parent1 = tournamentSelection()
                var parent2 = tournamentSelection()
                while (parent1 == parent2) {
                    parent2 = tournamentSelection()
                }

                if (random.nextDouble() < cr) {
                    val children = pmx(parent1, parent2)
                    offspring.add(children[0])
                    if (offspring.size < popSize) {
                        offspring.add(children[1])
                    }
                } else {
                    offspring.add(parent1.clone())
                    if (offspring.size < popSize) {
                        offspring.add(parent2.clone())
                    }
                }
            }

            // Apply mutation
            for (tour in offspring) {
                if (random.nextDouble() < pm) {
                    swapMutation(tour)
                }
            }

            // Evaluate offspring and update best
            for (tour in offspring) {
                problem.evaluate(tour)
                if (tour.getDistance() < best!!.getDistance()) {
                    best = tour.clone()
                }
            }

            // Update population
            population = offspring.toMutableList()
        }

        return best!!
    }

    private fun tournamentSelection(): Tour {
        val idx1 = random.nextInt(population.size)
        var idx2 = random.nextInt(population.size)
        while (idx1 == idx2) {
            idx2 = random.nextInt(population.size)
        }
        
        return if (population[idx1].getDistance() < population[idx2].getDistance()) {
            population[idx1]
        } else {
            population[idx2]
        }
    }

    private fun swapMutation(tour: Tour) {
        val size = tour.getPath().size
        val pos1 = random.nextInt(size)
        var pos2 = random.nextInt(size)
        while (pos1 == pos2) {
            pos2 = random.nextInt(size)
        }
        
        val path = tour.getPath()
        val temp = path[pos1]
        path[pos1] = path[pos2]
        path[pos2] = temp
    }

    private fun pmx(parent1: Tour, parent2: Tour): Array<Tour> {
        val size = parent1.getDimension()
        val cutPoint1 = random.nextInt(size)
        var cutPoint2 = random.nextInt(size)
        while (cutPoint1 == cutPoint2) {
            cutPoint2 = random.nextInt(size)
        }

        val start = minOf(cutPoint1, cutPoint2)
        val end = maxOf(cutPoint1, cutPoint2)

        val offspring1 = Tour(size)
        val offspring2 = Tour(size)

        // Copy the mapping section
        for (i in start..end) {
            offspring1.setCity(i, parent2.getPath()[i])
            offspring2.setCity(i, parent1.getPath()[i])
        }

        // Fill remaining positions
        fillRemaining(parent1, offspring1, start, end)
        fillRemaining(parent2, offspring2, start, end)

        return arrayOf(offspring1, offspring2)
    }

    private fun fillRemaining(parent: Tour, offspring: Tour, start: Int, end: Int) {
        val size = parent.getDimension()
        val used = offspring.getPath().slice(start..end).toSet()

        var parentIdx = 0
        for (i in 0 until size) {
            if (i in start..end) continue

            while (parent.getPath()[parentIdx] in used) {
                parentIdx++
            }
            offspring.setCity(i, parent.getPath()[parentIdx])
            parentIdx++
        }
    }
}
