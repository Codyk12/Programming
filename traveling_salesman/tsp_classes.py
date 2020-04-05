#!/usr/local/bin/python3

import math
import random
import numpy as np


class TSPSolution:
    def __init__(self, list_of_cities):
        self.route = list_of_cities
        # print([c.index for c in list_of_cities])

    def cost_of_route(self):
        cost = 0
        last = self.route[0]

        for city in self.route[1:]:
            cost += last.cost_to(city)
            last = city

        cost += self.route[-1].cost_to(self.route[0])
        return cost

    def enumerate_edges(self):
        elist = []
        city1 = self.route[0]

        for city2 in self.route[1:]:
            dist = city1.cost_to(city2)

            if dist == np.inf:
                return None

            elist.append((city1, city2, int(math.ceil(dist))))
            city1 = city2

        dist = self.route[-1].cost_to(self.route[0])

        if dist == np.inf:
            return None

        elist.append((self.route[-1], self.route[0], int(math.ceil(dist))))
        return elist


def name_for_int(num):
    if num == 0:
        return ''
    elif num <= 26:
        return chr(ord('A') + (num - 1))

    return name_for_int((num-1) // 26) + name_for_int((num - 1) % (26 + 1))


class Scenario:

    HARD_MODE_FRACTION_TO_REMOVE = 0.20  # Remove 20% of the edges

    def __init__(self, city_locations, difficulty, rand_seed):
        self.difficulty = difficulty

        if difficulty == "Normal" or difficulty == "Hard":
            self._cities = [
                City(
                    pt.x(), pt.y(),
                    random.uniform(0.0, 1.0)
                ) for pt in city_locations
            ]
        elif difficulty == "Hard (Deterministic)":
            random.seed(rand_seed)

            self._cities = [
                City(
                    pt.x(), pt.y(),
                    random.uniform(0.0, 1.0)
                ) for pt in city_locations
            ]
        else:
            self._cities = [City(pt.x(), pt.y()) for pt in city_locations]

        num = 0

        for city in self._cities:
            # if difficulty == "Hard":
            city.set_scenario(self)
            city.set_index_and_name(num, name_for_int(num + 1))
            num += 1

        # Assume all edges exists except self-edges
        ncities = len(self._cities)

        self.edge_exists = (
            np.ones((ncities, ncities)) -
            np.diag(np.ones((ncities)))
        ) > 0

        # print(self.edge_exists)
        if difficulty == "Hard":
            self.thin_edges()
        elif difficulty == "Hard (Deterministic)":
            self.thin_edges(deterministic=True)

    def get_cities(self):
        return self._cities

    def randperm(self, ncities):
        perm = np.arange(ncities)

        for i in range(ncities):
            randind = random.randint(i, ncities - 1)
            save = perm[i]
            perm[i] = perm[randind]
            perm[randind] = save

        return perm

    def thin_edges(self, deterministic=False):
        ncities = len(self._cities)
        edge_count = ncities * (ncities - 1)  # can't have self-edge

        num_to_remove = np.floor(
            self.HARD_MODE_FRACTION_TO_REMOVE * edge_count
        )

        # edge_exists = (
        #     np.ones((ncities,ncities)) -
        #     np.diag(np.ones((ncities)))
        # ) > 0

        can_delete = self.edge_exists.copy()

        # Set aside a route to ensure at least one tour exists
        route_keep = np.random.permutation(ncities)

        if deterministic:
            route_keep = self.randperm(ncities)
        for i in range(ncities):
            can_delete[route_keep[i], route_keep[(i + 1) % ncities]] = False

        # Now remove edges until
        while num_to_remove > 0:
            if deterministic:
                src = random.randint(0, ncities-1)
                dst = random.randint(0, ncities-1)
            else:
                src = np.random.randint(ncities)
                dst = np.random.randint(ncities)
            if self.edge_exists[src, dst] and can_delete[src, dst]:
                self.edge_exists[src, dst] = False
                num_to_remove -= 1

        # print(self.edge_exists)


class City:
    def __init__(self, x, y, elevation=0.0):
        self.x_coord = x
        self.y_coord = y
        self.elevation = elevation
        self._scenario = None
        self.index = -1
        self.name = None

    def __eq__(self, other):
        return self.name == other.name

    def __lt__(self, other):
        return self.name < other.name

    def __repr__(self):
        return "<City x_coord:{} y_coord:{} elevation:{} index:{} name:{}>".format(
            self.x_coord,
            self.y_coord,
            self.elevation,
            self.index,
            self.name
        )

    def __str__(self):
        return self.name

    def set_index_and_name(self, index, name):
        self.index = index
        self.name = name

    def set_scenario(self, scenario):
        self._scenario = scenario

    MAP_SCALE = 1000.0

    def cost_to(self, other_city):
        '''
        <summary>
            How much does it cost to get from this city to the destination?
            Note that this is an asymmetric cost function.

            In advanced mode, it returns infinity when there is no connection.
        </summary>
        '''

        assert isinstance(other_city, City)

        # In hard mode, remove edges; this slows down the calculation...
        # Use this in all difficulties, it ensures INF for self-edge
        if not self._scenario.edge_exists[self.index, other_city.index]:
            # print('Edge ({},{}) doesn\'t exist'.format(self.index, other_city.index))
            return np.inf

        # Euclidean Distance
        cost = math.sqrt(
            ((other_city.x_coord - self.x_coord) ** 2) +
            ((other_city.y_coord - self.y_coord) ** 2)
        )

        # For Medium and Hard modes, add in an a symmetric cost (in easy mode it is zero).
        if not self._scenario.difficulty == 'Easy':
            cost += (other_city.elevation - self.elevation)

            if cost < 0.0:
                cost = 0.0

        # cost *= SCALE_FACTOR
        return int(math.ceil(cost * self.MAP_SCALE))
