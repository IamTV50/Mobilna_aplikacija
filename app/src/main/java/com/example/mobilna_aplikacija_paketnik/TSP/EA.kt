package com.example.mobilna_aplikacija_paketnik.TSP

class EA(private val tsp: TSP, private val maxFes: Int) {
    private val random = RandomUtils()
    private var fes = 0 // Fitness evaluation counter
    private var population = mutableListOf<Tour>()
    private var bestSolution: Tour? = null
    
    fun run(populationSize: Int = 100, crossoverRate: Double = 0.8, mutationRate: Double = 0.1, tournamentSize: Int = 5, eliteCount: Int = 2): Tour {
        random.setSeedFromTime()

        // Initialize population
        initializePopulation(populationSize)
        evaluatePopulation()
        
        while (fes < maxFes) {
            val newPopulation = mutableListOf<Tour>()
            
            // Elitism - preserve best solutions
            val sortedPopulation = population.sortedBy { evaluateTour(it) }
            for (i in 0 until eliteCount) {
                newPopulation.add(sortedPopulation[i].copy())
            }
            
            // Fill rest of population with offspring
            while (newPopulation.size < populationSize) {
                // Tournament selection
                val parent1 = tournamentSelection(tournamentSize)
                val parent2 = tournamentSelection(tournamentSize)
                
                var offspring1 = parent1.copy()
                var offspring2 = parent2.copy()
                
                // PMX Crossover
                if (random.nextDouble() < crossoverRate) {
                    val crossoverResult = pmxCrossover(parent1, parent2)
                    offspring1 = crossoverResult.first
                    offspring2 = crossoverResult.second
                }
                
                // Mutation
                if (random.nextDouble() < mutationRate) {
                    swapMutation(offspring1)
                }
                if (random.nextDouble() < mutationRate) {
                    swapMutation(offspring2)
                }
                
                newPopulation.add(offspring1)
                if (newPopulation.size < populationSize) {
                    newPopulation.add(offspring2)
                }
            }
            
            population = newPopulation
            evaluatePopulation()
        }
        
        return bestSolution ?: population[0]
    }
    
    private fun initializePopulation(size: Int) {
        population.clear()
        repeat(size) {
            val tour = tsp.generateTSPTour(random).shuffled(random.getRandom()) // Use the new method
            population.add(Tour().apply { 
                tour.forEach { city -> addCityToTour(city) }
            })
        }
    }
    
    private fun evaluatePopulation() {
        for (tour in population) {
            val fitness = evaluateTour(tour)
            if (bestSolution == null || fitness < evaluateTour(bestSolution!!)) {
                bestSolution = tour.copy()
            }
        }
    }
    
    private fun evaluateTour(tour: Tour): Double {
        fes++
        var distance = 0.0
        val cities = tour.getGeneratedTour()
        for (i in cities.indices) {
            val city1 = cities[i]
            val city2 = cities[(i + 1) % cities.size]
            distance += tsp.calculateDistance(city1, city2) * 100.0
        }
        return distance
    }
    
    private fun tournamentSelection(tournamentSize: Int): Tour {
        var best = population[random.nextInt(population.size)]
        var bestFitness = evaluateTour(best)
        
        repeat(tournamentSize - 1) {
            val candidate = population[random.nextInt(population.size)]
            val candidateFitness = evaluateTour(candidate)
            if (candidateFitness < bestFitness) {
                best = candidate
                bestFitness = candidateFitness
            }
        }
        return best
    }
    
    private fun pmxCrossover(parent1: Tour, parent2: Tour): Pair<Tour, Tour> {
        val size = parent1.getGeneratedTour().size
        val point1 = random.nextInt(size) //Random range is empty: [0, 0).
        var point2 = random.nextInt(size)
        while (point1 == point2) {
            point2 = random.nextInt(size)
        }
        
        val start = minOf(point1, point2)
        val end = maxOf(point1, point2)
        
        val offspring1 = Tour()
        val offspring2 = Tour()
        
        // Initialize offsprings with empty cities
        repeat(size) {
            offspring1.addCityToTour(parent1.getGeneratedTour()[0])
            offspring2.addCityToTour(parent2.getGeneratedTour()[0])
        }
        
        // Copy the mapping section
        for (i in start..end) {
            val city1 = parent1.getGeneratedTour()[i]
            val city2 = parent2.getGeneratedTour()[i]
            offspring1.getGeneratedTour()[i] = city2
            offspring2.getGeneratedTour()[i] = city1
        }
        
        // Fill the remaining positions
        fillRemainingCities(parent1, offspring1, start, end)
        fillRemainingCities(parent2, offspring2, start, end)
        
        return Pair(offspring1, offspring2)
    }
    
    private fun fillRemainingCities(parent: Tour, offspring: Tour, start: Int, end: Int) {
        val size = parent.getGeneratedTour().size
        val usedCities = offspring.getGeneratedTour().slice(start..end).toSet()
        
        var j = 0
        for (i in 0 until size) {
            if (i in start..end) continue
            
            while (parent.getGeneratedTour()[j] in usedCities) {
                j++
            }
            offspring.getGeneratedTour()[i] = parent.getGeneratedTour()[j]
            j++
        }
    }
    
    private fun swapMutation(tour: Tour) {
        val size = tour.getGeneratedTour().size
        val pos1 = random.nextInt(size)
        var pos2 = random.nextInt(size)
        while (pos1 == pos2) {
            pos2 = random.nextInt(size)
        }
        
        val cities = tour.getGeneratedTour()
        val temp = cities[pos1]
        cities[pos1] = cities[pos2]
        cities[pos2] = temp
    }
}
