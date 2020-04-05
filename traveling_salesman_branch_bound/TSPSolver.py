#!/usr/bin/python3

from which_pyqt import PYQT_VER
if PYQT_VER == 'PYQT5':
    from PyQt5.QtCore import QLineF, QPointF
elif PYQT_VER == 'PYQT4':
    from PyQt4.QtCore import QLineF, QPointF
else:
    raise Exception('Unsupported Version of PyQt: {}'.format(PYQT_VER))




import time
import numpy as np
from TSPClasses import *
from heapq import *



class TSPSolver:
    def __init__( self, gui_view ):
        self._scenario = None

    def setupWithScenario( self, scenario ):
        self._scenario = scenario


    ''' <summary>
        This is the entry point for the default solver
        which just finds a valid random tour
        </summary>
        <returns>results array for GUI that contains three ints: cost of solution, time spent to find solution, number of solutions found during search (
not counting initial BSSF estimate)</returns> '''
    def defaultRandomTour( self, start_time, time_allowance=60.0 ):

        results = {}


        start_time = time.time()

        cities = self._scenario.getCities()
        ncities = len(cities)
        foundTour = False
        count = 0
        while not foundTour:
            # create a random permutation
            perm = np.random.permutation( ncities )
            route = []

            # Now build the route using the random permutation
            for i in range( ncities ):
                route.append( cities[ perm[i] ] )

            bssf = TSPSolution(route)
            count += 1

            if bssf.costOfRoute() < np.inf:
                # Found a valid route
                foundTour = True

        results['cost'] = bssf.cost
        results['time'] = time.time() - start_time
        results['count'] = count
        results['soln'] = bssf

       # return results;
        return results


    # O(n^2)
    def greedy( self, start_time, time_allowance=60.0 ):

        results = {}
        start_time = time.time()
        count = 1
        route = [self._scenario._cities[0]]
        visitedCities = [0]
        scenario = self._scenario

        # O(n^2) to make the matrix
        matrix = np.full([len(scenario._cities), len(scenario._cities)], math.inf)
        for city1 in range(len(scenario._cities)):
            for city2 in range(len(scenario._cities)):
                cost = scenario._cities[city1].costTo(scenario._cities[city2])
                if (cost != 0):
                    matrix[city1, city2] = cost

        # O(n^2) to check all the cities for every city
        curFromCity = 0
        for city1 in range(len(scenario._cities)):
            rowMin = math.inf
            rowMinIndex = 0
            for city2 in range(len(scenario._cities)):
                if(matrix[curFromCity, city2] < rowMin and city2 not in visitedCities):
                    rowMin = matrix[curFromCity, city2]
                    rowMinIndex = city2

            if(len(visitedCities) < len(scenario._cities)):
                curFromCity = rowMinIndex
                visitedCities.append(rowMinIndex)
                route.append(scenario._cities[rowMinIndex])

        bssf = TSPSolution(route)

        results['cost'] = bssf.cost
        results['time'] = time.time() - start_time
        results['count'] = count
        results['soln'] = bssf
        return results

    # O(n^2 + (n-1)!*(log(n) + n^3 + n^2)) = O(n^3*(n-1)!)
    def branchAndBound( self, start_time, time_allowance=60.0 ):

        # O(n^2) for greedy
        bssf = self.defaultRandomTour(start_time, time_allowance)
        bssf = bssf['soln']

        results = {}
        start_time = time.time()
        queueMax = 0
        count = 1
        prunedCnt = 0
        states = 0
        queue = []

        # O(n^2)
        curState = LowerBoundState()
        curState.initStarting(self._scenario)

        #O(1)
        heappush(queue, (curState.weight, curState))
        states += 1

        # Worst Case is O((n-1)!)
        while (queue):
            if (time.time() - start_time) > time_allowance:
                prunedCnt += len(queue)
                break
            if (len(queue) > queueMax):
                queueMax = len(queue)

            # O(log(n))
            popped = heappop(queue)
            curState = popped[1]

            if (curState.lowerBound > bssf.cost):
                prunedCnt += 1
                continue

            # Repeat n times
            for toCity in range(curState.matrix.shape[0]):
                if (time.time() - start_time) > time_allowance:
                    prunedCnt += len(queue)
                    break
                if (curState.matrix[curState.cityId, toCity] != math.inf):
                    # O(n^2) - become O(n^3) inside for loop
                    subState = LowerBoundState()
                    subState.initNext(curState, toCity, self._scenario._cities[toCity])
                    states += 1

                    if (subState.lowerBound < bssf.cost):
                        if (len(subState.path) == subState.matrix.shape[0]):
                            route = []
                            # O(n) - Only runs when new complete solution is found. Becomes O(n^2) in loop
                            for i in range(subState.matrix.shape[0]):
                                route.append(self._scenario.getCities()[subState.route[i]])

                            bssf = TSPSolution(route)
                            bssf.setPath(subState.path)
                            count += 1
                        else:
                            # O(log(n))
                            heappush(queue, (subState.weight, subState))
                    else:
                            prunedCnt += 1

        results['cities'] = len(self._scenario._cities)
        results['cost'] = bssf.cost
        results['time'] = time.time() - start_time
        results['count'] = count
        results['queueMax'] = queueMax
        results['prunedCnt'] = prunedCnt
        results['path'] = bssf.path
        results['soln'] = bssf
        results['states'] = states
        return results


 # O(n(n - 1)!)
    def fancy(self, start_time, time_allowance=60.0):
        cities = self._scenario._cities
        length = len(cities)
        bssf = self.default_random_tour(cities)['soln']
        count = 0

        self._range = range(length)
        matrix = self._init_matrix(cities)  # O(n^2)
        rcm, _ = self._get_rcm(matrix)  # O(n^2)

        # O(n^3)
        solution_matrix, covered_rows, covered_cols, n_lines = self._cross_zeros(
            rcm
        )

        current_time = time.time() - start_time

        # O(n^4)
        while n_lines < length and current_time < time_allowance:
            uncovered_min = np.min(solution_matrix)

            for i in self._range:
                for j in self._range:
                    if i not in covered_rows:
                        rcm[i][j] -= uncovered_min

                    if j in covered_cols:
                        rcm[i][j] += uncovered_min

            # O(n^3)
            solution_matrix, covered_rows, covered_cols, n_lines = self._cross_zeros(
                rcm
            )

            current_time = time.time() - start_time

        path = [0]
        minimum = 0
        solution = None

        # O(n(n - 1)!)
        while not solution and current_time < time_allowance:
            # O(n(n - 1)!)
            if self._found_tour(rcm, 0, minimum, path, 1):
                solution = list(map(lambda x: cities[x], path))
            else:
                # O(n^2)
                minimum = self._find_next(rcm, minimum)
                current_time = time.time() - start_time

        if solution:
            bssf = TSPSolution(solution)
            count += 1

        return {
            'cost': bssf.cost_of_route(),
            'time': time.time() - start_time,
            'count': count,
            'soln': bssf
        }

    # O(n^2), nested for loops
    def _init_matrix(self, cities):
        length = len(cities)
        matrix = np.full((length, length), np.inf)

        for i in range(length):
            for j in range(length):
                if i != j:
                    # set cost from each city to each other city
                    matrix[i][j] = cities[i].cost_to(cities[j])

        return matrix

    # O(n^2), nested for loops
    def _get_rcm(self, matrix, lower_bound=0):
        # O(n^2)
        for row in range(matrix.shape[0]):
            mini = min(matrix[row, :])

            if mini != np.inf:
                lower_bound += mini
                matrix[row, :] -= mini

        # O(n^2)
        for col in range(matrix.shape[1]):
            mini = min(matrix[:, col])

            if mini != np.inf:
                lower_bound += mini
                matrix[:, col] -= mini

        return matrix, lower_bound

    # This returns:
    #  a solution matrix with the min number of lines used to cross out the zeros
    #  A list of the indices of the covered rows
    #  A list of the indices of the covered col
    # O(n^3) because it crosses out up to n-lines within O(n^2) loops.
    def _cross_zeros(self, rcm):
        solution_matrix = rcm.copy()
        covered_rows = []
        covered_cols = []

        while np.isin(0, solution_matrix):
            # Checking rows
            for i in self._range:
                zero_count = 0
                zero_index = 0

                for j in self._range:
                    if solution_matrix[i][j] == 0:
                        zero_count += 1
                        zero_index = j

                if zero_count == 1:
                    solution_matrix[:, zero_index] = np.inf
                    covered_cols.append(zero_index)

            # Checking cols
            for j in self._range:
                zero_count = 0
                zero_index = 0

                for i in self._range:
                    if solution_matrix[i][j] == 0:
                        zero_count += 1
                        zero_index = i

                if zero_count == 1:
                    solution_matrix[zero_index, :] = np.inf
                    covered_rows.append(zero_index)

        n_lines = len(covered_cols) + len(covered_rows)
        return solution_matrix, covered_rows, covered_cols, n_lines

    # Worst case O(n((n - 1)!)), but optimized because it cuts after
    # finding a solution path.
    def _found_tour(self, rcm, row, minimum, path, length):
        sorted_row = list(rcm[row, :])
        sorted_row = enumerate(sorted_row)
        sorted_row = list(map(lambda t: (t[1], t[0]), sorted_row))
        heapify(sorted_row)

        while sorted_row:
            j = heappop(sorted_row)

            if j[0] <= minimum:
                if j[1] == 0 and length == len(self._range):
                    return True
                elif j[1] not in path:
                    path.append(j[1])
                    length += 1

                    if not self._found_tour(rcm, j[1], minimum, path, length):
                        del path[-1]
                        length -= 1
                    else:
                        return True

        return False

    # O(n^2), nested for loops
    def _find_next(self, rcm, minimum):
        low = np.inf

        for i in self._range:
            for j in self._range:
                if low > rcm[i][j] and rcm[i][j] > minimum:
                    low = rcm[i][j]

        return low



