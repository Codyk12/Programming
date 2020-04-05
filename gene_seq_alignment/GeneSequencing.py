#!/usr/bin/python3

from which_pyqt import PYQT_VER
if PYQT_VER == 'PYQT5':
    from PyQt5.QtCore import QLineF, QPointF
elif PYQT_VER == 'PYQT4':
    from PyQt4.QtCore import QLineF, QPointF
else:
    raise Exception('Unsupported Version of PyQt: {}'.format(PYQT_VER))




import time



class GeneSequencing:
    def __init__( self ):
        pass

    def align_all( self, sequences, banded, align_length ):
        results = []
        for i in range(len(sequences)):
            jresults = []
            sequ_i = sequences[i]
            # print(sequ_i)
            sequ_i_len = len(sequ_i)
            for j in range(0, len(sequences)):
                sequ_j = sequences[j]
                # print(sequ_j)
                sequ_j_len = len(sequ_j)
                if(i == j):
                    s = {'align_cost': max(-3*align_length,-3*align_length),
                         'seqi_first100': 'abc-easy  DEBUG:(seq{}, {} chars,align_len={}{})'.format(i + 1,
                                                                                                    len(sequences[i]),
                                                                                                    align_length,
                                                                                                    ',BANDED' if banded else ''),
                         'seqj_first100': 'as-123--  DEBUG:(seq{}, {} chars,align_len={}{})'.format(j + 1,
                                                                                                    len(sequences[j]),
                                                                                                    align_length,
                                                                                                    ',BANDED' if banded else '')}
                elif((i == 0 and j != 1) or (i == 1 and j != 0)):
                    s = {'align_cost': float('inf'),
                         'seqi_first100': 'abc-easy  DEBUG:(seq{}, {} chars,align_len={}{})'.format(i + 1,
                                                                                                    len(sequences[i]),
                                                                                                    align_length,
                                                                                                    ',BANDED' if banded else ''),
                         'seqj_first100': 'as-123--  DEBUG:(seq{}, {} chars,align_len={}{})'.format(j + 1,
                                                                                                    len(sequences[j]),
                                                                                                    align_length,
                                                                                                    ',BANDED' if banded else '')}
                elif(i > j):
                    s = {'align_cost': 0,
                         'seqi_first100': 'abc-easy  DEBUG:(seq{}, {} chars,align_len={}{})'.format(i + 1,
                                                                                                    len(sequences[i]),
                                                                                                    align_length,
                                                                                                    ',BANDED' if banded else ''),
                         'seqj_first100': 'as-123--  DEBUG:(seq{}, {} chars,align_len={}{})'.format(j + 1,
                                                                                                    len(sequences[j]),
                                                                                                    align_length,
                                                                                                    ',BANDED' if banded else '')}
                else:
                    # Here is the algorithm Code

                    if(align_length > sequ_i_len):
                        if(align_length > sequ_j_len):
                            dist_matrix = [[0 for col in range(sequ_i_len + 1)] for row in range(sequ_j_len + 1)]
                            path_matrix = [[("") for col in range(sequ_i_len + 1)] for row in range(sequ_j_len + 1)]
                    else:
                        dist_matrix = [[0 for col in range(align_length + 1)] for row in range(align_length + 1)]
                        path_matrix = [[("") for col in range(align_length + 1)] for row in range(align_length + 1)]

                    for m in range(len(dist_matrix[0])):
                        dist_matrix[0][m] = m * 5
                        path_matrix[0][m] = 'r'

                    for m in range(len(dist_matrix)):
                        dist_matrix[m][0] = m * 5
                        path_matrix[m][0] = 'a'

                    path_matrix[0][0]= ""

                    for k in range(1, len(dist_matrix)):
                        for l in range(1, len(dist_matrix[0])):
                            right_of = dist_matrix[k - 1][l] + 5
                            above = dist_matrix[k][l - 1] + 5
                            diagonal = dist_matrix[k - 1][l - 1] + self.diff(sequ_i, sequ_j, k-1, l-1)
                            mininum = min(right_of, above, diagonal)
                            dist_matrix[k][l] = mininum
                            if(mininum == right_of):
                               path_matrix[k][l] = ("r")
                            elif(mininum == above):
                                path_matrix[k][l] = ( "a")
                            elif(mininum == diagonal):
                                path_matrix[k][l] = ("d")
                    alignment = self.extract_align(path_matrix, sequ_i, sequ_j)
                    s = {'align_cost': dist_matrix[-1][-1],
                         'seqi_first100': alignment[0] + ' | DEBUG:(seq{}, {} chars,align_len={}{})'.format(i + 1,
                                                                                                    len(sequences[i]),
                                                                                                    align_length,
                                                                                                    ',BANDED' if banded else ''),
                         'seqj_first100': alignment[1] + ' | DEBUG:(seq{}, {} chars,align_len={}{})'.format(j + 1,
                                                                                                    len(sequences[j]),
                                                                                                    align_length,
                                                                                                    ',BANDED' if banded else '')}
                jresults.append(s)

            results.append(jresults)
        # print(results)
        return results


    def diff(self, sequ_i, sequ_j, k, l):
        # print(k, l)
        # print(sequ_i[l], sequ_j[k])
        if(sequ_i[l] == sequ_j[k]):
            return -3
        else:
            return 1

    def extract_align(self, path_matrix, sequ_i, sequ_j):
        return_i = ""
        return_j = ""
        path = ""
        j = -1
        k = -1
        while(path_matrix[j][k] != ''):
            path += path_matrix[j][k]
            if(path_matrix[j][k] == 'd'):
                j = j - 1
                k = k - 1
            elif(path_matrix[j][k] == 'a'):
                j = j - 1
            elif(path_matrix[j][k] == 'r'):
                k = k - 1

        path = path[::-1]
        i_spot = 0
        j_spot = 0
        for i in range(len(path)):
            if (path[i] == 'a'):
                return_i += "-"
            else:
                return_i += sequ_i[i_spot]
                i_spot += 1

            if (path[i] == 'r'):
                return_j += "-"
            else:
                return_j += sequ_j[j_spot]
                j_spot += 1

        return (return_i, return_j)
