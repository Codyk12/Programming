#!/usr/bin/python3

import signal
import sys


'''
from PyQt4.QtGui import QApplication, QWidget
from PyQt4.QtGui import QHBoxLayout, QVBoxLayout
from PyQt4.QtGui import QIcon, QLabel, QPushButton, QLineEdit
'''
from PyQt5.QtWidgets import QApplication, QWidget
from PyQt5.QtGui import QIcon
from PyQt5.QtWidgets import QHBoxLayout, QVBoxLayout
from PyQt5.QtWidgets import QLabel, QPushButton, QLineEdit


# Import in the code that implements the actula primality testing
import fermat



class Proj1GUI( QWidget ):

    def __init__( self ):
        super().__init__()
        self.initUI()

    def initUI( self ):
        #self.setGeometry(300,300,300,220) # equiv to self.move() and selft.resize()
        self.setWindowTitle('Fermat\'s Primality Tester')
        self.setWindowIcon( QIcon('icon312.png') )

        # Now setup for project 1
        vbox = QVBoxLayout()
        self.setLayout( vbox )

        self.input_n = QLineEdit('312')
        self.input_k = QLineEdit('10')
        self.test    = QPushButton('Test Primality')
        self.output  = QLabel('<i>N is the number to test, K is how many random trials.</i>')
        self.output.setMinimumSize(250,0)

        # N
        h = QHBoxLayout()
        h.addWidget( QLabel( 'N: ' ) )
        h.addWidget( self.input_n )
        vbox.addLayout(h)
        
        # K
        h = QHBoxLayout()
        h.addWidget( QLabel( 'K: ' ) )
        h.addWidget( self.input_k )
        vbox.addLayout(h)

        # Test
        h = QHBoxLayout()
        h.addStretch(1)
        h.addWidget( self.test )
        vbox.addLayout(h)

        # Output
        h = QHBoxLayout()
        h.addWidget( self.output )
        vbox.addLayout(h)

        # When the Test button is clicked, call testClicked()
        self.test.clicked.connect(self.testClicked)
        # Do the same if enter is pressed in either input field
        self.input_n.returnPressed.connect(self.testClicked)
        self.input_k.returnPressed.connect(self.testClicked)

        self.show()

    def testClicked( self ):
        try:
            # Make sure inputs are valid integers
            n = int( self.input_n.text() )
            k = int( self.input_k.text() )

            result = fermat.prime_test(n, k)

            if result == 'prime':
                prob = fermat.probability(k)
                self.output.setText('<i>Result:</i> {:d} <b>is prime</b> with probability {:5.5f}.'.format(n, prob))
            elif result == 'carmichael':
                self.output.setText('<i>Result:</i> {:d} is a <b>Carmichael number</b>.'.format(n))
            else:  # Should be 'composite'
                self.output.setText('<i>Result:</i> {:d} is <b>not prime</b>.'.format(n))

        # If inputs not valid, display an error
        except Exception as e:
            self.output.setText('<i>ERROR:</i> inputs must be integers!')





if __name__ == '__main__':
    # This line allows CNTL-C in the terminal to kill the program
    signal.signal(signal.SIGINT, signal.SIG_DFL)
    
    app = QApplication(sys.argv)
    w = Proj1GUI()
    sys.exit(app.exec())
