# def getAngleUpper(self, leftHull, rightHull, rightmostIndex, leftmostIndex, addIndex, leftFixed):
#     print(len(rightHull))
#     if (leftFixed):
#         cur_angle = rightHull[leftmostIndex + addIndex % len(rightHull)].y() - leftHull[(rightmostIndex)].y() / \
#                                                                                rightHull[leftmostIndex + addIndex % len(
#                                                                                    rightHull)].x() - leftHull[
#                         (rightmostIndex)].x()
#     else:
#         cur_angle = rightHull[(leftmostIndex)].y() - leftHull[-(rightmostIndex + addIndex % len(leftHull))].y() / \
#                                                      rightHull[(leftmostIndex)].x() - leftHull[
#                         -(rightmostIndex + addIndex % len(leftHull))].x()
#
#     return cur_angle
#
#
# def getAngleLower(self, leftHull, rightHull, rightmostIndex, leftmostIndex, addIndex, leftFixed):
#     print(len(rightHull))
#     if (leftFixed):
#         cur_angle = (
#         (rightHull[int(-(leftmostIndex + addIndex) % len(rightHull))].y() - leftHull[int((rightmostIndex))].y()) /
#         rightHull[int(-(leftmostIndex + addIndex) % len(rightHull))].x() - leftHull[(int(rightmostIndex))].x())
#     else:
#         cur_angle = ((rightHull[int((leftmostIndex) % len(rightHull))].y() - leftHull[
#             int((rightmostIndex + addIndex) % len(leftHull))].y()) /
#                      rightHull[int((leftmostIndex) % len(rightHull))].x() - leftHull[
#                          int((rightmostIndex + addIndex) % len(leftHull))].x())
#
#     return cur_angle
#
#
# def getRightUpperTangent(self, leftHull, rightHull, initial):
#     print("----------------------UPPER RIGHT TANGENT----------------------")
#     print(leftHull)
#     print(rightHull)
#     leftmostIndex = self.findLeftMostPoint(rightHull)
#     if (initial == 0):
#         rightmostIndex = self.findRightMostPoint(leftHull)
#     else:
#         rightmostIndex = initial
#
#     cur_angle = self.getAngleUpper(leftHull, rightHull, rightmostIndex, leftmostIndex, 0, True)
#     j = leftmostIndex
#     print("cur", cur_angle)
#     for i in range(len(rightHull)):
#         next_angle = self.getAngleUpper(leftHull, rightHull, rightmostIndex, leftmostIndex, i, True)
#         print("next", next_angle)
#         if (next_angle < cur_angle):
#             break
#         cur_angle = next_angle
#         j = i
#     return j
#
#
# def getRightLowerTangent(self, leftHull, rightHull, initial):
#     print("----------------------LOWER RIGHT TANGENT----------------------")
#     print(leftHull)
#     print(rightHull)
#     leftmostIndex = self.findLeftMostPoint(rightHull)
#     if (initial == 0):
#         rightmostIndex = self.findRightMostPoint(leftHull)
#     else:
#         rightmostIndex = initial
#
#     cur_angle = self.getAngleLower(leftHull, rightHull, rightmostIndex, leftmostIndex, 0, True)
#     j = leftmostIndex
#     print("cur", cur_angle)
#     for i in range(len(rightHull)):
#         next_angle = self.getAngleLower(leftHull, rightHull, rightmostIndex, leftmostIndex, i, True)
#         print("next", next_angle)
#         if (next_angle > cur_angle):
#             break
#         cur_angle = next_angle
#         j = i
#     return j
#
#
# def getLeftUpperTangent(self, leftHull, rightHull, initial):
#     print("----------------------UPPER LEFT TANGENT----------------------")
#     print(leftHull)
#     print(rightHull)
#     rightmostIndex = self.findRightMostPoint(leftHull)
#     if (initial == 0):
#         leftmostIndex = self.findLeftMostPoint(rightHull)
#     else:
#         leftmostIndex = initial
#
#     cur_angle = self.getAngleUpper(leftHull, rightHull, rightmostIndex, leftmostIndex, 0, False)
#     j = rightmostIndex
#     print("cur", cur_angle)
#     for i in range(len(rightHull)):
#         print("i", i)
#         next_angle = self.getAngleUpper(leftHull, rightHull, rightmostIndex, leftmostIndex, i, False)
#         print("next", next_angle)
#         if (next_angle > cur_angle):
#             break
#         cur_angle = next_angle
#         j = i
#     return j
#
#
# def getLeftLowerTangent(self, leftHull, rightHull, initial):
#     print("----------------------LOWER LEFT TANGENT----------------------")
#     print(leftHull)
#     print(rightHull)
#
#     rightmostIndex = self.findRightMostPoint(leftHull)
#     if (initial == 0):
#         leftmostIndex = self.findLeftMostPoint(rightHull)
#     else:
#         leftmostIndex = initial
#
#     cur_angle = self.getAngleLower(leftHull, rightHull, rightmostIndex, leftmostIndex, 0, False)
#     j = rightmostIndex
#     print("cur", cur_angle)
#     for i in range(len(rightHull)):
#         next_angle = self.getAngleLower(leftHull, rightHull, rightmostIndex, leftmostIndex, i, False)
#         print("next", next_angle)
#         if (next_angle < cur_angle):
#             break
#         cur_angle = next_angle
#         j = i
#     return j