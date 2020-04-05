#!/usr/bin/python3

from which_pyqt import PYQT_VER
if PYQT_VER == 'PYQT5':
    from PyQt5.QtCore import QLineF, QPointF
else:
    raise Exception('Unsupported Version of PyQt: {}'.format(PYQT_VER))

import time



class ConvexHullSolver:
    def __init__( self, display ):
        self.points = None
        self.gui_display = display

    def findRightMostPoint(self, hull):
        rightmost = -10
        index = 0
        for i in range(len(hull)):
            if(hull[i].x() > rightmost):
                rightmost = hull[i].x()
                index = i
        return index

    def findLeftMostPoint(self, hull):
        leftmost = 10
        index = 0
        for i in range(len(hull)):
            if (hull[i].x() < leftmost):
                leftmost = hull[i].x()
                index = i
        return index


    def angleBetween(self, point1, point2):
        return ((point2.y() - point1.y()) / (point2.x() - point1.x()))

    def getUpperTangents(self, leftHull, rightHull, rightmostIndex, leftmostIndex):
        upperTangents = [0,0]
        curUpperLeftTangent = rightmostIndex
        curUpperRightTangent = leftmostIndex
        while(True):
            #Do right top first:
            newUpperRightTangent = self.upperRight(leftHull, rightHull, curUpperLeftTangent, curUpperRightTangent)
            newUpperLeftTangent = self.upperLeft(leftHull, rightHull, curUpperLeftTangent, curUpperRightTangent)
            if (curUpperRightTangent == newUpperRightTangent and curUpperLeftTangent == newUpperLeftTangent):
                break
            curUpperRightTangent = newUpperRightTangent
            curUpperLeftTangent = newUpperLeftTangent
        upperTangents[0] = curUpperRightTangent
        upperTangents[1] = curUpperLeftTangent
        return upperTangents


    def upperRight(self, leftHull, rightHull, rightmostIndex, leftmostIndex):
        i = 0
        curAngle = self.angleBetween(leftHull[rightmostIndex], rightHull[leftmostIndex])
        while(True):
            nextAngle = self.angleBetween(leftHull[rightmostIndex], rightHull[(leftmostIndex+i+1) % len(rightHull)])
            if(nextAngle < curAngle):
                break
            i = i + 1
            curAngle = nextAngle
        return ((leftmostIndex+i))

    def upperLeft(self, leftHull, rightHull, rightmostIndex, leftmostIndex):
        curAngle = self.angleBetween(leftHull[rightmostIndex], rightHull[leftmostIndex])
        i = 0
        localAngle = curAngle
        while (True):
            nextAngle = self.angleBetween(leftHull[((rightmostIndex - i - 1) % len(leftHull))], rightHull[leftmostIndex])
            if (nextAngle > localAngle):
                break
            i = i + 1
            localAngle = nextAngle
        return (rightmostIndex - (i)) % len(leftHull)

    def getLowerTangents(self, leftHull, rightHull, rightmostIndex, leftmostIndex):
        lowerTangents = [0,0]
        curLowerLeftTangent = rightmostIndex
        curLowerRightTangent = leftmostIndex
        while(True):
            #Do right top first:
            newLowerRightTangent = self.lowerRight(leftHull, rightHull, curLowerLeftTangent, curLowerRightTangent)
            newLowerLeftTangent = self.lowerLeft(leftHull, rightHull, curLowerLeftTangent, curLowerRightTangent)
            if (curLowerRightTangent == newLowerRightTangent and curLowerLeftTangent == newLowerLeftTangent):
                break
            curLowerRightTangent = newLowerRightTangent
            curLowerLeftTangent = newLowerLeftTangent
        lowerTangents[0] = curLowerRightTangent
        lowerTangents[1] = curLowerLeftTangent
        return lowerTangents


    def lowerRight(self, leftHull, rightHull, rightmostIndex, leftmostIndex):
        curAngle = self.angleBetween(leftHull[rightmostIndex], rightHull[leftmostIndex])
        i = 0
        while(True):
            nextAngle = self.angleBetween(leftHull[rightmostIndex], rightHull[((leftmostIndex -i - 1) % len(rightHull))])
            if(nextAngle > curAngle):
                break
            i = i + 1
            curAngle = nextAngle
        return ((leftmostIndex - i)) % len(rightHull)

    def lowerLeft(self, leftHull, rightHull, rightmostIndex, leftmostIndex):
        curAngle = self.angleBetween(leftHull[rightmostIndex], rightHull[leftmostIndex])
        i = 0
        while (True):
            nextAngle = self.angleBetween(leftHull[((rightmostIndex + i + 1) % len(leftHull))], rightHull[leftmostIndex])
            if (nextAngle < curAngle):
                break
            i = i + 1
            curAngle = nextAngle
        return ((rightmostIndex + i) % len(leftHull))

    def mergeHulls(self, leftHull, rightHull):
        rightmostIndex = self.findRightMostPoint(leftHull)
        leftmostIndex = self.findLeftMostPoint(rightHull)
        uppers = self.getUpperTangents(leftHull, rightHull, rightmostIndex, leftmostIndex)
        rightUpper = uppers[0]
        leftUpper = uppers[1]
        lowers = self.getLowerTangents(leftHull, rightHull,rightmostIndex, leftmostIndex)
        rightLower = lowers[0]
        leftLower = lowers[1]

        newHullPoints = list()

        for i in range(len(leftHull)):
            newHullPoints.append(leftHull[i])
            if((i % len(leftHull)) == leftUpper):
                for j in range(rightUpper, len(rightHull)+rightUpper):
                    newHullPoints.append(rightHull[j % len(rightHull)])
                    if ((j % len(rightHull)) == rightLower):
                        for k in range(leftLower, len(leftHull)+leftLower):
                            if(k % len(leftHull) == 0):
                                break
                            newHullPoints.append(leftHull[k % len(leftHull)])
                        break
                break

        return newHullPoints

    def convexHullRecurse(self, points):
        if (len(points) == 3):
            hull = [QLineF(points[i], points[(i + 1) % len(points)]) for i in range(len(points))]

            assert (type(hull) == list and type(hull[0]) == QLineF)
            self.gui_display.addLines(hull, (255, 0, 0))
            returnList = [points[i] for i in range(len(points))]
            if(self.angleBetween(returnList[0],returnList[1]) < self.angleBetween(returnList[0],returnList[2])):
                temp = returnList[1]
                returnList[1] = returnList[2]
                returnList[2] = temp
            return returnList
        elif (len(points) == 2):
            hull = [QLineF(points[i], points[(i + 1) % len(points)]) for i in range(len(points)-1)]
            assert (type(hull) == list and type(hull[0]) == QLineF)
            self.gui_display.addLines(hull, (255, 0, 0))
            return [points[i] for i in range(len(points))]

        leftHull = self.convexHullRecurse(points[:(len(points) // 2)])
        rightHull = self.convexHullRecurse(points[(len(points) // 2):])

        mergedHull = self.mergeHulls(leftHull, rightHull)
        return mergedHull


    def compute_hull( self, unsorted_points ):
        assert( type(unsorted_points) == list and type(unsorted_points[0]) == QPointF )

        n = len(unsorted_points)
        print( 'Computing Hull for set of {} points'.format(n) )

        t1 = time.time()
        # xpts = unsorted_points.sort(lambda p: p.x())
        unsorted_points.sort(key=lambda p: p.x())
        t2 = time.time()
        print('Time Elapsed (Sorting): {:3.3f} sec'.format(t2-t1))

        t3 = time.time()

        newHullPoints = self.convexHullRecurse(unsorted_points)
        hull = [QLineF(newHullPoints[i], newHullPoints[(i + 1) % len(newHullPoints)]) for i in range(len(newHullPoints))]

        t4 = time.time()

        if(hull is None):
            hull = [QLineF(unsorted_points[i], unsorted_points[(i + 1) % 3]) for i in range(3)]
            assert (type(hull) == list and type(hull[0]) == QLineF)
            assert (type(hull) == list and type(hull[0]) == QLineF)

        self.gui_display.addLines(hull, (0, 0, 255))


        print('Time Elapsed (Convex Hull): {:3.3f} sec'.format(t4-t3))
        self.gui_display.displayStatusText('Time Elapsed (Convex Hull): {:3.3f} sec'.format(t4-t3))

        # refresh the gui display
        self.gui_display.update()
