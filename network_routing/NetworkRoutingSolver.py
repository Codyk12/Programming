#!/usr/bin/python3


from CS312Graph import *
import time
import sys

class NetworkRoutingSolver:
    def __init__( self, display ):
        pass



    def initializeNetwork( self, network ):
        assert( type(network) == CS312Graph )
        self.network = network


    def getShortestPath( self, dest_index ):
        self.dest = dest_index

        path_edges = []
        total_length = 0

        self.cur_node = self.queue.nodes[self.dest]
        while(self.cur_node.node_id != self.source):
            cur_loc = self.cur_node.loc
            prev = self.queue.get_prev_node(self.cur_node)
            if(prev != None):
                prev_loc = prev.loc
            else:
                break
            length = self.queue.get_prev_length(self.cur_node)
            path_edges.append((cur_loc, prev_loc, '{:.0f}'.format(length)))
            self.cur_node = self.queue.get_prev_node(self.cur_node)
            total_length += length

        return {'cost':total_length, 'path':path_edges}



    def computeShortestPaths( self, srcIndex, use_heap=False ):
        self.source = srcIndex


        t1 = time.time()
        if(use_heap):
            self.queue = Heap(self.network, srcIndex)
        else:
            self.queue = Array(self.network, srcIndex)

        while(not self.queue.queue_empty()):
            lowest_node = self.queue.delete_min()
            if(lowest_node == -1):
                break

            if(lowest_node.node_id != 0):
                self.queue.push_neighbors_on_queue(lowest_node)
            neighbors = lowest_node.neighbors
            for i in range(0, len(neighbors)):
                if(self.queue.get_distance_to(neighbors[i].dest) > self.queue.get_distance_to(lowest_node) + neighbors[i].length):
                    self.queue.set_distance_to(neighbors[i].dest, self.queue.get_distance_to(lowest_node) + neighbors[i].length)
                    self.queue.set_prev_node(neighbors[i].dest, lowest_node)
                    self.queue.decrease_key()

        t2 = time.time()

        return (t2-t1)


class Heap:
    def __init__(self, graph, src):
        self.nodes = graph.nodes
        self.queue = list()
        self.queue_size = 0
        self.node_dist = [sys.maxsize-1]*len(self.nodes)
        self.prev_node = [None]*len(self.nodes)
        self.popped_nodes = list()

        self.insert(CS312GraphEdge(self.nodes[src], self.nodes[src], 0))
        self.node_dist[src] = 0
        self.prev_node[src] = self.nodes[src]
        self.push_neighbors_on_queue(self.nodes[src])

        pass

    def push_neighbors_on_queue(self, node):
        for i in range(len(node.neighbors)):
            if(node.neighbors[i].dest.node_id not in self.popped_nodes):
                self.insert(node.neighbors[i])
                self.sift_up(self.queue_size-1)

    def insert(self, edge):
        self.queue.append(edge)
        self.queue_size = self.queue_size + 1

    def sift_up(self, i):
        parent = (i-1) // 2
        while i != 0 and self.node_dist[self.queue[i].dest.node_id] < self.node_dist[self.queue[parent].dest.node_id]:
            temp = self.queue[parent]
            self.queue[parent] = self.queue[i]
            self.queue[i] = temp
            i = parent
            parent = (i - 1) // 2

    def delete_min(self):
        if (self.queue_empty()):
            return -1

        return_node = self.queue[0].dest
        self.queue[0] = self.queue[-1]
        self.queue.pop()
        self.popped_nodes.append(return_node.node_id)
        self.queue_size = self.queue_size - 1
        self.sift_down(0)

        return return_node


    def sift_down(self, i):
        if(not self.queue_empty()):
            while (i * 2) < len(self.queue):
                min_child = self.min_child(i)
                if(min_child == -1):
                    break
                if (self.node_dist[self.queue[i].dest.node_id] > self.node_dist[self.queue[min_child].dest.node_id]):
                    temp = self.queue[i]
                    self.queue[i] = self.queue[min_child]
                    self.queue[min_child] = temp
                i = min_child

    def min_child(self, i):
        left_child = i * 2 + 1
        right_child = i * 2 + 2
        if left_child >= len(self.queue):
            return -1
        elif right_child >= len(self.queue):
            return left_child
        else:
            if self.node_dist[self.queue[left_child].dest.node_id] < self.node_dist[self.queue[right_child].dest.node_id]:
                return left_child
            else:
                return right_child

    def queue_empty(self):
        if (len(self.queue) > 0):
            return False
        else:
            return True

    def get_distance_to(self, node):
        return self.node_dist[node.node_id]

    def get_prev_length(self, node):
        if (self.prev_node[node.node_id] != None):
            return self.node_dist[node.node_id] - self.node_dist[self.prev_node[node.node_id]]
        else:
            return 0

    def get_prev_node(self, node):
        if (self.prev_node[node.node_id] != None):
            return self.nodes[self.prev_node[node.node_id]]

    def set_distance_to(self, node, distance):
        self.node_dist[node.node_id] = distance

    def set_prev_node(self, node, prev_node):
        self.prev_node[node.node_id] = prev_node.node_id

    def decrease_key(self):
        return True


class Array:
    def __init__(self, graph, src):
        self.graph = graph
        self.nodes = graph.getNodes()
        self.node_dist = [sys.maxsize-1]*len(self.nodes)
        self.prev_node = [None]*len(self.nodes)
        self.popped_nodes = list()
        self.queue = list()

        self.queue.append(CS312GraphEdge(self.nodes[src], self.nodes[src], 0))
        self.push_neighbors_on_queue(self.nodes[src])
        self.node_dist[src] = 0
        self.prev_node[src] = self.nodes[src]

    def push_neighbors_on_queue(self, node):
        for i in range(0,len(node.neighbors)):
            if(node.neighbors[i].dest.node_id not in self.popped_nodes):
                self.queue.append(node.neighbors[i])

    def queue_empty(self):
        if(len(self.queue) > 0):
            return False
        else:
            return True

    def delete_min(self):
        temp_highest = sys.maxsize
        node_index = -1
        queue_index = -1
        for i in range(len(self.queue)):
            if(self.node_dist[self.queue[i].dest.node_id] < temp_highest
               and self.queue[i].dest.node_id not in self.popped_nodes):
                temp_highest = self.node_dist[self.queue[i].dest.node_id]
                node_index = self.queue[i].dest.node_id
                queue_index = i

        if(node_index == -1):
            return -1
        else:
            return_node = self.queue[queue_index].dest
            self.queue.pop(queue_index)
            self.popped_nodes.append(return_node.node_id)
            return return_node

    def get_distance_to(self, node):
        return self.node_dist[node.node_id]

    def set_distance_to(self, node, distance):
        self.node_dist[node.node_id] = distance

    def get_prev_node(self, node):
        if (self.prev_node[node.node_id] != None):
            return self.nodes[self.prev_node[node.node_id]]

    def set_prev_node(self, node, prev_node):
        self.prev_node[node.node_id] = prev_node.node_id

    def get_prev_length(self, node):
        if (self.prev_node[node.node_id] != None):
            return self.node_dist[node.node_id] - self.node_dist[self.prev_node[node.node_id]]
        else:
            return 0

    def decrease_key(self):
        return True
