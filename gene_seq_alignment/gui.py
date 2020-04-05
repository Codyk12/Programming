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
from GeneSequencing import *




class Proj4GUI( QMainWindow ):

    def __init__( self ):
        super(Proj4GUI,self).__init__()

        self.RED_STYLE   = "background-color: rgb(255, 220, 220)"
        self.PLAIN_STYLE = "background-color: rgb(255, 255, 255)"

        self.seqs = self.loadSequencesFromFile()
        self.processed_results = None

        self.initUI()
        self.solver = GeneSequencing()


    def processClicked(self):
        sequences = [ self.seqs[i][2] for i in sorted(self.seqs.keys()) ]
        print(sequences[0],sequences[1])

        self.statusBar.showMessage('Processing...')
        start = time.time()
        self.processed_results = self.solver.align_all( sequences,
                                                        banded=self.banded.isChecked(),
                                                        align_length=int(self.alignLength.text()) )
        end = time.time()
        ns = (end-start)
        nm = math.floor(ns/60.)
        ns = ns - 60.*nm
        if nm > 0:
            self.statusBar.showMessage('Done.  Time taken: {} mins and {:3.3f} seconds.'.format(nm,ns))
        else:
            self.statusBar.showMessage('Done.  Time taken: {:3.3f} seconds.'.format(ns))

        self.fillTable()
        self.processButton.setEnabled(False)
        self.clearButton.setEnabled(True)

    def clearClicked(self):
        #print('Clear Clicked')
        self.processed_results = None
        self.resetTable()
        self.processButton.setEnabled(True)
        self.clearButton.setEnabled(False)

        self.seq1n_lbl.setText( 'Label {}: '.format('I') )
        self.seq1c_lbl.setText( 'Sequence {}: '.format('I') )
        self.seq2c_lbl.setText( 'Sequence {}: '.format('J') )
        self.seq2n_lbl.setText( 'Label {}: '.format('J') )

        self.seq1_name.setText( '{}'.format(' ') )
        self.seq2_name.setText( '{}'.format(' ') )
        self.seq1_chars.setText( '{}'.format(' ') )
        self.seq2_chars.setText( '{}'.format(' ') )

    def resetTable(self):
        for i in range(self.table.rowCount()):
            for j in range(self.table.columnCount()):
                if j >= i:
                    self.table.item(i,j).setText(' ')

    def fillTable(self):
        for i in range(self.table.rowCount()):
            for j in range(i,self.table.columnCount()):
                #if j >= i:
                if self.processed_results:
                    if len(self.processed_results[i][j]) > 0:
                        results = self.processed_results[i][j]
                        self.table.item(i,j).setText('{}'.format(results['align_cost']))

    def cellClicked(self, i, j):
        #print('Cell {},{} clicked!'.format(i,j))
        #print('lbls: {} and {}'.format(self.seqs[i][1],self.seqs[j][1]))

        if self.processed_results and j >= i:
            self.seq1n_lbl.setText( 'Label {}: '.format(i+1) )
            self.seq1c_lbl.setText( 'Sequence {}: '.format(i+1) )
            self.seq2c_lbl.setText( 'Sequence {}: '.format(j+1) )
            self.seq2n_lbl.setText( 'Label {}: '.format(j+1) )

            self.seq1_name.setText( '{}'.format(self.seqs[i][1]) )
            self.seq2_name.setText( '{}'.format(self.seqs[j][1]) )
            print(i,j)
            results = self.processed_results[i][j]
            self.seq1_chars.setText( '{}'.format(results['seqi_first100']) )
            self.seq2_chars.setText( '{}'.format(results['seqj_first100']) )


    def loadSequencesFromFile( self ):
        FILENAME = 'genomes.txt'
        raw = open(FILENAME,'r').readlines()
        #print(raw[0:3])
        #print(raw[-1])
        sequences = {}
        
        i = 0
        #state   = 'id'
        cur_id  = ''
        cur_str = ''
        for liner in raw:
            line = liner.strip()
            #if state == 'id':
            if '#' in line:
                if len(cur_id) > 0:
                    if '|' in cur_id:
                        cur_id = cur_id.split('|')[-1]
                    sequences[i] = (i,cur_id,cur_str)
                    #print('HERE!')
                    cur_id  = ''
                    cur_str = ''
                    i += 1
                parts = line.split('#')
                cur_id = parts[0]
                cur_str += parts[1]
                #state ='str'
            else:
                #if state == 'str':
                cur_str += line
                #elif state == 'id':
                    #cur_id += line

        if len(cur_str) > 0 or len(cur_id) > 0:
            if '|' in cur_id:
                cur_id = cur_id.split('|')[-1]
            sequences[i] = (i,cur_id,cur_str)

        #print(len(sequences.keys()))
        #print(sequences.keys())

        return sequences



    def getTableDims( self ):
        #w = self.table.verticalHeader().width()+2
        w = self.table.columnWidth(self.table.rowCount()-1) - 4
        #print('VERTICAL WIDTH={}'.format(w))
        #w += self.table.horizontalHeader().width()
        for i in range(self.table.columnCount()):
            #print('COLUMN[{}].width={}'.format(i,self.table.columnWidth(i)))
            w += self.table.columnWidth(i)
        h = self.table.horizontalHeader().height() + 1
        for i in range(self.table.rowCount()):
            h += self.table.rowHeight(i)
        #print ('WIDTH={}, HEIGHT={}'.format(w,h))
        return (w,h)

    def initUI( self ):
        self.setWindowTitle('Gene Sequence Alignment')
        self.setWindowIcon( QIcon('icon312.png') )

        self.statusBar = QStatusBar()
        self.setStatusBar( self.statusBar )

        vbox = QVBoxLayout()
        boxwidget = QWidget()
        boxwidget.setLayout(vbox)
        self.setCentralWidget( boxwidget )

        self.table = QTableWidget(self)
        NSEQ = 10
        self.table.setRowCount(NSEQ)
        self.table.setColumnCount(NSEQ)

        headers = [ 'seq{}'.format(a+1) for a in range(NSEQ) ]
        self.table.setHorizontalHeaderLabels(headers)
        self.table.setVerticalHeaderLabels(headers)
        self.table.setHorizontalScrollBarPolicy(Qt.ScrollBarAlwaysOff)
        self.table.setVerticalScrollBarPolicy(Qt.ScrollBarAlwaysOff)
        #self.table.resizeRowsToContents()
        # Initialize everthing to ""

        #print(self.table.columnWidth(0))
        for i in range(NSEQ):
            for j in range(NSEQ):
                qitem = QTableWidgetItem(" ")
                #qitem.setEnabled(False)
                qitem.setFlags( Qt.ItemIsSelectable |  Qt.ItemIsEnabled )
                if j < i:
                    qitem.setBackground(QColor(200,200,200))
                    qitem.setFlags( Qt.ItemIsSelectable )
                self.table.setItem(i,j,qitem)
        for i in range(NSEQ):
            self.table.resizeColumnToContents(i)
        for j in range(NSEQ):
            self.table.resizeRowToContents(i)
        #print(self.table.columnWidth(0))

        w,h = self.getTableDims()
        #w,h = self.getTableDims()
        #self.table.setTableWidth()
        #self.table.resizeRowsToContents()
        self.table.setFixedWidth(w)
        self.table.setFixedHeight(h)
        #self.table.setMaximumWidth(w)
        #self.table.setMaximumSize(self.getTableDims())

        self.processButton  = QPushButton('Process')
        #print(self.processButton.styleSheet())
        #self.processButton.setStyleSheet("background-color:rgb(200,255,200)")
        self.clearButton    = QPushButton('Clear')

        self.banded     = QCheckBox('Banded')
        self.banded.setChecked(False)
        self.alignLength      = QLineEdit('1000')
        '''self.arrayTime      = QLineEdit('')
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
        self.size           = QLineEdit('7')
        self.sourceNode     = QLineEdit('')
        self.targetNode     = QLineEdit('')
        self.totalCost      = QLineEdit('0.0')
'''
        self.seq1_name     = QLineEdit('')
        self.seq1_name.setFixedWidth(500)
        self.seq1_name.setEnabled(False)
        self.seq1_chars     = QLineEdit('')
        self.seq1_chars.setFixedWidth(850)
        self.seq1_chars.setEnabled(False)
        self.seq2_chars     = QLineEdit('')
        self.seq2_chars.setFixedWidth(850)
        self.seq2_chars.setEnabled(False)
        self.seq2_name     = QLineEdit('')
        self.seq2_name.setFixedWidth(500)
        self.seq2_name.setEnabled(False)

        h = QHBoxLayout()
        h.addStretch(1)
        h.addWidget( self.table )
        h.addStretch(1)
        vbox.addLayout(h)

        h = QHBoxLayout()
        vleft  = QVBoxLayout()
        vright = QVBoxLayout()
        #h.addStretch(1)
        self.seq1n_lbl = QLabel('Label I: ')
        vleft.addWidget( self.seq1n_lbl )
        vright.addWidget( self.seq1_name )
        #h.addStretch(1)

        #h.addStretch(1)
        self.seq1c_lbl = QLabel('Sequence I: ')
        vleft.addWidget( self.seq1c_lbl )
        vright.addWidget( self.seq1_chars )
        #h.addStretch(1)

        #h.addStretch(1)
        self.seq2c_lbl = QLabel('Sequence J: ')
        vleft.addWidget( self.seq2c_lbl )
        vright.addWidget( self.seq2_chars )
        #h.addStretch(1)

        #h.addStretch(1)
        self.seq2n_lbl = QLabel('Label J: ')
        vleft.addWidget( self.seq2n_lbl )
        vright.addWidget( self.seq2_name )
        #h.addStretch(1)

        h.addLayout(vleft)
        h.addLayout(vright)
        vbox.addLayout(h)


        #h.addWidget( self.clearButton )
        
        h = QHBoxLayout()
        h.addStretch(1)
        h.addWidget( self.processButton )
        h.addWidget( self.clearButton )
        h.addStretch(1)
        vbox.addLayout(h)

        h = QHBoxLayout()
        h.addStretch(1)
        h.addWidget( self.banded )
        h.addWidget( QLabel('Align Length: ') )
        h.addWidget( self.alignLength )
        h.addStretch(1)
        vbox.addLayout(h)


        '''
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

        self.graphReady = False
        self.checkPathInputs()
        '''
        self.processButton.clicked.connect(self.processClicked)
        self.clearButton.clicked.connect(self.clearClicked)
        self.clearButton.setEnabled(False)
        self.table.cellClicked.connect(self.cellClicked)

        self.show()




if __name__ == '__main__':
    # This line allows CNTL-C in the terminal to kill the program
    signal.signal(signal.SIGINT, signal.SIG_DFL)
    
    app = QApplication(sys.argv)
    w = Proj4GUI()
    sys.exit(app.exec())
