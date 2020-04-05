#!/usr/local/bin/python3

import math
import random
import signal
import sys
import time

from PyQt5.QtCore import QLineF, QPointF, QRectF, Qt

from PyQt5.QtGui import (
    QColor, QFont, QIcon, QPainter,
    QPolygonF, QTextOption, QTransform
)

from PyQt5.QtWidgets import (
    QApplication, QComboBox, QHBoxLayout, QLabel,
    QLineEdit, QMainWindow, QMessageBox, QPushButton,
    QStatusBar, QVBoxLayout, QWidget
)

# Import in the code with the actual implementation
from tsp_classes import Scenario
from tsp_solver import TSPSolver


class PointLineView(QWidget):
    def __init__(self, status_bar, data_range):
        super().__init__()
        self.setMinimumSize(950, 600)

        self.pointList = {}
        # self.lineList   = {}
        self.edgeList = {}
        self.labelList = {}
        self.status_bar = status_bar
        self.data_range = data_range
        self.start_pt = None
        self.end_pt = None

    def displayStatusText(self, text):
        self.status_bar.showMessage(text)

    def clearPoints(self):
        # print('POINTS CLEARED!')
        self.pointList = {}

    # def clearLines(self):
        # self.lineList = {}
    def clearEdges(self):
        self.edgeList = {}
        self.labelList = {}
        # self.update()

    def addPoints(self, point_list, color):
        if color in self.pointList:
            self.pointList[color].extend(point_list)
        else:
            self.pointList[color] = point_list

    def setStartLoc(self, point):
        self.start_pt = point
        self.update()

    def setEndLoc(self, point):
        self.end_pt = point
        self.update()

    # def addLines( self, line_list, color ):
        # if color in self.lineList:
        # self.lineList[color].extend( line_list )
        # else:
        # self.lineList[color] = line_list

    def addEdge(self, startPt, endPt, label, edgeColor, labelColor=None, xoffset=0.0):
        if not labelColor:
            labelColor = edgeColor

        assert isinstance(startPt, QPointF)
        assert isinstance(endPt, QPointF)
        assert isinstance(label, str)

        # if color in self.edgeList:
        # self.lineList[color].extend( line_list )
        # else:
        # self.lineList[color] = line_list

        # print(self.edgeList)
        edge = QLineF(startPt, endPt)
        if edgeColor in self.edgeList.keys():
            self.edgeList[edgeColor].append(edge)
        else:
            self.edgeList[edgeColor] = [edge]
        # self.update()

        midp = QPointF((edge.x1()*0.2 + edge.x2()*0.8),
                       (edge.y1()*0.2 + edge.y2()*0.8))
        self.addLabel(midp, label, labelColor, xoffset=xoffset)

    def addLabel(self, point, label, labelColor, xoffset=0.0):
        if labelColor in self.labelList.keys():
            self.labelList[labelColor].append((point, label, xoffset))
        else:
            self.labelList[labelColor] = [(point, label, xoffset)]

    def paintEvent(self, event):
        # print('Paint!!!')
        painter = QPainter(self)
        painter.setRenderHint(QPainter.Antialiasing, True)

        xr = self.data_range['x']
        yr = self.data_range['y']
        w = self.width()
        h = self.height()
        w2h_desired_ratio = (xr[1]-xr[0])/(yr[1]-yr[0])
        # self.aspect_ratio = WIDTH/HEIGHT
        # print('DESIRED', w2h_desired_ratio )
        # print('CURRENT', w/h )
        if w / h < w2h_desired_ratio:
            # h = w / w2h_desired_ratio
            scale = w / (xr[1]-xr[0])
        else:
            # w = h * w2h_desired_ratio
            scale = h / (yr[1]-yr[0])
        # print('FIXED', w/h )
        # print('FIXED', w/h, 'w={},h={},scale={}'.format(w,h,scale) )

        tform = QTransform()
        tform.translate(self.width()/2.0, self.height()/2.0)
        tform.scale(1.0, -1.0)
        painter.setTransform(tform)

        for color in self.edgeList:
            c = QColor(color[0], color[1], color[2])
            painter.setPen(c)
            for edge in self.edgeList[color]:
                ln = QLineF(scale*edge.x1(), scale*edge.y1(),
                            scale*edge.x2(), scale*edge.y2())
                painter.drawLine(ln)

        for color in self.edgeList:
            c = QColor(color[0], color[1], color[2])
            painter.setPen(c)
            for edge in self.edgeList[color]:
                # arrow_scale = .015
                arrow_scale = 5.0
                unit_edge = (edge.x2() - edge.x1(), edge.y2() - edge.y1())
                unit_edge_mag = math.sqrt(
                    (edge.x2() - edge.x1())**2 + (edge.y2() - edge.y1())**2)
                unit_edge = (unit_edge[0] / unit_edge_mag,
                             unit_edge[1] / unit_edge_mag)
                unit_edge_perp = (-unit_edge[1], unit_edge[0])

                temp_tform = QTransform()
                temp_tform.translate(self.width()/2.0, self.height()/2.0)
                temp_tform.scale(1.0, -1.0)
                temp_tform.translate(scale*edge.x2(), scale*edge.y2())
                temp_tform.scale(1.0, -1.0)
                painter.setTransform(temp_tform)
                # painter.drawText( RECT, label[1], align )

                tri_pts = []
                tri_pts.append(QPointF(0, 0))
                tri_pts.append(QPointF(-arrow_scale*(2*unit_edge[0] + unit_edge_perp[0]),
                                       arrow_scale*(2*unit_edge[1] + unit_edge_perp[1])))
                tri_pts.append(QPointF(-arrow_scale*(2*unit_edge[0] - unit_edge_perp[0]),
                                       arrow_scale*(2*unit_edge[1] - unit_edge_perp[1])))
                tri = QPolygonF(tri_pts)
                b = painter.brush()
                painter.setBrush(c)
                painter.drawPolygon(tri)
                painter.setBrush(b)

        painter.setTransform(tform)
        font = QFont("Monospace")
        font.setStyleHint(QFont.TypeWriter)

        R = 1.0E3
        CITY_SIZE = 2.0  # DIAMETER
        RECT = QRectF(-R, -R, 2.0*R, 2.0*R)
        align = QTextOption(Qt.Alignment(Qt.AlignHCenter | Qt.AlignVCenter))
        for color in self.labelList:
            c = QColor(color[0], color[1], color[2])
            painter.setPen(c)
            for label in self.labelList[color]:
                temp_tform = QTransform()
                temp_tform.translate(self.width()/2.0, self.height()/2.0)
                temp_tform.scale(1.0, -1.0)
                pt = label[0]
                xoff = label[2]
                temp_tform.translate(scale*pt.x()+xoff, scale*pt.y())
                temp_tform.scale(1.0, -1.0)
                painter.setTransform(temp_tform)
                painter.drawText(RECT, label[1], align)

        painter.setTransform(tform)
        for color in self.pointList:
            c = QColor(color[0], color[1], color[2])
            painter.setPen(c)
            b = painter.brush()
            painter.setBrush(c)
            for point in self.pointList[color]:
                # pt = QPointF(w*point.x(), h*point.y())
                pt = QPointF(scale*point.x(), scale*point.y())
                painter.drawEllipse(pt, CITY_SIZE, CITY_SIZE)
            painter.setBrush(b)

        # if self.start_pt:
        #     painter.setPen(QPen(QColor(0.0, 255.0, 0.0), 2.0))
        #     pt = QPointF(scale*self.start_pt.x() - 0.0,
        #                  scale*self.start_pt.y() - 0.0)
        #     painter.drawEllipse(pt, 4.0, 4.0)

        # if self.end_pt:
        #     painter.setPen(QPen(QColor(255.0, 0.0, 0.0), 2.0))
        #     pt = QPointF(scale*self.end_pt.x() - 0.0,
        #                  scale*self.end_pt.y() - 0.0)
        #     painter.drawEllipse(pt, 4.0, 4.0)


class Proj5GUI(QMainWindow):

    def __init__(self):
        super().__init__()

        self.red_style = "background-color: rgb(255, 220, 220)"
        self.plain_style = "background-color: rgb(255, 255, 255)"
        self._max_seed = 1000

        self.graph_ready = False
        self._scenario = None
        self._solution = None
        self.init_ui()
        self.solver = TSPSolver()
        self.gen_params = {'size': None, 'seed': None, 'diff': None}

    def new_points(self):

        # if self.randBySeed.isChecked():
        seed = int(self.cur_seed.text())
        random.seed(seed)
        # else: # do by time
        #     random.seed(time.time())

        ptlist = []
        # unique_xvals = {}
        x_range = self.data_range['x']
        y_range = self.data_range['y']
        npoints = int(self.size.text())

        while len(ptlist) < npoints:
            rand_x = random.uniform(0.0, 1.0)
            rand_y = random.uniform(0.0, 1.0)
            # if (x ** 2) + (y ** 2) <= max_r ** 2:
            xval = x_range[0] + (x_range[1] - x_range[0]) * rand_x
            yval = y_range[0] + (y_range[1] - y_range[0]) * rand_y
            # if not xval in unique_xvals:
            ptlist.append(QPointF(xval, yval))
            # print('Appended ({},{})'.format(xval, yval))
            # unique_xvals[xval] = 1

        return ptlist

    # def clear_clicked(self):
        # print('clear_clicked')
        # self.view.clearEdges()
        # self.view.update()
        # self.solve_button.setEnabled(True)

    def generate_network(self):
        points = self.new_points()  # uses current rand seed
        diff = self.diff_drop_down.currentText()
        rand_seed = int(self.cur_seed.text())

        self._scenario = Scenario(
            city_locations=points, difficulty=diff, rand_seed=rand_seed
        )

        self.gen_params = {
            'size': self.size.text(),
            'seed': self.cur_seed.text(),
            'diff': diff
        }

        self.view.clearEdges()
        self.view.clearPoints()

        self.add_cities()

    def add_cities(self):
        # x_offset = 0.0375
        cities = self._scenario.get_cities()
        self.view.clearEdges()

        for city in cities:
            # self.view.addLabel(city.x_coord, city.y_coord, city.name)

            self.view.addLabel(
                QPointF(city.x_coord, city.y_coord), city.name,
                labelColor=(128, 128, 128), xoffset=10.0
            )

    def generate_clicked(self):
        # print('generate_clicked')
        if self._scenario:
            result = QMessageBox.question(
                self,
                'Overwrite Existing Scenario?',
                'Existing scenario will be overwritten.  Proceed?',
                QMessageBox.Yes | QMessageBox.No, QMessageBox.Yes
            )

            if result == QMessageBox.Yes:
                # self.view.clearPoints()
                # self.view.clearLines()
                self.generate_network()

                self.view.addPoints(
                    [QPointF(c.x_coord, c.y_coord)
                     for c in self._scenario.get_cities()],
                    (0, 0, 0)
                )

                self.view.update()
        else:
            self.generate_network()

            self.view.addPoints(
                [QPointF(c.x_coord, c.y_coord)
                 for c in self._scenario.get_cities()],
                (0, 0, 0)
            )

            self.view.update()

        # self.solver.reset()
        self.solve_button.setEnabled(True)
        self.graph_ready = True
        self.check_gen_inputs()
        # self.checkPathInputs()
        self.num_solutions.setText('--')
        self.tour_cost.setText('--')
        self.solved_in.setText('--')

    def display_solution(self):
        self.view.clearEdges()
        self.add_cities()

        # if array_path:
        #     cost = array_path['cost']

        # for start, end, lbl in array_path['path']:
        #     self.view.addEdge(
        #         start_pt=start, end_pt=end,
        #         label=lbl, edge_color=(128, 128, 255)
        #     )

        # self.array_time.setText('{:.6f}s'.format(array_time))

        # if not heap_path:
        #     self.heap_time.setText('')
        #     self.speedup.setText('')

        edges = self._solution.enumerate_edges()

        if edges:
            edge_color = (128, 128, 255)
            label_color = (64, 64, 255)

            for edge in edges:
                pt1, pt2, label = edge

                self.view.addEdge(
                    QPointF(pt1.x_coord, pt1.y_coord), QPointF(
                        pt2.x_coord, pt2.y_coord),
                    '{}'.format(label), edge_color, label_color
                )

        self.view.update()

    def rand_seed_clicked(self):
        new_seed = random.randint(0, self._max_seed - 1)
        self.cur_seed.setText('{}'.format(new_seed))

    def solve_clicked(self):
        self.solver.setup_with_scenario(self._scenario)

        max_time = float(self.time_limit.text())

        solve_func = 'self.solver.' + \
            self.ALGORITHMS[self.alg_drop_down.currentIndex()][1]

        results = eval(solve_func)(
            start_time=time.time(), time_allowance=max_time
        )

        # print(results)
        # print(results)

        if results:
            self.num_solutions.setText('{}'.format(results['count']))
            self.tour_cost.setText('{}'.format(results['cost']))
            self.solved_in.setText('{:6.6f} seconds'.format(results['time']))
            self._solution = results['soln']
            # print(self._solution)

            if self._solution:
                self.display_solution()
        else:
            print('GOT NULL SOLUTION BACK!!')

    # def _randbytime(self):
        # self.randSeed.setEnabled(False)

    # def _randbyseed(self):
        # self.randSeed.setEnabled(True)

    def check_gen_inputs(self):
        seed = self.cur_seed.text()
        # print(src)
        size = self.size.text()
        diff = self.diff_drop_down.currentText()
        # diff = self.diffDropDown.currentIndex()

        if self._scenario:
            if self.gen_params['seed'] == seed and \
               self.gen_params['size'] == size and \
               self.gen_params['diff'] == diff:
                self.generate_button.setEnabled(False)
                self.solve_button.setEnabled(True)
            elif (seed == '') or (size == ''):
                self.generate_button.setEnabled(False)
                self.solve_button.setEnabled(True)
            else:
                self.generate_button.setEnabled(True)
                self.solve_button.setEnabled(False)

    def check_input_value(self, widget, validrange):
        assert isinstance(widget, QLineEdit)
        retval = None
        valid = False

        try:
            sval = widget.text()
            # print('Found: \"{}\"'.format(sval))

            if sval == '':
                valid = True
            else:
                ival = int(sval)
                # print('INT: \"{}\"'.format(ival))

                if validrange:
                    # print('HASRANGE:{}'.format(validrange))
                    if ival >= validrange[0] and ival <= validrange[1]:
                        # print('INRANGE!!!')
                        retval = ival
                        valid = True
        except ValueError:
            # print('INVALID: \"{}\"!!!'.format(sval))
            pass

        if not valid:
            widget.setStyleSheet(self.red_style)
            # self.statusBar.showMessage('Invalid value!!!')
        else:
            widget.setStyleSheet('')

        return '' if retval is None else retval

    # def checkPathInputs(self):
        # print(src)

        # if not self.graphReady:
        #     self.computeCost.setEnabled(False)
        #     print(self.sourceNode.styleSheet())
        #     self.sourceNode.setStyleSheet('')
        #     self.sourceNode.setEnabled(False)
        #     self.targetNode.setStyleSheet('')
        #     self.targetNode.setEnabled(False)
#
        # else: # HAS GRAPH!!!
        #     self.sourceNode.setEnabled(True)
        #     self.targetNode.setEnabled(True)
        #     self.computeCost.setEnabled(False)
#
        # valid_inds = [1, int(self.genParams[1])]
        # points = self.graph.getNodes()

        # src  = self.checkInputValue(self.sourceNode, valid_inds)

        # if not src == '':
        #     self.view.setStartLoc(points[src - 1].loc)
        # else:
        #     self.view.setStartLoc(None)
#
        # dest = self.checkInputValue(self.targetNode, valid_inds)
        # if not dest == '':
        #     if src == dest:
        #         self.targetNode.setStyleSheet( self.RED_STYLE )
        #         self.view.setEndLoc( None )
        #     else:
        #         self.view.setEndLoc( points[dest-1].loc )
        # else:
        #     self.view.setEndLoc( None )

        # if ((not src == self.lastPath[0]) or (not dest == self.lastPath[1])) and \
        #     (not src == '') and (not dest == '') and (not src == dest):
        #     self.computeCost.setEnabled(True)

        # self.lastPath = (None,None)
        # self.statusBar.showMessage('')

    ALGORITHMS = [
        ('Default', 'default_random_tour'),
        ('Greedy', 'greedy'),
        ('Branch and Bound', 'branch_and_bound'),
        ('Fancy', 'fancy')
    ]

    def init_ui(self):
        self.setWindowTitle('Traveling Salesperson Problem')
        self.setWindowIcon(QIcon('icon312.png'))

        self.status_bar = QStatusBar()
        self.setStatusBar(self.status_bar)

        vbox = QVBoxLayout()
        boxwidget = QWidget()
        boxwidget.setLayout(vbox)
        self.setCentralWidget(boxwidget)

        scale = 1.0

        self.data_range = {
            'x': [-1.5 * scale, 1.5 * scale],
            'y': [-scale, scale]
        }

        self.view = PointLineView(
            self.status_bar,
            self.data_range
        )

        self.rand_seed_button = QPushButton('Randomize Seed')
        self.generate_button = QPushButton('Generate Scenario')
        # self.solve_button = QPushButton('Solve')
        self.solve_button = QPushButton('Solve TSP')
        # self.clear_button = QPushButton('Clear To Points')

        # self.useUnsorted = QRadioButton('Unsorted Array')
        # self.useHeap = QRadioButton('Min Heap')
        # self.useBoth = QRadioButton('Use Both')
        # self.arrayTime = QLineEdit('')
        # self.arrayTime.setFixedWidth(120)
        # self.arrayTime.setEnabled(False)
        # self.heapTime = QLineEdit('')
        # self.heapTime.setFixedWidth(120)
        # self.heapTime.setEnabled(False)
        # self.speedup = QLineEdit('')
        # self.speedup.setFixedWidth(200)
        # self.speedup.setEnabled(False)

        # self.randByTime = QRadioButton('Random Seed')
        # self.randBySeed = QRadioButton('Seed')
        self.cur_seed = QLineEdit('20')
        self.cur_seed.setFixedWidth(100)
        self.size = QLineEdit('5')
        self.size.setFixedWidth(50)
        self.time_limit = QLineEdit('60')
        self.time_limit.setFixedWidth(50)
        self.num_solutions = QLineEdit('--')
        self.num_solutions.setFixedWidth(100)
        self.tour_cost = QLineEdit('--')
        self.tour_cost.setFixedWidth(100)
        self.solved_in = QLineEdit('--')

        self.diff_drop_down = QComboBox(self)
        self.alg_drop_down = QComboBox(self)

        hbox = QHBoxLayout()
        hbox.addWidget(self.view)
        vbox.addLayout(hbox)

        hbox = QHBoxLayout()
        hbox.addWidget(QLabel('Problem Size: '))
        hbox.addWidget(self.size)
        hbox.addWidget(QLabel('Difficulty: '))
        hbox.addWidget(self.diff_drop_down)
        hbox.addWidget(QLabel('Current Seed: '))
        hbox.addWidget(self.cur_seed)
        hbox.addWidget(self.rand_seed_button)
        hbox.addWidget(self.generate_button)
        # hbox.addWidget(self.solve_button)
        hbox.addStretch(1)
        vbox.addLayout(hbox)
        # self.solve_button.setEnabled(False)

        # hbox.addWidget(self.clearButton)

        hbox = QHBoxLayout()
        hbox.addWidget(QLabel('Algorithm: '))
        hbox.addWidget(self.alg_drop_down)
        hbox.addWidget(QLabel('Time Limit'))
        hbox.addWidget(self.time_limit)
        hbox.addWidget(QLabel('seconds'))
        hbox.addWidget(self.solve_button)
        # self.totalCost.setEnabled(False)
        hbox.addStretch(1)
        vbox.addLayout(hbox)

        hbox = QHBoxLayout()
        hbox.addWidget(QLabel('# Solutions:'))
        hbox.addWidget(self.num_solutions)
        hbox.addWidget(QLabel('Cost of tour:'))
        hbox.addWidget(self.tour_cost)
        hbox.addWidget(QLabel('Solved in:'))
        hbox.addWidget(self.solved_in)
        self.num_solutions.setEnabled(False)
        self.tour_cost.setEnabled(False)
        self.solved_in.setEnabled(False)
        hbox.addStretch(1)
        vbox.addLayout(hbox)

        # self.randByTime.clicked.connect(self._randbytime)
        # self.randBySeed.clicked.connect(self._randbyseed)

        # self.randBySeed.setChecked(True)
        # self.generateClicked()

        self.last_path = (None, None)
        self.solve_button.setEnabled(False)
        # self.sourceNode.textChanged.connect(self.checkPathInputs)
        # self.targetNode.textChanged.connect(self.checkPathInputs)

        self.cur_seed.textChanged.connect(self.check_gen_inputs)
        self.size.textChanged.connect(self.check_gen_inputs)

        self.rand_seed_button.clicked.connect(self.rand_seed_clicked)
        self.generate_button.clicked.connect(self.generate_clicked)
        self.solve_button.clicked.connect(self.solve_clicked)

        # self.diffDropDown.addItem(' ')
        self.diff_drop_down.addItem('Easy')
        self.diff_drop_down.addItem('Normal')
        self.diff_drop_down.addItem('Hard')
        self.diff_drop_down.addItem('Hard (Deterministic)')
        # self.diffDropDown.setCurrentIndex(1)
        self.diff_drop_down.activated.connect(self.diff_changed)
        self.diff_drop_down.setCurrentIndex(3)
        self.diff_changed(3)  # to handle start state

        # self.algDropDown.addItem(' ')

        for alg in self.ALGORITHMS:
            self.alg_drop_down.addItem(alg[0])

        # self.algDropDown.setCurrentIndex(1)
        self.alg_drop_down.activated.connect(self.alg_changed)
        self.alg_drop_down.setCurrentIndex(2)
        self.alg_changed(2)  # to handle start state

        self.graph_ready = False
        # self.checkPathInputs()

        self.show()

    def diff_changed(self, text):
        #print('Difficulty changed: now {}'.format(text))
        self.check_gen_inputs()

    def alg_changed(self, text):
        #print('Algorithm changed: now {}'.format(text))
        pass


if __name__ == '__main__':
    # This line allows CNTL-C in the terminal to kill the program
    signal.signal(signal.SIGINT, signal.SIG_DFL)

    APP = QApplication(sys.argv)
    W = Proj5GUI()
    sys.exit(APP.exec())
