#!/usr/bin/python3


import math
from copy import deepcopy

import numpy as np
import random
import time
import pprint


class TSPSolution:
    def __init__( self, listOfCities):
        self.route = listOfCities
        self.cost = self.costOfRoute()
        self.path = list()

        #print( [c._index for c in listOfCities] )

    def costOfRoute( self ):
        cost = 0
        last = self.route[0]
        for city in self.route[1:]:
            cost += last.costTo(city)
            last = city
        cost += self.route[-1].costTo( self.route[0] )
        return cost

    def setPath(self, path):
        self.path = path

    def enumerateEdges( self ):
        elist = []
        c1 = self.route[0]
        for c2 in self.route[1:]:
            dist = c1.costTo( c2 )
            if dist == np.inf:
                return None
            elist.append( (c1, c2, int(math.ceil(dist))) )
            c1 = c2
        dist = self.route[-1].costTo( self.route[0] )
        if dist == np.inf:
            return None
        elist.append( (self.route[-1], self.route[0], int(math.ceil(dist))) )
        return elist


def nameForInt( num ):
    if num == 0:
        return ''
    elif num <= 26:
        return chr( ord('A')+num-1 )
    else:
        return nameForInt((num-1) // 26 ) + nameForInt((num-1)%26+1)


class Scenario:

    HARD_MODE_FRACTION_TO_REMOVE = 0.20 # Remove 20% of the edges

    def __init__( self, city_locations, difficulty, rand_seed ):
        self._difficulty = difficulty

        if difficulty == "Normal" or difficulty == "Hard":
            self._cities = [City( pt.x(), pt.y(), \
                                  random.uniform(0.0,1.0) \
                                ) for pt in city_locations]
        elif difficulty == "Hard (Deterministic)":
            random.seed( rand_seed )
            self._cities = [City( pt.x(), pt.y(), \
                                  random.uniform(0.0,1.0) \
                                ) for pt in city_locations]
        else:
            self._cities = [City( pt.x(), pt.y() ) for pt in city_locations]


        num = 0
        for city in self._cities:
            #if difficulty == "Hard":
            city.setScenario(self)
            city.setIndexAndName( num, nameForInt( num+1 ) )
            num += 1

        # Assume all edges exists except self-edges
        ncities = len(self._cities)
        self._edge_exists = ( np.ones((ncities,ncities)) - np.diag( np.ones((ncities)) ) ) > 0

        #print( self._edge_exists )
        if difficulty == "Hard":
            self.thinEdges()
        elif difficulty == "Hard (Deterministic)":
            self.thinEdges(deterministic=True)

    def getCities( self ):
        return self._cities


    def randperm( self, n ):
        perm = np.arange(n)
        for i in range(n):
            randind = random.randint(i,n-1)
            save = perm[i]
            perm[i] = perm[randind]
            perm[randind] = save
        return perm

    def thinEdges( self, deterministic=False ):
        ncities = len(self._cities)
        edge_count = ncities*(ncities-1) # can't have self-edge
        num_to_remove = np.floor(self.HARD_MODE_FRACTION_TO_REMOVE*edge_count)

        #edge_exists = ( np.ones((ncities,ncities)) - np.diag( np.ones((ncities)) ) ) > 0
        can_delete  = self._edge_exists.copy()

        # Set aside a route to ensure at least one tour exists
        route_keep = np.random.permutation( ncities )
        if deterministic:
            route_keep = self.randperm( ncities )
        for i in range(ncities):
            can_delete[route_keep[i],route_keep[(i+1)%ncities]] = False

        # Now remove edges until 
        while num_to_remove > 0:
            if deterministic:
                src = random.randint(0,ncities-1)
                dst = random.randint(0,ncities-1)
            else:
                src = np.random.randint(ncities)
                dst = np.random.randint(ncities)
            if self._edge_exists[src,dst] and can_delete[src,dst]:
                self._edge_exists[src,dst] = False
                num_to_remove -= 1

        #print( self._edge_exists )

class City:
    def __init__( self, x, y, elevation=0.0 ):
        self._x = x
        self._y = y
        self._elevation = elevation
        self._scenario  = None
        self._index = -1
        self._name  = None
        self._id = 0

    def setIndexAndName( self, index, name ):
        self._index = index
        self._name = name

    def setScenario( self, scenario ):
        self._scenario = scenario

    def setId( self, num ):
        self._id = num - 1

    ''' <summary>
        How much does it cost to get from this city to the destination?
        Note that this is an asymmetric cost function.
         
        In advanced mode, it returns infinity when there is no connection.
        </summary> '''
    MAP_SCALE = 1000.0
    def costTo( self, other_city ):

        assert( type(other_city) == City )

        # In hard mode, remove edges; this slows down the calculation...
        # Use this in all difficulties, it ensures INF for self-edge
        if not self._scenario._edge_exists[self._index, other_city._index]:
            #print( 'Edge ({},{}) doesn\'t exist'.format(self._index,other_city._index) )
            return np.inf

        # Euclidean Distance
        cost = math.sqrt( (other_city._x - self._x)**2 +
                          (other_city._y - self._y)**2 )

        # For Medium and Hard modes, add in an a symmetric cost (in easy mode it is zero).
        if not self._scenario._difficulty == 'Easy':
            cost += (other_city._elevation - self._elevation)
            if cost < 0.0:
                cost = 0.0
        #cost *= SCALE_FACTOR


        return int(math.ceil(cost * self.MAP_SCALE))

class LowerBoundState:

    def __init__(self):
        self.route = list()
        self.path = list()
        pass

    # O(n^2)
    def initStarting(self, scenario):
        self.cityId = 0
        self.cityName = scenario._cities[0]._name
        self.route.append(0)
        self.path.append(self.cityName)
        # O(n^2)
        matrix = self.makeMatrix(scenario)
        # O(n^2)
        matrix = self.reduceMatrix(matrix, -1)
        self.matrix = matrix[0]
        self.lowerBound = self.computeLowerBound(0, 0, matrix[1])
        self.weight = self.computeWeight()

    # O(n^2)
    def initNext(self, prevLowerBoundState, curCityId, curCity):
        self.cityId = curCityId
        self.cityName = curCity._name

        self.route = deepcopy(prevLowerBoundState.route)
        self.route.append(curCityId)

        self.path = deepcopy(prevLowerBoundState.path)
        self.path.append(self.cityName)

        prevMatrix = prevLowerBoundState.matrix.copy()
        # O(n^2)
        matrix = self.reduceMatrix(prevMatrix, prevLowerBoundState.cityId)
        self.matrix = matrix[0]
        self.lowerBound = self.computeLowerBound(prevLowerBoundState.lowerBound, prevLowerBoundState.matrix[prevLowerBoundState.cityId][self.cityId],  matrix[1])
        self.weight = self.computeWeight()
        pass

    #O(n^2)
    def makeMatrix(self, scenario):
        matrix = np.full([len(scenario._cities), len(scenario._cities)], math.inf)
        for city1 in range(len(scenario._cities)):
            for city2 in range(len(scenario._cities)):
                cost = scenario._cities[city1].costTo(scenario._cities[city2])
                matrix[city1, city2] = cost

        return matrix

    #O(n^2)
    def reduceMatrix(self, matrix, prevCityId):
        #O(n)
        if(prevCityId != -1):
            matrix[:, self.cityId] = math.inf
            matrix[prevCityId, :] = math.inf
            matrix[self.cityId, prevCityId] = math.inf

        reduction = 0

        # O(n^2)
        for row in range(matrix.shape[0]):
            mini = min(matrix[row,:])
            if(mini != math.inf):
                reduction += mini
                matrix[row,:] -= mini
        # O(n^2)
        for col in range(matrix.shape[1]):
            mini = min(matrix[:,col])
            if (mini != math.inf):
                reduction += mini
                matrix[:,col] -= mini
        return (matrix, reduction)

    def computeLowerBound(self, prevLB, prevCost, thisLower):
        return prevLB + prevCost + thisLower

    def computeWeight(self):
        n = self.matrix.shape[0]
        r = len(self.route)
        return (1 + ((n-r)/n)) * self.lowerBound

    def toString(self):
        print(" cityId ", self.cityId)
        print("lowerBound ", self.lowerBound)
        print("route = ", self.route)
        print("weight = ", self.weight)
        print("matrix ")
        print(self.matrix)

    def __lt__(self, other):
        return self.path[-1] < other.path[-1]
