#!/usr/bin/python3

import math
import random
import signal
import sys
import time


from which_pyqt import PYQT_VER
if PYQT_VER == 'PYQT5':
    from PyQt5.QtWidgets import *
    from PyQt5.QtGui import *
    from PyQt5.QtCore import *
elif PYQT_VER == 'PYQT4':
    from PyQt4.QtGui import *
    from PyQt4.QtCore import *
else:
    raise Exception('Unsupported Version of PyQt: {}'.format(PYQT_VER))


# Import in the code with the actual implementation
from NetworkRoutingSolver import *
from CS312Graph import *


class PointLineView( QWidget ):
    def __init__( self, status_bar, data_range ):
        super(QWidget,self).__init__()
        self.setMinimumSize(600,400)

        self.pointList  = {}
        #self.lineList   = {}
        self.edgeList   = {}
        self.labelList   = {}
        self.status_bar = status_bar
        self.data_range = data_range
        self.start_pt = None
        self.end_pt = None

    def displayStatusText(self, text):
        self.status_bar.showMessage(text)

    def clearPoints(self):
        #print('POINTS CLEARED!')
        self.pointList = {}

    #def clearLines(self):
        #self.lineList = {}
    def clearEdges(self):
        self.edgeList = {}
        self.labelList = {}
        #self.update()

    def addPoints( self, point_list, color ):
        if color in self.pointList:
            self.pointList[color].extend( point_list )
        else:
            self.pointList[color] = point_list

    def setStartLoc( self, point ):
        self.start_pt = point
        self.update()

    def setEndLoc( self, point ):
        self.end_pt = point
        self.update()

    #def addLines( self, line_list, color ):
        #if color in self.lineList:
            #self.lineList[color].extend( line_list )
        #else:
            #self.lineList[color] = line_list

    def addEdge( self, startPt, endPt, label, edgeColor, labelColor=None ):
        if not labelColor:
            labelColor = edgeColor

        assert( type(startPt) == QPointF )
        assert( type(endPt)   == QPointF )
        assert( type(label)   == str )

        #if color in self.edgeList:
            #self.lineList[color].extend( line_list )
        #else:
            #self.lineList[color] = line_list

        #print(self.edgeList)
        edge = QLineF(startPt, endPt)
        if edgeColor in self.edgeList.keys():
            self.edgeList[edgeColor].append( edge )
        else:
            self.edgeList[edgeColor] = [edge]
        #self.update()

        midp = QPointF( (edge.x1()+edge.x2())/2.0, 
                        (edge.y1()+edge.y2())/2.0 )
        if edgeColor in self.labelList.keys():
            self.labelList[edgeColor].append( (midp,label) )
        else:
            self.labelList[edgeColor] = [(midp,label)]




    def paintEvent(self, event):
        #print('Paint!!!')
        painter = QPainter(self)
        painter.setRenderHint(QPainter.Antialiasing,True)

        xr = self.data_range['x']
        yr = self.data_range['y']
        w = self.width()
        h = self.height()
        w2h_desired_ratio = (xr[1]-xr[0])/(yr[1]-yr[0])
        #self.aspect_ratio = WIDTH/HEIGHT
        #print('DESIRED', w2h_desired_ratio )
        #print('CURRENT', w/h )
        if w / h < w2h_desired_ratio:
             #h = w / w2h_desired_ratio
             scale = w / (xr[1]-xr[0])
        else:
             #w = h * w2h_desired_ratio
             scale = h / (yr[1]-yr[0])
        #print('FIXED', w/h )
        #print('FIXED', w/h, 'w={},h={},scale={}'.format(w,h,scale) )

        tform = QTransform()
        tform.translate(self.width()/2.0,self.height()/2.0)
        tform.scale(1.0,-1.0)
        painter.setTransform(tform)

        for color in self.edgeList:
            c = QColor(color[0],color[1],color[2])
            painter.setPen( c )
            for edge in self.edgeList[color]:
                ln = QLineF( scale*edge.x1(), scale*edge.y1(), scale*edge.x2(), scale*edge.y2() )
                painter.drawLine( ln )

        R = 1.0E3
        RECT = QRectF(-R,-R,2.0*R,2.0*R)
        align = QTextOption( Qt.Alignment(Qt.AlignHCenter | Qt.AlignVCenter) )
        for color in self.labelList:
            c = QColor(color[0],color[1],color[2])
            painter.setPen( c )
            for label in self.labelList[color]:
                temp_tform = QTransform()
                temp_tform.translate(self.width()/2.0,self.height()/2.0)
                temp_tform.scale(1.0,-1.0)
                pt = label[0]
                temp_tform.translate(scale*pt.x(),scale*pt.y())
                temp_tform.scale(1.0,-1.0)
                painter.setTransform(temp_tform)
                painter.drawText( RECT, label[1], align )

        painter.setTransform(tform)
        for color in self.pointList:
            c = QColor(color[0],color[1],color[2])
            painter.setPen( c )
            for point in self.pointList[color]:
                #pt = QPointF(w*point.x(), h*point.y())
                pt = QPointF(scale*point.x(), scale*point.y())
                painter.drawEllipse( pt, 1.0, 1.0)

        if self.start_pt:
            painter.setPen( QPen(QColor(0.0,255.0,0.0), 2.0) )
            pt = QPointF( scale*self.start_pt.x() -0.0, \
                          scale*self.start_pt.y() -0.0 )
            painter.drawEllipse( pt, 4.0, 4.0)

        if self.end_pt:
            painter.setPen( QPen(QColor(255.0,0.0,0.0), 2.0) )
            pt = QPointF( scale*self.end_pt.x() -0.0, \
                          scale*self.end_pt.y() -0.0 )
            painter.drawEllipse( pt, 4.0, 4.0)



class Proj3GUI( QMainWindow ):

    def __init__( self ):
        super(Proj3GUI,self).__init__()

        self.RED_STYLE   = "background-color: rgb(255, 220, 220)"
        self.PLAIN_STYLE = "background-color: rgb(255, 255, 255)"

        self.graph = None
        self.initUI()
        self.solver = NetworkRoutingSolver( self.view )
        self.genParams = (None, None)


       
    def newPoints(self):

        #if self.randBySeed.isChecked():
        seed = int(self.randSeed.text())
        random.seed( seed )
        #else: # do by time
            #random.seed( time.time() )

        ptlist = []
        #unique_xvals = {}
        RANGE = self.data_range
        xr = self.data_range['x']
        yr = self.data_range['y']
        npoints = int(self.size.text())
        while len(ptlist) < npoints:
            x = random.uniform(0.0,1.0)
            y = random.uniform(0.0,1.0)
            #if x**2+y**2 <= max_r**2:
            if True:
                xval = xr[0] + (xr[1]-xr[0])*x
                yval = yr[0] + (yr[1]-yr[0])*y
                #if not xval in unique_xvals:
                ptlist.append( QPointF(xval,yval) )
                #print( 'Appended ({},{})'.format(xval,yval) )
                    #unique_xvals[xval] = 1
        return ptlist

    #def clearClicked(self):
        #print('clearClicked')
        #self.view.clearEdges()
        #self.view.update()
        #self.solveButton.setEnabled(True)

    def generateNetwork(self):
        nodes = self.newPoints()
        #print(nodes)
        OUT_DEGREE = 3
        size = len(nodes)
        #print(size)
        edgeList = {}
        for u in range(size):
            edgeList[u] = []
            pt_u = nodes[u]
            chosen = []
            for i in range(OUT_DEGREE):
                v = random.randint(0,size-1)
                while v in chosen or v == u:
                    #print('picking {}.{}'.format(u,i))
                    v = random.randint(0,size-1)
                chosen.append(v)
                pt_v = nodes[v]
                uv_len = math.sqrt( (pt_v.x()-pt_u.x())**2 + \
                                    (pt_v.y()-pt_u.y())**2 )
                edgeList[u].append( (v,100.0*uv_len) )

            edgeList[u] = sorted(edgeList[u], key=lambda n:n[0])

        self.graph = CS312Graph(nodes, edgeList)
        self.genParams = (self.randSeed.text(), self.size.text())
        self.view.clearEdges()
        self.view.clearPoints()
        self.sourceNode.setText('')
        self.targetNode.setText('')

    def generateClicked(self):
        #print('generateClicked')
        if self.graph:
            result = QMessageBox.question(self, \
                         'Overwrite Existing Points?', \
                         'Existing points will be overwritten.  Proceed?',
                         QMessageBox.Yes | QMessageBox.No, QMessageBox.Yes)
            if result == QMessageBox.Yes:
                #self.view.clearPoints()
                #self.view.clearLines()
                self.generateNetwork()
                self.view.addPoints( [x.loc for x in self.graph.getNodes()], (0,0,0) )
                self.view.update()
        else:
            self.generateNetwork()
            self.view.addPoints( [x.loc for x in self.graph.getNodes()], (0,0,0) )
            self.view.update()

        #self.solver.reset()
        #self.solveButton.setEnabled(True)
        self.graphReady = True
        self.checkGenInputs()
        self.checkPathInputs()
 


    def display_paths( self, heap_path, heap_time, array_path, array_time ):
        self.view.clearEdges()
        if heap_path:
            cost = heap_path['cost']
            for start,end,lbl in heap_path['path']:
                self.view.addEdge(startPt=start, endPt=end, label=lbl, edgeColor=(128,128,255))
            self.heapTime.setText('{:.6f}s'.format(heap_time))
            if not array_path:
                self.arrayTime.setText('')
                self.speedup.setText('')
                

        if array_path:
            cost = array_path['cost']
            for start,end,lbl in array_path['path']:
                self.view.addEdge(startPt=start, endPt=end, label=lbl, edgeColor=(128,128,255))
            self.arrayTime.setText('{:.6f}s'.format(array_time))
            if not heap_path:
                self.heapTime.setText('')
                self.speedup.setText('')

        if heap_path and array_path:
            ratio = 1.0*array_time/heap_time
            self.speedup.setText('Heap is {:.3f}x Faster'.format(ratio))

        self.view.update()

    def computeClicked(self):
        self.solver.initializeNetwork(self.graph)
        doArray = False
        doHeap  = False
        if self.useUnsorted.isChecked():
            doArray = True
            heap_path = None
            heap_time = None
        elif self.useHeap.isChecked():
            doHeap = True
            array_path = None
            array_time = None
        else:
            doArray = True
            doHeap = True

        if doArray:
            array_time = self.solver.computeShortestPaths( int(self.sourceNode.text())-1, use_heap=False )
            array_path = self.solver.getShortestPath( int(self.targetNode.text())-1 )
            dist = array_path['cost']
        if doHeap:
            heap_time = self.solver.computeShortestPaths( int(self.sourceNode.text())-1, use_heap=True )
            heap_path = self.solver.getShortestPath( int(self.targetNode.text())-1 )
            dist = heap_path['cost']
        self.display_paths( heap_path, heap_time, array_path, array_time )

        #dist = results['totalDist']
        self.checkPathInputs()
        #self.solveButton.setEnabled(False)
        #self.graphReady = True
        self.checkPathInputs()
        if dist == float('inf'):
            self.totalCost.setText( 'UNREACHABLE' )
        else:
            self.totalCost.setText( '{:.3f}'.format(dist) )


    #def _randbytime(self):
        #self.randSeed.setEnabled(False)
    #def _randbyseed(self):
        #self.randSeed.setEnabled(True)
    def checkGenInputs(self):
        if (self.randSeed.text().isdigit()):
            seed  = self.randSeed.text()
        else:
            return None
        #print( src )
        if (self.size.text().isdigit()):
            size = self.size.text()
        else:
            return None

        if self.graph:
            if self.genParams[0] == seed and self.genParams[1] == size:
                self.generateButton.setEnabled(False)
            elif (seed == '') or (size == ''):
                self.generateButton.setEnabled(False)
            else:
                self.generateButton.setEnabled(True)


    def checkInputValue(self, widget, validrange):
        assert( type(widget) == QLineEdit )
        retval = None
        valid  = False
        try:
            sval = widget.text()
            #print('Found: \"{}\"'.format(sval))
            if sval == '':
                valid = True
            else:
                ival = int(sval)
                #print('INT: \"{}\"'.format(ival))
                if validrange:
                    #print('HASRANGE:{}'.format(validrange))
                    if ival >= validrange[0] and ival <= validrange[1]:
                        #print('INRANGE!!!')
                        retval = ival
                        valid = True
        except:
            #print( 'INVALID: \"{}\"!!!'.format(sval) )
            pass

        if not valid:
            widget.setStyleSheet( self.RED_STYLE )
            #self.statusBar.showMessage('Invalid value!!!')
        else:
            widget.setStyleSheet( '' )

        return '' if retval==None else retval
            
        
    def checkPathInputs(self):
        #print( src )
        if not self.graphReady:
            self.computeCost.setEnabled(False)
            print( self.sourceNode.styleSheet() )
            self.sourceNode.setStyleSheet( '' )
            self.sourceNode.setEnabled(False)
            self.targetNode.setStyleSheet( '' )
            self.targetNode.setEnabled(False)

        else: # HAS GRAPH!!!
            self.sourceNode.setEnabled(True)
            self.targetNode.setEnabled(True)
            self.computeCost.setEnabled(False)

            valid_inds = [1,int(self.genParams[1])]
            points = self.graph.getNodes()

            src  = self.checkInputValue( self.sourceNode, valid_inds )
            if not src == '':
                self.view.setStartLoc( points[src-1].loc )
            else:
                self.view.setStartLoc( None )

            dest = self.checkInputValue( self.targetNode, valid_inds )
            if not dest == '':
                if src == dest:
                    self.targetNode.setStyleSheet( self.RED_STYLE )
                    self.view.setEndLoc( None )
                else:
                    self.view.setEndLoc( points[dest-1].loc )
            else:
                self.view.setEndLoc( None )

            
            if ((not src == self.lastPath[0]) or (not dest == self.lastPath[1])) and \
                (not src == '') and (not dest == '') and (not src == dest):
                self.computeCost.setEnabled(True)

        #self.lastPath = (None,None)
        #self.statusBar.showMessage('')


    def initUI( self ):
        self.setWindowTitle('Network Routing')
        self.setWindowIcon( QIcon('icon312.png') )

        self.statusBar = QStatusBar()
        self.setStatusBar( self.statusBar )

        vbox = QVBoxLayout()
        boxwidget = QWidget()
        boxwidget.setLayout(vbox)
        self.setCentralWidget( boxwidget )


        SCALE = 1.0
        self.data_range     = { 'x':[-2*SCALE,2*SCALE], \
                                'y':[-SCALE,SCALE] }
        self.view           = PointLineView( self.statusBar, \
                                             self.data_range )
        self.generateButton = QPushButton('Generate')
        #self.solveButton    = QPushButton('Solve')
        self.computeCost    = QPushButton('Compute Cost')
        #self.clearButton    = QPushButton('Clear To Points')
        self.useUnsorted    = QRadioButton('Unsorted Array')
        self.useHeap        = QRadioButton('Min Heap')
        self.useBoth        = QRadioButton('Use Both')
        self.arrayTime      = QLineEdit('')
        self.arrayTime.setFixedWidth(120)
        self.arrayTime.setEnabled(False)
        self.heapTime       = QLineEdit('')
        self.heapTime.setFixedWidth(120)
        self.heapTime.setEnabled(False)
        self.speedup        = QLineEdit('')
        self.speedup.setFixedWidth(200)
        self.speedup.setEnabled(False)

        #self.randByTime     = QRadioButton('Random Seed')
        #self.randBySeed     = QRadioButton('Seed')
        self.randSeed       = QLineEdit('42')
        self.size           = QLineEdit('20')
        self.sourceNode     = QLineEdit('1')
        self.targetNode     = QLineEdit('4')
        self.totalCost      = QLineEdit('0.0')

        h = QHBoxLayout()
        h.addWidget( self.view )
        vbox.addLayout(h)

        h = QHBoxLayout()
        h.addWidget( QLabel('Random Seed: ') )
        h.addWidget( self.randSeed )
        h.addWidget( QLabel('Size: ') )
        h.addWidget( self.size )
        h.addWidget( self.generateButton )
        #h.addWidget( self.solveButton )
        h.addStretch(1)
        vbox.addLayout(h)
        #self.solveButton.setEnabled(False)

        #h.addWidget( self.clearButton )
        
        h = QHBoxLayout()
        h.addWidget( QLabel( 'Source Node: ' ) )
        h.addWidget( self.sourceNode )
        h.addWidget( QLabel( 'Target Node: ' ) )
        h.addWidget( self.targetNode )
        h.addWidget( self.computeCost )
        h.addWidget( QLabel( 'Total Path Cost: ' ) )
        h.addWidget( self.totalCost )
        self.totalCost.setEnabled(False)
        h.addStretch(1)
        vbox.addLayout(h)

        h = QHBoxLayout()
        h.addWidget( self.useUnsorted )
        h.addWidget( self.arrayTime )
        h.addWidget( self.useHeap )
        h.addWidget( self.heapTime )
        h.addWidget( self.useBoth )
        h.addWidget( self.speedup )
        self.useHeap.setChecked(True)

        h.addStretch(1)
        vbox.addLayout(h)


        #self.randByTime.clicked.connect(self._randbytime)
        #self.randBySeed.clicked.connect(self._randbyseed)


        #self.randBySeed.setChecked(True)
        #self.generateClicked()

        self.lastPath = (None,None)
        self.computeCost.setEnabled(False)
        self.sourceNode.textChanged.connect(self.checkPathInputs)
        self.targetNode.textChanged.connect(self.checkPathInputs)

        self.randSeed.textChanged.connect(self.checkGenInputs)
        self.size.textChanged.connect(self.checkGenInputs)

        self.generateButton.clicked.connect(self.generateClicked)
        self.computeCost.clicked.connect(self.computeClicked)

        self.graphReady = False
        self.checkPathInputs()

        self.show()




if __name__ == '__main__':
    # This line allows CNTL-C in the terminal to kill the program
    signal.signal(signal.SIGINT, signal.SIG_DFL)
    
    app = QApplication(sys.argv)
    w = Proj3GUI()
    sys.exit(app.exec())
